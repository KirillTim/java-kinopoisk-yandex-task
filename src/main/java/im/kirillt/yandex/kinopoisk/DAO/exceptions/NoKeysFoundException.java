package im.kirillt.yandex.kinopoisk.DAO.exceptions;

import im.kirillt.yandex.kinopoisk.DAO.annotations.Key;

public class NoKeysFoundException extends IllegalArgumentException {
    public NoKeysFoundException(Class<?> clazz) {
        super("Class " + clazz.getCanonicalName() + " have no " + Key.class.getSimpleName() + " annotated fields");
    }
}
