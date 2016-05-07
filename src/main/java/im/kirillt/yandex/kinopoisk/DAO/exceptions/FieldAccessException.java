package im.kirillt.yandex.kinopoisk.DAO.exceptions;

import java.lang.reflect.Field;

/**
 * Thrown when can't get or set value of an object
 */
public class FieldAccessException extends IllegalArgumentException {
    public FieldAccessException(Field field, Throwable cause) {
        super("Can't get value from " + field.getName() + " in class " + field.getDeclaringClass().getSimpleName(), cause);
    }
}
