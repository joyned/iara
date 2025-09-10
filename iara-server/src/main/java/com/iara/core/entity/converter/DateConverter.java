package com.iara.core.entity.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

@Converter(autoApply = false)
public class DateConverter implements AttributeConverter<Date, String> {

    @Override
    public String convertToDatabaseColumn(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (Objects.isNull(date)) {
            date = new Date();
        }
        calendar.setTime(date);
        return String.valueOf(calendar.getTimeInMillis());
    }

    @Override
    public Date convertToEntityAttribute(String s) {
        if (StringUtils.isBlank(s)) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(s));
        return calendar.getTime();
    }
}
