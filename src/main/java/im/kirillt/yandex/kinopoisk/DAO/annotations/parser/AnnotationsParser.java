package im.kirillt.yandex.kinopoisk.DAO.annotations.parser;

import im.kirillt.yandex.kinopoisk.DAO.annotations.Column;
import im.kirillt.yandex.kinopoisk.DAO.annotations.Key;
import im.kirillt.yandex.kinopoisk.DAO.annotations.Table;
import im.kirillt.yandex.kinopoisk.DAO.annotations.parser.exceptions.NotATableException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Annotated fields parser
 */
public class AnnotationsParser {

    /**
     * Return name provided by {@link Table} annotation
     *
     * @param clazz class to get table name from
     * @return table name
     * @throws NotATableException if class have no {@link Table} annotation
     */
    public static String getTable(Class<?> clazz) throws NotATableException {
        if (clazz.isAnnotationPresent(Table.class)) {
            return clazz.getAnnotation(Table.class).name();
        }
        throw new NotATableException(clazz);
    }

    /**
     * @param clazz class to get fields from
     * @return fields with {@link Key} annotation
     */
    public static List<Field> getKeys(Class<?> clazz) {
        return getAnnotatedFields(clazz, Key.class);
    }

    /**
     * @param clazz class to get fields from
     * @return fields with {@link Column} annotation
     */
    public static List<Field> getColumns(Class<?> clazz) {
        return getAnnotatedFields(clazz, Column.class);
    }

    private static List<Field> getAnnotatedFields(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        final List<Field> result = new ArrayList<>();
        final Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(annotationClass)) {
                result.add(field);
            }
        }
        return result;
    }
}
