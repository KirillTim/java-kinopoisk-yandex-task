package im.kirillt.yandex.kinopoisk.DAO.exceptions;

/**
 * Throw if something goes wrong with the database
 */
public class DataAccessException extends RuntimeException {
    public DataAccessException(Throwable cause) {
        super(cause);
    }
}
