package im.kirillt.yandexkinopoisk.DAO.annotations.parser.exceptions;

import im.kirillt.yandexkinopoisk.DAO.annotations.Table;

public class NotATableException extends IllegalArgumentException {
    public NotATableException(Class<?> clazz) {
        super("Class " + clazz.getCanonicalName() + " have no " + Table.class.getSimpleName() + " annotation");
    }
}
