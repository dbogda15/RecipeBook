package me.dbogda.recipebook.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class FirstController {

    @GetMapping("/")
    public String helloUser() {
        return "Приложение запущено";
    }

    @GetMapping("/info")
    public String nextPage () {
        String studentName = "Diana Bogdanova";
        String projectName = "Recipe book";
        LocalDate creationDate = LocalDate.of(2023,2,23);
        String projectInfo = "The main reason for creating this app is to collect the best recipes in one place";
        return "Student name: " + studentName  + ". Project name: " + projectName + ". Date of creation: " +
                creationDate.getDayOfMonth() + " " + creationDate.getMonth() + " "
                + creationDate.getYear() + ". Info about this project: " + projectInfo ;
    }
}
