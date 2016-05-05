package im.kirillt.yandexkinopoisk.DAO.annotations.parser;

import im.kirillt.yandexkinopoisk.DAO.annotations.Key;
import im.kirillt.yandexkinopoisk.DAO.annotations.Table;
import im.kirillt.yandexkinopoisk.DAO.annotations.parser.exceptions.NoKeysFoundException;
import im.kirillt.yandexkinopoisk.DAO.annotations.parser.exceptions.NotATableException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


public class AnnotationsParser {
    public static String getTable(Class<?> clazz) throws NotATableException {
        if (clazz.isAnnotationPresent(Table.class)) {
            return clazz.getAnnotation(Table.class).name();
        }
        throw new NotATableException(clazz);
    }

    public static Map<String, Object> getKeys(Class<?> clazz) throws NoKeysFoundException, IllegalAccessException {
        final Map<String, Object> keys = new HashMap<>();
        final Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Key.class)) {
                keys.put(field.getName(), field.get(clazz));
            }
        }
        return keys;
    }
}
