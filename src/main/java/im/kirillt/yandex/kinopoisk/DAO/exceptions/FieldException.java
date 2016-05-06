package im.kirillt.yandex.kinopoisk.DAO.exceptions;

import java.lang.reflect.Field;

//TODO: rename
public class FieldException extends IllegalArgumentException {
    public FieldException(Field field, Throwable cause) {
        super("Can't get value from " + field.getName() + " in class " + field.getDeclaringClass().getSimpleName(), cause);
    }
}
