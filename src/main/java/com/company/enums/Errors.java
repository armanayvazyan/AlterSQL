package com.company.enums;

public enum Errors {
    WRONG_COMMAND ("Wrong command"),
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
