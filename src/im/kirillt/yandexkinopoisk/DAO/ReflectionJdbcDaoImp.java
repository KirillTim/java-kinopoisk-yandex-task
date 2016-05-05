package im.kirillt.yandexkinopoisk.DAO;

import im.kirillt.yandexkinopoisk.DAO.annotations.Key;
import im.kirillt.yandexkinopoisk.DAO.annotations.parser.AnnotationsParser;
import im.kirillt.yandexkinopoisk.DAO.exceptions.FieldException;
import sun.reflect.annotation.AnnotationParser;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class ReflectionJdbcDaoImp<T> implements ReflectionJdbcDao<T> {

    private final Connection connection;
    private Class<T> typeHolder;
    private String tableName;
    private List<Field> columns;
    private List<Field> keys;

    @SuppressWarnings("unchecked")
    public ReflectionJdbcDaoImp(Connection connection, T type) {
        this.connection = connection;
        this.typeHolder = (Class<T>) type.getClass();
        init(type);
    }

    public ReflectionJdbcDaoImp(Connection connection) {
        this.connection = connection;
        try {
            init(typeHolder.newInstance());
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new IllegalArgumentException("Type T must have default constructor!");
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
    public List<T> selectAll() {
        return null;
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
            index ++;
        }
        return statement;
    }

    private void init(T type) {
        this.tableName = AnnotationsParser.getTable(type.getClass());
        this.columns = AnnotationsParser.getColumns(type.getClass());
        this.keys = AnnotationsParser.getKeys(type.getClass());
    }
}
