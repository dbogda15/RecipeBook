package me.dbogda.recipebook.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.dbogda.recipebook.model.Recipe;
import me.dbogda.recipebook.service.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/recipe")
@Tag(name = "Книга рецептов", description = "Приложение для добавления и редактирования рецептов")
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController (RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping("/add")
    @Operation(summary = "Добавление нового рецепта")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Новый рецепт добавлен"
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
    public ResponseEntity <Integer> postRecipe (@RequestBody Recipe recipe) {
        if (recipe==null) {
            return ResponseEntity.notFound().build();
        }
        int id = recipeService.putRecipe(recipe);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Поиск и распечатка рецепта по id")
    @Parameters(value = {
            @Parameter(name = "id", example = "1")
    })
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Рецепт найден",
                    content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Recipe.class))
                    )
                    }
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
    public ResponseEntity<String> getRecipe (@PathVariable int id) {
        Recipe recipe = recipeService.getRecipeByID(id);
        if (recipe == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Удаление рецепта по id")
    @Parameters(value = {
            @Parameter(name = "id", example = "1")
    })
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Рецепт удалён",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Recipe.class))
                            )
                    }
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
    public ResponseEntity<String> deleteRecipe (@PathVariable int id) {
        String message = recipeService.deleteRecipe(id);
        if (message == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(message);
    }

    @PutMapping("/edit/{id}")
    @Operation(summary = "Редактирование рецепта")
    @Parameters(value = {
            @Parameter(name = "id", example = "1")
    })
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Рецепт был изменён",
                    content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Recipe.class))
                    )

            }),
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
    public ResponseEntity<Recipe> editRecipe (@PathVariable int id, @RequestBody Recipe recipe) {
        Recipe editRecipe = recipeService.editRecipe(id, recipe);
        if (editRecipe==null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(editRecipe);
    }

    @GetMapping("/print")
    @Operation(summary = "Распечатать все рецепты")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Все рецепты найдены",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Recipe.class))
                            )
                    }
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
    public ResponseEntity<Map<Integer, Recipe>> getAllRecipes (){
        Map<Integer, Recipe> allRecipesMap = recipeService.getAllRecipes();
        if (allRecipesMap.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(allRecipesMap);
    }
}
