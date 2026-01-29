package it.gov.pagopa.arc.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@Mapper
public interface MapperUtilities {
    /** To convert LocalDateTime into ZonedDateTime and truncate to seconds */
    @Named("convertToZonedDateTimeAndTruncateSeconds")
    static ZonedDateTime convertToZonedDateTimeAndTruncateSeconds(LocalDateTime dateTime){
        return ZonedDateTime.of(dateTime, ZoneOffset.UTC).truncatedTo(ChronoUnit.SECONDS);
    }

    /** To calculate totalAmount (amount + fee) */
    @Named("calculateTotalAmount")
    static Long calculateTotalAmount(Long amount, Long fee){
        if(amount == null){
            return null;
        }

        if (fee == null) {
            fee = 0L;
        }

        return Long.sum(amount, fee);
    }

}
