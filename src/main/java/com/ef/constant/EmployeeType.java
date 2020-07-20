package com.ef.constant;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EmployeeType {
    ADMIN("A"),
    EMPLOYEE("E");

    private String value;

    EmployeeType(String value) {
        this.value = value;
    }

    public static EmployeeType findByString(String s) {
        for (EmployeeType employeeType : EmployeeType.values()) {
            if (employeeType.getValue().equalsIgnoreCase(s)) return employeeType;
        }
        return null;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }


}
