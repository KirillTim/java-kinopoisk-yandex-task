package im.kirillt.yandexkinopoisk.DAO;

import java.util.List;

public interface ReflectionJdbcDao<T> {

    void insert(T object);

    void update(T object);

    void deleteByKey(T key);

    T selectByKey(T key);

    List<T> selectAll();
}
