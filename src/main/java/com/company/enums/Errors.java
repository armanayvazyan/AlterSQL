package com.company.enums;

public enum Errors {
    WRONG_COMMAND ("Wrong command"),
    WRONG_PASSED_VALUE_TYPE ("Wrong value type was passed"),
    COLUMN_NOT_FOUND ("Column not found"),
    TABLE_NOT_FOUND("Table not found"),
    COULD_NOT_PERFORM_OPERATION("Could not perform operation, try again later")
    ;

    private String value;

    Errors(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
