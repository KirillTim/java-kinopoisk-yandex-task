package im.kirillt.yandex.kinopoisk.DAO;

import java.util.List;


public interface ReflectionJdbcDao<T> {
    /**
     * Insert {@code object} to database table
     * with name taken from {@link im.kirillt.yandex.kinopoisk.DAO.annotations.Table} annotation
     *
     * @param object object to insert
     * @return true if success, false otherwise
     */
    boolean insert(T object);

    /**
     * Update row in table
     * with name taken from {@link im.kirillt.yandex.kinopoisk.DAO.annotations.Table} annotation
     * and key taken from {@link im.kirillt.yandex.kinopoisk.DAO.annotations.Key}
     *
     * @param object object to update
     * @return true if success, false otherwise
     */
    boolean update(T object);

    /**
     * Delete object in table
     * with name taken from {@link im.kirillt.yandex.kinopoisk.DAO.annotations.Table} annotation
     * and key taken from {@link im.kirillt.yandex.kinopoisk.DAO.annotations.Key}
     * <br> Fields without {@link im.kirillt.yandex.kinopoisk.DAO.annotations.Key} annotation ignored
     *
     * @param key object with key fields to delete
     * @return true if success, false otherwise
     */
    boolean deleteByKey(T key);

    /**
     * Find object in table
     * with name taken from {@link im.kirillt.yandex.kinopoisk.DAO.annotations.Table} annotation
     * and key taken from {@link im.kirillt.yandex.kinopoisk.DAO.annotations.Key}
     * <br> Fields without {@link im.kirillt.yandex.kinopoisk.DAO.annotations.Key} annotation ignored
     * @param key object with key fields to find
     * @return object if success, null otherwise
     */
    T selectByKey(T key);

    /**
     * Return all object from table
     * with name taken from {@link im.kirillt.yandex.kinopoisk.DAO.annotations.Table} annotation
     * @return List of objects
     */
    List<T> selectAll();
}
