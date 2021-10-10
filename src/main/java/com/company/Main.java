package com.company;

import com.company.enums.DataTypes;
import com.company.utils.Condition;
import com.company.utils.TableHelper;
import com.company.utils.TypeCheckers;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

import static com.company.enums.Errors.WRONG_COMMAND;
import static com.company.enums.Errors.WRONG_PASSED_VALUE_TYPE;
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
        String com = command[0].toLowerCase(Locale.ROOT);
        switch (com) {
            case "create": {
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
            case "drop": {
                if (!command[1].equalsIgnoreCase("table")) {
                    System.out.println(WRONG_COMMAND.getValue());
                    break;
                }
                String tableName = command[2];
                TableHelper.getTable(tableName).drop();
                break;
            }
            case "alter": {
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
            case "truncate": {
                if (!command[1].equalsIgnoreCase("table") || command.length != 3) {
                    System.out.println(WRONG_COMMAND.getValue());
                    break;
                }
                String tableName = command[2];
                TableHelper.getTable(tableName).truncate();
                break;
            }
            case "insert": {
                //INSERT INTO TABLE_NAME (col1, col2, col3,.... col N) VALUES (value1, value2, value3, .... valueN);
                if (!command[1].equalsIgnoreCase("into") || command.length < 4) {
                    System.out.println(WRONG_COMMAND.getValue());
                    break;
                }
                String tableName = command[2];

                String[] cols = command[3].substring(1, command[3].length() - 1).split(",");
                String[] values = command[5].substring(1, command[5].length() - 1).split(",");

                if(cols.length == 1 && cols[0].equals("*")) {
                    cols = new String[TableHelper.getTable(tableName).getColumnsCount() - 1];
                    for (int i = 0; i < cols.length; i++) {
                        cols[i] = TableHelper.getTable(tableName).getColumnValue(i + 1);
                    }
                }
                if(cols.length != values.length || cols.length > TableHelper.getTable(tableName).getColumnsCount()) {
                    System.out.println(WRONG_COMMAND.getValue());
                    break;
                }

                String[] valuesArray = new String[TableHelper.getTable(tableName).getColumnsCount()];
                int colIndex = 0;
                for (String col : cols) {
                    List<String> columns = TableHelper.getTable(tableName).getColumns();
                    String neededColumn = columns.stream().filter(c -> c.equals(col)).findAny().orElseThrow(() -> new RuntimeException("Column with name " + col + " not found on table"));

                    if(!TypeCheckers.isNumber(col) && TableHelper.getTable(tableName).getColumnType(neededColumn).equals(DataTypes.NUMBER)) {
                        System.out.println(WRONG_PASSED_VALUE_TYPE.getValue() + " for column " + col + "\nExpecting " + DataTypes.NUMBER);
                        break;
                    }
                    int index = TableHelper.getTable(tableName).getColumnIndex(col);
                    valuesArray[index] = values[colIndex];
                    colIndex++;
                }
                TableHelper.getTable(tableName).insertData(Arrays.asList(valuesArray));
                break;
            }
            case "update": {
                //UPDATE students SET (name=Arman,surname=Ayvazyan) WHERE (Student_Id = 3)
                String tableName = command[1];

                if (!command[2].equalsIgnoreCase("set") || command.length != 6) {
                    System.out.println(WRONG_COMMAND.getValue());
                    break;
                }

                String[] cols = command[3].substring(1, command[3].length() - 1).split(",");
                String[] conditions = command[5].substring(1, command[5].length() - 1).split("=");
                if(conditions.length != 2) {
                    System.out.println(WRONG_COMMAND.getValue());
                    break;
                }
                String conditionName = conditions[0];
                String conditionValue = conditions[1];
                if(TableHelper.getTable(tableName).isColumnNameValid(conditionName)) {
                    TableHelper.getTable(tableName).updateData(Arrays.asList(cols), new Condition(conditionName, conditionValue));
                }
                break;
            }
            default:
                System.out.println(WRONG_COMMAND.getValue());
        }
    }
}
