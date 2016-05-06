package im.kirillt.yandexkinopoisk.DAO.exceptions;

import im.kirillt.yandexkinopoisk.DAO.annotations.Key;

public class NoKeysFoundException extends IllegalArgumentException {
    public NoKeysFoundException(Class<?> clazz) {
        super("Class " + clazz.getCanonicalName() + " have no " + Key.class.getSimpleName() + " annotated fields");
    }
}
