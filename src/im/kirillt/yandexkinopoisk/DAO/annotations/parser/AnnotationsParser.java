package im.kirillt.yandexkinopoisk.DAO.annotations.parser;

import im.kirillt.yandexkinopoisk.DAO.annotations.Table;
import im.kirillt.yandexkinopoisk.DAO.annotations.parser.exceptions.NotATableException;


public class AnnotationsParser {
    public static String getTable(Class<?> clazz) throws NotATableException {
        if (clazz.isAnnotationPresent(Table.class)) {
            return clazz.getAnnotation(Table.class).name();
        }
        throw new NotATableException(clazz);
    }
}
