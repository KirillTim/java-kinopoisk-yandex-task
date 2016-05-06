package im.kirillt.yandexkinopoisk.test.dao;

import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.builder.DataSetBuilder;

public class DefaultDataSet {
    public static final String TABLE_NAME = "Person";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_AGE = "AGE";

    static IDataSet getDataSet() throws DataSetException {
        DataSetBuilder builder = new DataSetBuilder();
        builder.newRow(TABLE_NAME).with(COLUMN_ID, 1).with(COLUMN_NAME, "Bob").with(COLUMN_AGE, 18).add();
        builder.newRow(TABLE_NAME).with(COLUMN_ID, 2).with(COLUMN_NAME, "Alice").with(COLUMN_AGE, 23).add();
        builder.newRow(TABLE_NAME).with(COLUMN_ID, 3).with(COLUMN_NAME, "Charlie").with(COLUMN_AGE, 42).add();
        return builder.build();

    }
}
