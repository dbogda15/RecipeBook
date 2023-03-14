package me.dbogda.recipebook.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.dbogda.recipebook.model.Ingredient;
import me.dbogda.recipebook.service.IngredientsService;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.TreeMap;

@org.springframework.stereotype.Service
public class IngredientsServiceImpl implements IngredientsService {
    private final IngredientFileServiceImpl fileService;
    public static TreeMap<Integer, Ingredient> ingredients = new TreeMap<>();
    private Integer id = 0;

    public IngredientsServiceImpl(IngredientFileServiceImpl fileService) {
        this.fileService = fileService;
    }

    @PostConstruct
    private void init(){
        fileService.readFromFile();
    }

    @Override
    public int putIngredients(Ingredient ingredient) {
        if (ingredient != null) {
            ingredients.put(id, ingredient);
            saveToFile();
        }
        return id++;
    }

    @Override
    public Ingredient getIngredientByID(Integer id) {
        if (ingredients.get(id) == null) {
            return null;
        }
        return ingredients.get(id);
    }

    @Override
    public Ingredient editIngredient(int id, Ingredient ingredient) {
        if (ingredients.containsKey(id)) {
            ingredients.put(id, ingredient);
            saveToFile();
            return ingredient;
        }
        return null;
    }

    @Override
    public String deleteIngredient(int id) {
        if (ingredients.containsKey(id)) {
            ingredients.remove(id);
            return "ingredient with id = " + id + " has been deleted";
        }
        return "There is not ingredients with id= " + id;
    }

    @Override
    public Map<Integer, Ingredient> getAllIngredients() {
        readFromFile();
        return ingredients;
    }


    private void saveToFile(){
        try {
            String json = new ObjectMapper().writeValueAsString(ingredients);
            fileService.saveToFile(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    private void readFromFile (){
        try {
            String json = fileService.readFromFile();
            ingredients = new ObjectMapper().readValue(json, new TypeReference<TreeMap<Integer, Ingredient>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}






