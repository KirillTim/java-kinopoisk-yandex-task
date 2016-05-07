package im.kirillt.yandex.kinopoisk.DAO.annotations.parser.exceptions;

import im.kirillt.yandex.kinopoisk.DAO.annotations.Table;

/**
 * Thrown when class have no {@link Table} annotation
 */
public class NotATableException extends IllegalArgumentException {
    public NotATableException(Class<?> clazz) {
        super("Class " + clazz.getCanonicalName() + " have no " + Table.class.getSimpleName() + " annotation");
    }
}
