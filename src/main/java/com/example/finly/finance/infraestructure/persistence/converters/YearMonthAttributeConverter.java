package com.example.finly.finance.infraestructure.persistence.converters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDate;
import java.time.YearMonth;

@Converter(autoApply = true)
public class YearMonthAttributeConverter implements AttributeConverter<YearMonth, LocalDate> {
    @Override
    public LocalDate convertToDatabaseColumn(YearMonth yearMonth) {
        if (!(yearMonth == null)){
            return yearMonth.atDay(1);
        }
        return null;
    }

    @Override
    public YearMonth convertToEntityAttribute(LocalDate localDate) {
        if (!(localDate == null)){
            return YearMonth.from(localDate);
        }
        return null;
    }
}
