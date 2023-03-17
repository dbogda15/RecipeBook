package me.dbogda.recipebook.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.dbogda.recipebook.service.impl.IngredientFileServiceImpl;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
@RequestMapping("/ingredients/file")
@Tag(name = "Работа с файлами с ингридиентами", description = "Здесь можно загрузить и скачать файлы с ингридиентами")
public class IngredientsFileController {

    private final IngredientFileServiceImpl ingredientFileService;

    public IngredientsFileController(IngredientFileServiceImpl ingredientFileService) {
        this.ingredientFileService = ingredientFileService;
    }


    @Operation(summary = "Скачать файл с ингридиентами")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Файл загружен на ваш компьютер"
            )
    })
    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> downloadIngredientsFile () throws FileNotFoundException {
        File file = ingredientFileService.getDataFile();
        if(file.exists()) {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .contentLength(file.length())
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Ingredients.json\"")
                    .body(resource);
        } else {
            return ResponseEntity.noContent().build();
        }
    }


    @Operation(summary = "Импорт файла с ингридиентами")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Файл загружен в программу"
            )
    })
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadIngredientsFile(@RequestParam MultipartFile file){
        ingredientFileService.cleanDataFile();
        File ingredientsFile = ingredientFileService.getDataFile();
        try (FileOutputStream fos = new FileOutputStream(ingredientsFile)){
            IOUtils.copy(file.getInputStream(), fos);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
