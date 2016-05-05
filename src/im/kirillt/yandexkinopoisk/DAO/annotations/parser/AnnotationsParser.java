package im.kirillt.yandexkinopoisk.DAO.annotations.parser;

import im.kirillt.yandexkinopoisk.DAO.annotations.Column;
import im.kirillt.yandexkinopoisk.DAO.annotations.Key;
import im.kirillt.yandexkinopoisk.DAO.annotations.Table;
import im.kirillt.yandexkinopoisk.DAO.annotations.parser.exceptions.NotATableException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;


public class AnnotationsParser {
    public static String getTable(Class<?> clazz) throws NotATableException {
        if (clazz.isAnnotationPresent(Table.class)) {
            return clazz.getAnnotation(Table.class).name();
        }
        throw new NotATableException(clazz);
    }

    public static List<Field> getKeys(Class<?> clazz) {
        return getAnnotatedFields(clazz, Key.class);
    }

    public static List<Field> getColumns(Class<?> clazz) {
        return getAnnotatedFields(clazz, Column.class);
    }

    private static List<Field> getAnnotatedFields(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        final List<Field> result = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(annotationClass)) {
                result.add(field);
            }
        }
        return result;
    }
}
