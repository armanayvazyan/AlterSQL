package com.company.utils;

import com.company.enums.DataTypes;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandUtils {

    public static boolean isColumnParamKeyValued(List<String[]> pairs) {
        for(String[] s : pairs) {
            if(s.length < 2) {
                return false;
            }
        }
        return true;
    }

    public static List<String[]> makeColumnParamKeyValued(String[] columns) {
        return Arrays.stream(columns)
                .map(param -> param.split(":"))
                .collect(Collectors.toList());
    }

    public static List<Pair<String, DataTypes>> parseColumnToMap(List<String[]> pairs ) {
        return pairs.stream()
                .map(param -> Pair.of(param[0], DataTypes.parse(param[1])))
                .collect(Collectors.toList());
    }

    // if DataType value is null, means that wrong data type was passed,  breaking process
    public static boolean isColumnTypeNull(List<Pair<String, DataTypes>> pairList) {
        for (var pair : pairList) {
            if(pair.getRight() == null) {
                return true;
            }
        }
        return false;
    }
}
