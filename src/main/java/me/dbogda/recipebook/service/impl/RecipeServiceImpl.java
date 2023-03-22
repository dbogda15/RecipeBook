package me.dbogda.recipebook.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.dbogda.recipebook.model.Recipe;
import me.dbogda.recipebook.service.RecipeService;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

@org.springframework.stereotype.Service
public class RecipeServiceImpl implements RecipeService {
    private final RecipeFileServiceImpl fileService;
    private HashMap<Integer, Recipe> recipeMap = new HashMap<>();

    private static Integer id = 0;

    public RecipeServiceImpl(RecipeFileServiceImpl fileService) {
        this.fileService = fileService;
    }

    @PostConstruct
    private void init() {
        File file = fileService.getDataFile();
        if (file.exists()) {
            fileService.readFromFile();
        }
    }

    @Override
    public int putRecipe(Recipe recipe) {
        if (recipe != null) {
            recipeMap.put(id, recipe);
            saveToFile();
        }
        return id++;
    }

    @Override
    public Recipe getRecipeByID(Integer id) {
        if (recipeMap.get(id) == null) {
            return null;
        }
        return recipeMap.get(id);
    }

    @Override
    public String deleteRecipe(int id) {
        if (recipeMap.containsKey(id)) {
            recipeMap.remove(id);
            saveToFile();
            return "ingredient with id = " + id + " has been deleted";
        }
        return "There is not ingredients with id= " + id;
    }

    @Override
    public Recipe editRecipe(int id, Recipe recipe) {
        if (recipeMap.containsKey(id)) {
            recipeMap.put(id, recipe);
            saveToFile();
            return recipe;
        }
        return null;
    }

    @Override
    public Map<Integer, Recipe> getAllRecipes() {
        return recipeMap;
    }

    public Path createRecipesCollection() throws IOException {
        Path path = fileService.createTempFile("recipesCollection");

        for (Recipe recipe : recipeMap.values()) {
            try (Writer writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
                writer.append(recipe.getName());
                writer.append("\n");
                writer.append("Время приготовления: ").append(String.valueOf(recipe.getCookingTime())).append(" минут.");
                writer.append("\n");

                writer.append("Ингредиенты:");
                writer.append("\n");
                recipe.getIngredients().forEach((integer, ingredient) -> {
                    try {
                        writer.append(ingredient.getName()).append(" ").append(String.valueOf(ingredient.getQuantity())).append(" ").append(ingredient.getUnit());
                        writer.append("\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                writer.append("Инструкция приготовления:");
                recipe.getSteps().forEach(step -> {
                    try {
                        writer.append(step);
                        writer.append("\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                writer.append("\n");
            }
        }
        return path;
    }

    private void saveToFile() {
        try {
            String json = new ObjectMapper().writeValueAsString(recipeMap);
            fileService.saveToFile(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void readFromFile() {
        try {
            String json = fileService.readFromFile();
            recipeMap = new ObjectMapper().readValue(json, new TypeReference<HashMap<Integer, Recipe>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

