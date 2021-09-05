package com.yeapkl.mapper;

import com.yeapkl.entity.PaymentDetails;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.boot.context.properties.bind.BindException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class PaymentDetailsMapper implements FieldSetMapper<PaymentDetails> {

    @Override
    public PaymentDetails mapFieldSet(FieldSet fieldSet) throws BindException {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        return new PaymentDetails(
                fieldSet.readLong("accountNumber"),
                fieldSet.readBigDecimal("trxAmount"),
                fieldSet.readString("description"),
                LocalDateTime.of(LocalDate.parse(fieldSet.readString("trxDate"),dateFormatter), LocalTime.parse(fieldSet.readString("trxTime"),timeFormatter)),
                fieldSet.readLong("customerId"));
    }
}