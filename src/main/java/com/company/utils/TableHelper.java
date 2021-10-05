package com.company.utils;

import com.company.enums.DataTypes;
import com.company.exceptions.FileNotCreatedException;
import com.company.exceptions.FileNotDeletedException;
import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.company.Constants.DB_BASE_DIR;
import static com.company.enums.Errors.COULD_NOT_PERFORM_OPERATION;
import static com.company.enums.Errors.TABLE_NOT_FOUND;

public class TableHelper {

    private static volatile TableHelper tableHelper;
    private String tableName;

    private TableHelper() {
    }

    public static TableHelper getTable(String tableName) {
        if(tableHelper == null) {
            synchronized (TableHelper .class) {
                if(tableHelper == null) {
                    tableHelper = new TableHelper();
                }
            }
        }
        tableHelper.tableName = tableName;
        return tableHelper;
    }

    public void create(List<Pair<String, DataTypes>> column) {
        try {
            FileUtils.createCSVFile(tableName);
            FileUtils.createMetaFile(tableName);
            if(column == null) {
                return;
            }
            for (Pair<String, DataTypes> col : column) {
                attachColumns(col);
            }
        } catch (FileAlreadyExistsException e) {
            System.out.println("Table Already exists");
        } catch (FileNotCreatedException | IOException e) {
            System.out.println(COULD_NOT_PERFORM_OPERATION.getValue());
        }
    }

    public void create() {
       create(null);
    }

    public void drop() {
        try{
            FileUtils.dropFile(tableName);
            System.out.println("Table Successfully deleted");
        } catch (FileNotFoundException e) {
            System.out.println(TABLE_NOT_FOUND.getValue());
        } catch (FileNotDeletedException e) {
            System.out.println(COULD_NOT_PERFORM_OPERATION.getValue());
        }
    }

    public void attachColumns(Pair<String, DataTypes> col) {
        File file = new File(String.format(DB_BASE_DIR+"%s.csv", tableName));
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.append(String.format("%s;",col.getLeft()));
            System.out.println("Added column: " + col.getLeft());

            File metaFile = new File(String.format(DB_BASE_DIR+"%s_META.txt", tableName));
            addColumnMetaInfo(metaFile, col);
        } catch (IOException e) {
            System.out.println(COULD_NOT_PERFORM_OPERATION.getValue());
        }
    }

    public void attachColumns(List<Pair<String, DataTypes>> columns) {
        for (Pair<String, DataTypes> col : columns) {
            attachColumns(col);
        }
    }

    public void modifyColumn(List<Pair<String, DataTypes>> columns) {
        for (Pair<String, DataTypes> col : columns) {
            updateColumnMetaInfo(col);
        }
    }

    private void addColumnMetaInfo(File metaFile, Pair<String, DataTypes> col) {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(metaFile, true))) {
            writer.write(String.format("%s:%s\n",col.getLeft(), col.getRight().toString()));
        } catch (IOException e) {
            System.out.println(COULD_NOT_PERFORM_OPERATION.getValue());
        }
    }

    private void updateColumnMetaInfo(Pair<String, DataTypes> col) {
        File metaFile = new File(String.format(DB_BASE_DIR+ "%s_META.txt", tableName));

        try (Stream<String> lines = Files.lines(metaFile.toPath())) {
            List<String> replaced = lines.map(line -> line.replace(line.substring(line.indexOf(":") + 1), col.getRight().toString())).collect(Collectors.toList());

            Files.write(metaFile.toPath(), replaced);
            System.out.println("Column type changed");

        } catch (IOException e) {
            System.out.println(COULD_NOT_PERFORM_OPERATION.getValue());
        }
    }
}
