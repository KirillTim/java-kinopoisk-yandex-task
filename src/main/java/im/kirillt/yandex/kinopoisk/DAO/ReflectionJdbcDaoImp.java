package im.kirillt.yandex.kinopoisk.DAO;

import im.kirillt.yandex.kinopoisk.DAO.annotations.Column;
import im.kirillt.yandex.kinopoisk.DAO.exceptions.DataAccessException;
import im.kirillt.yandex.kinopoisk.DAO.exceptions.FieldAccessException;
import im.kirillt.yandex.kinopoisk.DAO.exceptions.NoKeysFoundException;
import im.kirillt.yandex.kinopoisk.DAO.annotations.parser.AnnotationsParser;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Simple Reflection DAO
 * Get table name from {@link im.kirillt.yandex.kinopoisk.DAO.annotations.Table} annotation
 * Get keys from fields with {@link im.kirillt.yandex.kinopoisk.DAO.annotations.Key} annotation
 * Get columns from fields with {@link im.kirillt.yandex.kinopoisk.DAO.annotations.Column} annotation
 *
 * @param <T> Type DAO created for, should have default constructor
 */
public class ReflectionJdbcDaoImp<T> implements ReflectionJdbcDao<T> {

    private final Connection connection;
    private String tableName;
    private List<Field> columns;
    private List<Field> keys;
    private final Class<T> typeHolder;

    /**
     * Main and only constructor
     *
     * @param typeHolder type T class information for object instantiation
     * @param connection database connection to read from and write to
     */
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
        final T type = createObjectOfTypeT();
        this.tableName = AnnotationsParser.getTable(type.getClass());
        this.columns = AnnotationsParser.getColumns(type.getClass());
        this.keys = AnnotationsParser.getKeys(type.getClass());
        if (this.keys.isEmpty()) {
            throw new NoKeysFoundException(typeHolder);
        }
    }

    /**
     * @throws DataAccessException if any SQL Error happened
     * @inheritDoc
     */
    @Override
    public boolean insert(T object) {
        try (final PreparedStatement statement = generateInsertStatement(getFieldsValues(columns, object))) {
            int result = statement.executeUpdate();
            return result > 0;
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    /**
     * @throws DataAccessException if any SQL Error happened
     * @inheritDoc
     */
    @Override
    public boolean update(T object) {
        try (final PreparedStatement statement = generateUpdateStatement(object)) {
            int result = statement.executeUpdate();
            return result > 0;
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    /**
     * @throws DataAccessException if <b>object not found</b> or if any SQL Error happened
     * @inheritDoc
     */
    @Override
    public boolean deleteByKey(T key) {
        try (final PreparedStatement statement = generateDeleteStatement(getFieldsValues(keys, key))) {
            int result = statement.executeUpdate();
            return result > 0;
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    /**
     * @throws DataAccessException if <b>object not found</b> or if any SQL Error happened
     * @inheritDoc
     */
    @Override
    public T selectByKey(T key) {
        //it's better to return Either[T, SQLException] though
        try (final PreparedStatement statement = generateSelectStatement(getFieldsValues(keys, key));
             final ResultSet resultSet = statement.executeQuery()) {
            resultSet.next();
            return generateObject(resultSet);
        } catch (SQLException ex) {
            // It's not the best solution to throw an exception if key not found,
            // but there is no way to check is something goes wrong with database
            // or is key not found on a such low level
            throw new DataAccessException(ex);
        }
    }

    /**
     * @throws DataAccessException if any SQL Error happened
     * @inheritDoc
     */
    @Override
    public List<T> selectAll() {
        try (final PreparedStatement preparedStatement = generateSelectStatement(Collections.emptyMap());
             final ResultSet resultSet = preparedStatement.executeQuery()) {
            final List<T> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(generateObject(resultSet));
            }
            return result;
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        }
    }

    private Map<String, Object> getFieldsValues(List<Field> fields, T object) {
        final HashMap<String, Object> values = new HashMap<>();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                values.put(field.getName(), field.get(object));
                field.setAccessible(false);
            } catch (IllegalAccessException ex) {
                throw new FieldAccessException(field, ex);
            }
        }
        return values;
    }

    private PreparedStatement generateUpdateStatement(T object) throws SQLException {
        final Map<String, Object> setValues = getFieldsValues(columns, object);
        final Map<String, Object> keyValues = getFieldsValues(keys, object);
        final StringJoiner setJoiner = new StringJoiner(", ");
        for (String key : setValues.keySet()) {
            setJoiner.add(key + "=?");
        }
        final String query = "UPDATE " + tableName + " SET " + setJoiner + generateWhereString(keyValues);
        final PreparedStatement statement = connection.prepareStatement(query);
        int index = 1;
        for (Object value : setValues.values()) {
            statement.setObject(index, value);
            index++;
        }
        for (Object value : keyValues.values()) {
            statement.setObject(index, value);
            index++;
        }
        return statement;
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

    private String generateWhereString(Map<String, Object> keys) {
        final StringJoiner keysJoiner = new StringJoiner(" AND ");
        for (String key : keys.keySet()) {
            keysJoiner.add(key + "=?");
        }
        return " WHERE " + keysJoiner.toString();
    }

    private PreparedStatement generateDeleteStatement(Map<String, Object> keys) throws SQLException {
        if (keys.isEmpty()) {
            throw new IllegalArgumentException("keys shouldn't be empty");
        }
        final String whereString = generateWhereString(keys);
        String query = "DELETE FROM " + tableName + whereString;
        PreparedStatement statement = connection.prepareStatement(query);
        statement = addValues(statement, keys);
        return statement;
    }

    private PreparedStatement generateSelectStatement(Map<String, Object> keys) throws SQLException {
        String query = "SELECT * FROM " + tableName;
        if (!keys.isEmpty()) {
            query += generateWhereString(keys);
        }
        PreparedStatement statement = connection.prepareStatement(query);
        statement = addValues(statement, keys);
        return statement;
    }

    private static PreparedStatement addValues(PreparedStatement statement, Map<String, Object> values)
            throws SQLException {
        int index = 1;
        for (Object value : values.values()) {
            statement.setObject(index, value);
            index++;
        }
        return statement;
    }

    private T generateObject(ResultSet cursor) throws SQLException {
        try {
            final T result = createObjectOfTypeT();
            for (Field field : columns) {
                String columnName = field.getAnnotation(Column.class).name();
                field.setAccessible(true);
                field.set(result, cursor.getObject(columnName));
                field.setAccessible(false);
            }
            return result;

        } catch (IllegalAccessException ex) {
            throw new IllegalArgumentException("Can't access feild");
        }
    }

    private T createObjectOfTypeT() {
        try {
            return typeHolder.newInstance();
        } catch (InstantiationException ex) {
            throw new IllegalArgumentException("Generic type must have default constructor!", ex);
        } catch (IllegalAccessException ex) {
            throw new IllegalArgumentException("Can't access constructor", ex);
        }
    }
}
