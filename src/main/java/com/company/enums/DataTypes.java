package com.company.enums;

public enum DataTypes {

    STRING(String.class),
    INTEGER(Integer.class);

    private Class type;

    public static DataTypes parse(String str){
        try {
            return DataTypes.valueOf(str);
        } catch (IllegalArgumentException e) {
            System.out.println("Wrong Data Type inserted");
            return null;
        }
    }

    public Class getType() {
        return type;
    }

    DataTypes(Class type) {
        this.type = type;
    }
}
