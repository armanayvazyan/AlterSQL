package com.company.utils;


import com.company.exceptions.FileNotCreatedException;
import com.company.exceptions.FileNotDeletedException;
import lombok.SneakyThrows;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.company.Constants.DB_BASE_DIR;
import static com.company.enums.Errors.TABLE_NOT_FOUND;

public class FileUtils {

    @SneakyThrows
    public static Long getIDCursor(String filename) {
        BufferedReader input = new BufferedReader(new FileReader("src/main/resources/database/" + filename + ".csv"));
        String line;
        String id= "";

        while ((line = input.readLine()) != null) {
            id = line.substring(0, line.indexOf(";"));
        }
        return Long.parseLong(id);
    }
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

    public static void clearFileContent(String fileName) throws IOException {
        FileChannel.open(Paths.get("src/main/resources/database/" + fileName + ".csv"), StandardOpenOption.WRITE).truncate(0).close();
    }

    public static String getMETAFileContent(String fileName) throws IOException {
        Path path = Paths.get("src/main/resources/database/" + fileName + "_META.txt");
        Stream<String> lines = Files.lines(path);
        String data = lines.collect(Collectors.joining("\n"));
        lines.close();
        return data;
    }

    public static void insertDataIntoFile(String fileName, List<String> data) throws IOException {
        File file = new File(String.format(DB_BASE_DIR+"%s.csv", fileName));
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
        for (int i = 0; i < data.size(); i++) {
            if(i == data.size() - 1){
                writer.append(String.format("%s", data.get(i)));
            } else{
                writer.append(String.format("%s;", data.get(i)));
            }
        }
        writer.append("\n");
        writer.close();
    }

    public static String getLineFromFile(String fileName, int index) throws IOException {
        BufferedReader input = new BufferedReader(new FileReader("src/main/resources/database/" + fileName + ".csv"));
        String line;
        int ind = 0;
        while ((line = input.readLine()) != null) {
            if(ind == index)
                return line;
            ++ind;
        }
        return null;
    }

    public static void updateFileData(String fileName, List<String> data, int index, String conditionValue) throws IOException {
        File file = new File(String.format(DB_BASE_DIR+"%s.csv", fileName));

        BufferedReader input = new BufferedReader(new FileReader(file));
        String line;
        int lineCursor= 0;
        while ((line = input.readLine()) != null) {
            String[] rowData = line.split(";");
            if(rowData[index].equals(conditionValue)) {

                List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
                data.set(0, rowData[0]);
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < data.size(); i++) {
                    if(i == data.size() - 1){
                        builder.append(String.format("%s", data.get(i)));
                    } else{
                        builder.append(String.format("%s;", data.get(i)));
                    }
                }
                lines.set(lineCursor, builder.toString());
                Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
            }
            ++lineCursor;
        }
    }
}
