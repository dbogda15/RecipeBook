package me.dbogda.recipebook.service;

import org.springframework.stereotype.Service;

import java.io.File;

@Service
public interface FileService {
    boolean saveToFile(String json);

    boolean cleanDataFile();

    String readFromFile();

    File getDataFile();
}
