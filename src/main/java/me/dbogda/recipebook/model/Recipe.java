package me.dbogda.recipebook.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Recipe {
    private  String name;
    private int cookingTime;
    private HashMap<Integer, Ingredient> ingredients;
    private List<String> steps;
}
