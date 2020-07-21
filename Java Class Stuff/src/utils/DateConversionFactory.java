package utils;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class DateConversionFactory {

//    public static final Function<Timestamp, ZonedDateTime> timestampToZonedDateTime =  (timestamp ->
//            timestamp.toLocalDateTime().atZone(ZonedDateTime.now(ZoneId.systemDefault()).getZone()));
//
//    public static final Function<ZonedDateTime, Timestamp> zonedDateTimeToUtcTimestamp =  (zonedDateTime ->
//            Timestamp.valueOf(zonedDateTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime()));
//
//    public static final Function<ZonedDateTime, Date> zonedDateTimeToUtcDate =  (zonedDateTime ->
//            Date.valueOf(LocalDate.from(zonedDateTime.withZoneSameInstant(ZoneId.of("UTC")).toInstant())));
//
//    public static final Function<Date, ZonedDateTime> dateToZonedDateTime =  (date ->
//            ZonedDateTime.ofInstant(date.toInstant(),ZoneId.systemDefault()));
//
//    public static final UnaryOperator<Timestamp> timestampToUtcTimestamp =  (timestamp ->
//            timestampToZonedDateTime.andThen(zonedDateTimeToUtcTimestamp).apply(timestamp));
//
//    public static final Supplier<Timestamp> getCurrentUtcTimestamp = () -> timestampToUtcTimestamp.apply(Timestamp.valueOf(LocalDateTime.now()));
}
