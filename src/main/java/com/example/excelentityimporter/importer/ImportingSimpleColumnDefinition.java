package com.example.excelentityimporter.importer;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.function.BiConsumer;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ImportingSimpleColumnDefinition<T> {
    String translateKey;
    int columnNumber;
    boolean unique;
    boolean nullable = true;
    BiConsumer<T, Object> setFieldValue;
}
