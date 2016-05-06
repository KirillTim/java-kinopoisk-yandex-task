package im.kirillt.yandexkinopoisk.DAO;

import im.kirillt.yandexkinopoisk.DAO.annotations.Column;
import im.kirillt.yandexkinopoisk.DAO.annotations.parser.AnnotationsParser;
import im.kirillt.yandexkinopoisk.DAO.exceptions.FieldException;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ReflectionJdbcDaoImp<T> implements ReflectionJdbcDao<T> {

    private final Connection connection;
    private String tableName;
    private List<Field> columns;
    private List<Field> keys;
    private final Class<T> typeHolder;

    public ReflectionJdbcDaoImp(Class<T> typeHolder, Connection connection) {
        // java generics sucks!
        // It's such a pain to use language with no types information
        // It's all because of backward compatibility, I know.
        // Even `(Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
        //  .getActualTypeArguments()[0]` trick will work only after
        // `class MyTypeImpl extends AbstractParameterizedClass<MyType>`
        // ^ the only way to tell compiler what type T REALLY is
        // But I WANT to know what type T is, so , please, pass it to my constructor
        this.connection = connection;
        this.typeHolder = typeHolder;
        try {
            T type = typeHolder.newInstance();
            this.tableName = AnnotationsParser.getTable(type.getClass());
            this.columns = AnnotationsParser.getColumns(type.getClass());
            this.keys = AnnotationsParser.getKeys(type.getClass());
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new IllegalArgumentException("Generic type must have default constructor!");
        }
    }

    @Override
    public void insert(T object) throws SQLException {
        final PreparedStatement preparedStatement = generateInsertStatement(getColumnsValues(object));
        preparedStatement.executeUpdate();
    }

    @Override
    public void update(T object) {

    }

    @Override
    public void deleteByKey(T key) {

    }

    @Override
    public T selectByKey(T key) {
        return null;
    }

    @Override
    public List<T> selectAll() throws SQLException {
        final PreparedStatement preparedStatement = generateSelectStatement(Collections.emptyMap());
        final ResultSet resultSet = preparedStatement.executeQuery();
        final List<T> result = new ArrayList<>();
        while (resultSet.next()) {
            result.add(generateObject(resultSet));
        }
        return result;
    }

    private Map<String, Object> getColumnsValues(T object) {
        final HashMap<String, Object> values = new HashMap<>();
        for (Field field : columns) {
            try {
                values.put(field.getName(), field.get(object));
            } catch (IllegalAccessException ex) {
                throw new FieldException(field, ex);
            }
        }
        return values;
    }

    private PreparedStatement generateInsertStatement(Map<String, Object> values) throws SQLException {
        final StringJoiner keysJoiner = new StringJoiner(",", "(", ")");
        final StringJoiner valuesJoiner = new StringJoiner(",", "(", ")");
        for (String key : values.keySet()) {
            keysJoiner.add(key);
            valuesJoiner.add("?");
        }
        final String query = "insert into " + tableName + " " + keysJoiner + " values " + valuesJoiner;
        PreparedStatement statement = connection.prepareStatement(query);
        statement = addValues(statement, values);
        return statement;
    }

    private PreparedStatement generateSelectStatement(Map<String, Object> values) throws SQLException {
        final StringJoiner keysJoiner = new StringJoiner(",", "(", ")");
        keysJoiner.setEmptyValue("*");
        values.keySet().forEach(keysJoiner::add);
        final String query = "select " + keysJoiner + " from " + tableName;
        PreparedStatement statement = connection.prepareStatement(query);
        statement = addValues(statement, values);
        return statement;
    }

    private static PreparedStatement addValues(PreparedStatement statement, Map<String, Object> values) throws SQLException {
        int index = 1;
        for (Object value : values.values()) {
            statement.setObject(index, value);
            index++;
        }
        return statement;
    }

    private T generateObject(ResultSet cursor) throws SQLException {
        try {
            final T result = typeHolder.newInstance();
            for (Field field : columns) {
                String columnName = field.getAnnotation(Column.class).name();
                field.set(result, cursor.getObject(columnName));
            }
            return result;

        } catch (InstantiationException | IllegalAccessException ex) {
            throw new IllegalArgumentException("Generic type must have default constructor!");
        }
    }
}
