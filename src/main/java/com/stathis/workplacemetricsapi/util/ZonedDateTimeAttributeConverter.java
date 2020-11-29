package com.stathis.workplacemetricsapi.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Converter(autoApply = true)
public class ZonedDateTimeAttributeConverter implements AttributeConverter<ZonedDateTime, Timestamp> {

    static ZoneId utcZoneId = ZoneId.of("UTC");
    static ZoneId defaultZoneId = ZoneId.systemDefault();

    @Override
    public Timestamp convertToDatabaseColumn(ZonedDateTime zonedDateTime) {
        // Store always in UTC
        return (zonedDateTime == null ? null :
                Timestamp.valueOf(toUtcZoneId(zonedDateTime).toLocalDateTime()));
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(Timestamp sqlTimestamp) {
        // Read from database (stored in UTC) and return with the system default.
        return (sqlTimestamp == null ? null :
                toDefaultZoneId(sqlTimestamp.toLocalDateTime().atZone(ZoneId.of("UTC"))));
    }

    private ZonedDateTime toUtcZoneId(ZonedDateTime zonedDateTime) {
        return zonedDateTime.withZoneSameInstant(utcZoneId);
    }

    private ZonedDateTime toDefaultZoneId(ZonedDateTime zonedDateTime) {
        return zonedDateTime.withZoneSameInstant(defaultZoneId);
    }

}