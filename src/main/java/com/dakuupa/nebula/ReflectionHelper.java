package com.dakuupa.nebula;


import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class to ease reflection
 *
 * @author EWilliams
 *
 */
public class ReflectionHelper {

    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        if (clazz.getSuperclass() != null) {
            fields.addAll(getAllFields(clazz.getSuperclass()));
        }
        return fields;
    }

    public static Class<?> getGenericType(Field field) {
        ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
        Class<?> type = (Class<?>) stringListType.getActualTypeArguments()[0];
        return type;
    }

    public static Class<?> getGenericType(Class clazz) {
        Class<?> type = (Class<?>) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];
        return type;
    }

}
