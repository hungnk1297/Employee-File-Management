package com.ef.constant.converter;

import com.ef.constant.EmployeeType;

import javax.persistence.AttributeConverter;

public class EmployeeTypeConverter implements AttributeConverter<EmployeeType, String> {

    @Override
    public String convertToDatabaseColumn(EmployeeType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    @Override
    public EmployeeType convertToEntityAttribute(String dbData) {
        return EmployeeType.findByString(dbData);
    }
}
