package im.kirillt.yandexkinopoisk.test.dao;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.builder.DataSetBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultDataSet {
    public static final String TABLE_NAME = "Person";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_AGE = "AGE";
    public static final ArrayList<Person> defaultPersons
            = new ArrayList<>(Arrays.asList(new Person(1, "Bob", 18), new Person(2, "Alice", 23), new Person(3, "Charlie", 42)));
    private static DataSetBuilder builder = null;

    public static DataSetBuilder getDefaultBuilder() throws DataSetException {
        if (builder == null) {
            builder = createBuilder(defaultPersons);
        }
        return builder;
    }

    static IDataSet getDataSet() throws DataSetException {
        if (builder == null) {
            builder = getDefaultBuilder();
        }
        return builder.build();
    }

    static IDataSet getDataSet(List<Person> persons) throws DataSetException {
        return createBuilder(persons).build();
    }

    private static DataSetBuilder createBuilder(List<Person> persons) throws DataSetException {
        DataSetBuilder builder = new DataSetBuilder();
        for (Person person : persons) {
            builder.newRow(TABLE_NAME)
                    .with(COLUMN_ID, person.getId())
                    .with(COLUMN_NAME, person.getName())
                    .with(COLUMN_AGE, person.getAge()).add();
        }
        return builder;
    }
}
