package im.kirillt.yandexkinopoisk.DAO;

import java.lang.reflect.ParameterizedType;

public abstract class AbstractReflectionJdbcDao<T> implements ReflectionJdbcDao<T> {
    private Class<T> typeHolder;

    @SuppressWarnings("unchecked")
    public AbstractReflectionJdbcDao() {
        //Java generics are broken, want to have C# generics instead :(
        this.typeHolder = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    protected Class<T> getTypeHolder() {
        return typeHolder;
    }
}
