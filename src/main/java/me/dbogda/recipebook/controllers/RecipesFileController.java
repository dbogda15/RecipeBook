package me.dbogda.recipebook.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.dbogda.recipebook.service.impl.RecipeFileServiceImpl;
import me.dbogda.recipebook.service.impl.RecipeServiceImpl;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/recipes/file")
@Tag(name = "Работа с файлами c рецептами", description = "Здесь можно загрузить и скачать файлы с рецептами")
public class RecipesFileController {
    private final RecipeFileServiceImpl recipeFileService;
    private final RecipeServiceImpl recipeService;

    public RecipesFileController(RecipeFileServiceImpl recipeFileService, RecipeServiceImpl recipeService) {
        this.recipeFileService = recipeFileService;
        this.recipeService = recipeService;
    }


    @GetMapping("/export/json")
    @Operation(summary = "Скачать JSON файл с рецептами")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Файл загружен на ваш компьютер"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "есть ошибка в параметрах запроса"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "URL неверный или такого действия нет в веб-приложении"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "во время выполнения запроса произошла ошибка на сервере"
            )
    })
    public ResponseEntity<InputStreamResource> downloadRecipesJsonFile() throws FileNotFoundException {
        File file = recipeFileService.getDataFile();
        if (file.exists()) {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .contentLength(file.length())
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Recipes.txt\"")
                    .body(resource);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Импорт файла с рецептами")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Файл загружен в программу"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "есть ошибка в параметрах запроса"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "URL неверный или такого действия нет в веб-приложении"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "во время выполнения запроса произошла ошибка на сервере"
            )
    })
    public ResponseEntity<Void> uploadRecipesFile(@RequestParam MultipartFile file) {
        recipeFileService.cleanDataFile();
        File recipesFile = recipeFileService.getDataFile();
        try (FileOutputStream fos = new FileOutputStream(recipesFile)) {
            IOUtils.copy(file.getInputStream(), fos);
            return ResponseEntity.ok().build();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/export/txt")
    @Operation(summary = "Скачать файл со всеми рецептами в текстовом формате")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "файл загружен на ваш компьютер"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "есть ошибка в параметрах запроса"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "URL неверный или такого действия нет в веб-приложении"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "во время выполнения запроса произошла ошибка на сервере"
            )
    })
    public ResponseEntity<Object> downloadRecipesTxtFile() {
        try {
            Path path = recipeService.createRecipesCollection();
            if (Files.size(path) == 0) {
                return ResponseEntity.noContent().build();
            }

            InputStreamResource resource = new InputStreamResource(new FileInputStream(path.toFile()));
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .contentLength(Files.size(path))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"RecipesCollection.txt\"")
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.toString());
        }
    }
}
