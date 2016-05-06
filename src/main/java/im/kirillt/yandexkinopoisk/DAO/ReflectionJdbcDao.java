package im.kirillt.yandexkinopoisk.DAO;


import java.sql.SQLException;
import java.util.List;

public interface ReflectionJdbcDao<T> {

    void insert(T object) throws SQLException;

    void update(T object);

    void deleteByKey(T key);

    T selectByKey(T key) throws SQLException;

    List<T> selectAll() throws SQLException;
}
