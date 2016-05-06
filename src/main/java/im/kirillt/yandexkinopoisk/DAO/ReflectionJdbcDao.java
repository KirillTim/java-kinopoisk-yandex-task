package im.kirillt.yandexkinopoisk.DAO;


import java.sql.SQLException;
import java.util.List;

public interface ReflectionJdbcDao<T> {

    boolean insert(T object);

    boolean update(T object);

    boolean deleteByKey(T key);

    T selectByKey(T key);

    List<T> selectAll();
}
