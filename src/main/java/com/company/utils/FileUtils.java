package com.company.utils;


import com.company.exceptions.FileNotCreatedException;
import com.company.exceptions.FileNotDeletedException;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;

import static com.company.Constants.DB_BASE_DIR;
import static com.company.enums.Errors.TABLE_NOT_FOUND;

public class FileUtils {

    public static void createCSVFile(String name) throws IOException {
        createFile(name, "csv");
    }

    public static void createMetaFile(String name) throws IOException {
        createFile(name + "_META", "txt");
    }

    private static void createFile(String name, String type) throws IOException {
        File file = new File(String.format(DB_BASE_DIR + "%s.%s", name, type));
        if (!file.exists()) {
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else
                throw new FileNotCreatedException();
        } else {
            throw new FileAlreadyExistsException(file.getName());
        }
    }

    public static void dropFile(String fileName) throws FileNotFoundException, FileNotDeletedException {
        File directory = new File("src/main/resources/database/");
        File[] matches = directory.listFiles((dir, name) -> name.startsWith(fileName));
        if (matches == null) {
            throw new FileNotFoundException();
        }
        for (File match : matches) {
            if (!match.delete()) {
                throw new FileNotDeletedException(TABLE_NOT_FOUND.getValue());
            }
        }
    }
}
