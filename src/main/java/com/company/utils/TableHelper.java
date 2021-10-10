package com.company.utils;

import com.company.enums.DataTypes;
import com.company.exceptions.FileNotCreatedException;
import com.company.exceptions.FileNotDeletedException;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Pair;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.company.Constants.DB_BASE_DIR;
import static com.company.enums.Errors.*;

public class TableHelper {

    private static volatile TableHelper tableHelper;
    private String tableName;
    private long cursorID;

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
        tableHelper.cursorID = FileUtils.getIDCursor(tableName);
        return tableHelper;
    }

    public void create(List<Pair<String, DataTypes>> column) {
        try {
            FileUtils.createCSVFile(tableName);
            FileUtils.createMetaFile(tableName);
            if(column == null) {
                return;
            }
            attachColumns(Pair.of("ID", DataTypes.NUMBER));
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

    public void truncate() {
        try {
            FileUtils.clearFileContent(tableName);
        } catch (IOException e) {
            System.out.println(COULD_NOT_PERFORM_OPERATION.getValue());
        }
    }

    public List<String> getColumns() {
        try {
            return Arrays.stream(FileUtils.getMETAFileContent(tableName).split("\n"))
                    .map(s -> s.substring(0, s.indexOf(":")))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println(COULD_NOT_PERFORM_OPERATION.getValue());
        }
        return null;
    }

    public void insertData(List<String> datas) {
        try {
            datas.set(0, String.valueOf(++cursorID));
            FileUtils.insertDataIntoFile(tableName, datas);
        } catch (IOException e) {
            System.out.println(COULD_NOT_PERFORM_OPERATION.getValue());
        }
    }

    public void updateData(List<String> datas, Condition condition) {
        Integer columnIndex = TableHelper.getTable(tableName).getColumnIndex(condition.getName());

        datas.forEach(e -> {
            if(!isColumnNameValid(e.substring(0, e.indexOf("=")))) {
                System.out.println(COLUMN_NOT_FOUND.getValue() + ":  " + e.substring(0, e.indexOf("=")));
            }
        });
        List<String> row = getRow(columnIndex);

        for (int i = 0; i < datas.size(); i++) {
            String columnName = datas.get(i).substring(0, datas.get(i).indexOf("="));
            String columnValue = datas.get(i).substring(datas.get(i).indexOf("=") + 1);
            row.set(getColumnIndex(columnName), columnValue);
        }

        try {
            FileUtils.updateFileData(tableName, row, columnIndex, condition.getValue());
        } catch (IOException e) {
            System.out.println(COULD_NOT_PERFORM_OPERATION.getValue());
        }
    }

    public DataTypes getColumnType(String columnName) {
        try {
            return DataTypes.parse(Arrays.stream(FileUtils.getMETAFileContent(tableName).split("\n"))
                    .filter(s -> s.contains(columnName))
                    .map(s -> s.substring(s.indexOf(":") + 1))
                    .findAny()
                    .orElseThrow(IOException::new));
        } catch (IOException e) {
            System.out.println(COULD_NOT_PERFORM_OPERATION.getValue());
        }
        return null;
    }

    public Integer getColumnIndex(String columnName) {
        try {
            String[] cols = FileUtils.getMETAFileContent(tableName).split("\n");
            for (int i = 0; i < cols.length; i++) {
                if(cols[i].substring(0, cols[i].indexOf(":")).equals(columnName)) {
                    return i;
                }
            }

        } catch (IOException e) {
            System.out.println(COULD_NOT_PERFORM_OPERATION.getValue());
        }
        return null;
    }

    public List<String> getRow(int index) {
        List<String> rowData = new ArrayList<>();
        try {
            String[] strings = Objects.requireNonNull(FileUtils.getLineFromFile(tableName, index)).split(";");
            rowData.addAll(Arrays.asList(strings));
        } catch (IOException e) {
            System.out.println(COULD_NOT_PERFORM_OPERATION.getValue());
        }
        return rowData;
    }

    public String getColumnValue(int index) {
        try {
            String[] cols = FileUtils.getMETAFileContent(tableName).split("\n");
            return cols[index].substring(0, cols[index].indexOf(":"));

        } catch (IOException e) {
            System.out.println(COULD_NOT_PERFORM_OPERATION.getValue());
        }
        return null;
    }

    public boolean isColumnNameValid(String col) {
        try {
            String[] cols = FileUtils.getMETAFileContent(tableName).split("\n");
            for (String s : cols) {
                if (s.substring(0, s.indexOf(":")).equals(col)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println(COULD_NOT_PERFORM_OPERATION.getValue());
        }
        return false;
    }

    @SneakyThrows
    public int getColumnsCount() {
        String[] cols = FileUtils.getMETAFileContent(tableName).split("\n");
        return cols.length;
    }

    public void attachColumns(Pair<String, DataTypes> col) {
        File metaFile = new File(String.format(DB_BASE_DIR+"%s_META.txt", tableName));
        addColumnMetaInfo(metaFile, col);
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
