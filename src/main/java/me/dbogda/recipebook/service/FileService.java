package me.dbogda.recipebook.service;

import org.springframework.stereotype.Service;

@Service
public interface FileService {
    boolean saveToFile(String json);

    boolean cleanDataFile();

    String readFromFile();

}
