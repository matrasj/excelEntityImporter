package com.example.excelentityimporter.helper;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BatchUtils {
    public static <T, R> Map<R, T> itSelfByIdentifierMap(Set<T> items, Function<T, R> keyExtractor) {
        return items.stream().collect(Collectors.toMap(keyExtractor, Function.identity()));
    }
}
