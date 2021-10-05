package com.company;

import com.company.enums.DataTypes;
import com.company.utils.TableHelper;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Scanner;

import static com.company.enums.Errors.WRONG_COMMAND;
import static com.company.utils.CommandUtils.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Connected");
        while (true){
            Scanner scanner = new Scanner(System.in);
            String[] command = scanner.nextLine().split(" ");

            if(command[0].equalsIgnoreCase("exit")){
                System.out.println("Quiting...");
                break;
            }

            invokeCommand(command);
        }
    }

    private static void invokeCommand(String[] command) {
        switch (command[0]) {
            case "create":
            case "CREATE": {
                if (!command[1].equalsIgnoreCase("table")) {
                    System.out.println(WRONG_COMMAND.getValue());
                    break;
                }

                //Getting table name
                String tableName = command[2];

                // if length is greater than 3 it means that table is created with columns
                if (command.length > 3) {

                    // Taking all passed columns into array
                    String[] columns = command[3].substring(1, command[3].length() - 1).split(",");

                    List<String[]> pairs = makeColumnParamKeyValued(columns);

                    // Check if all columns are key:value pairs
                    if (!isColumnParamKeyValued(pairs)) {
                        System.out.println(WRONG_COMMAND.getValue());
                        break;
                    }

                    // parsing columns String into Pair<String, DataType>
                    List<Pair<String, DataTypes>> pairList = parseColumnToMap(pairs);

                    // if DataType value is null, means that wrong data type was passed,  breaking process
                    if (isColumnTypeNull(pairList)) break;

                    TableHelper.getTable(tableName).create(pairList);
                } else
                    TableHelper.getTable(tableName).create();
                break;
            }
            case "drop":
            case "DROP": {
                if (!command[1].equalsIgnoreCase("table")) {
                    System.out.println(WRONG_COMMAND.getValue());
                    break;
                }
                String tableName = command[2];
                TableHelper.getTable(tableName).drop();
                break;
            }
            case "alter":
            case "ALTER": {
                if (!command[1].equalsIgnoreCase("table") || command.length < 4) {
                    System.out.println(WRONG_COMMAND.getValue());
                    break;
                }
                String tableName = command[2];
                String[] columns = command[4].substring(1, command[4].length() - 1).split(",");

                List<String[]> pairs = makeColumnParamKeyValued(columns);

                // Check if all columns are key:value pairs
                if (!isColumnParamKeyValued(pairs)) {
                    System.out.println(WRONG_COMMAND.getValue());
                    break;
                }

                // parsing columns String into Pair<String, DataType>
                List<Pair<String, DataTypes>> pairList = parseColumnToMap(pairs);

                // if DataType value is null, means that wrong data type was passed,  breaking process
                if (isColumnTypeNull(pairList)) break;

                if (command[3].equalsIgnoreCase("MODIFY")) {

                    TableHelper.getTable(tableName).modifyColumn(pairList);

                } else if (command[3].equalsIgnoreCase("ADD")) {

                    TableHelper.getTable(tableName).attachColumns(pairList);

                } else {
                    System.out.println(WRONG_COMMAND.getValue());
                    break;
                }
                break;
            }
            default:
                System.out.println(WRONG_COMMAND.getValue());
        }
    }
}
