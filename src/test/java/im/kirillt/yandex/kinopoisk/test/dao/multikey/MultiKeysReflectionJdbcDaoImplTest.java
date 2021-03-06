package im.kirillt.yandex.kinopoisk.test.dao.multikey;

import im.kirillt.yandex.kinopoisk.DAO.ReflectionJdbcDao;
import im.kirillt.yandex.kinopoisk.DAO.ReflectionJdbcDaoImp;
import im.kirillt.yandex.kinopoisk.DAO.exceptions.DataAccessException;
import org.dbunit.Assertion;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.h2.tools.RunScript;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static im.kirillt.yandex.kinopoisk.test.dao.Utils.*;
import static im.kirillt.yandex.kinopoisk.test.dao.multikey.MultiKeyDefaultDataSet.*;
import static org.h2.engine.Constants.UTF8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class MultiKeysReflectionJdbcDaoImplTest {

    private static ReflectionJdbcDao<MultiKeysPerson> multyKeyPersonDao = null;

    @BeforeClass
    public static void createSchema() throws Exception {
        RunScript.execute(JDBC_URL, USER, PASSWORD, "src/test/resources/multikeys_schema.sql", UTF8, false);
        multyKeyPersonDao = new ReflectionJdbcDaoImp<>(MultiKeysPerson.class, dataConnection());
    }

    @Before
    public void importDataSet() throws Exception {
        IDataSet dataSet = MultiKeyDefaultDataSet.getDataSet();
        cleanlyInsert(dataSet);
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void insertSuccess() throws Exception {
        MultiKeysPerson toInsert = new MultiKeysPerson.PersonBuilder().withId(100500).withName("Uniq Name").withAge(666).build();
        boolean result = multyKeyPersonDao.insert(toInsert);
        assertTrue(result);
        IDataSet expected =  MultiKeyDefaultDataSet.getDefaultBuilder().newRow(TABLE_NAME)
                .with(COLUMN_ID, toInsert.getId())
                .with(COLUMN_NAME, toInsert.getName())
                .with(COLUMN_AGE, toInsert.getAge()).add().build();
        Assertion.assertEquals(dataSetFromConnection(dataConnection()), expected);
    }

    @Test
    public void insertFail() throws Exception {
        exception.expect(DataAccessException.class);
        MultiKeysPerson toInsert = MultiKeyDefaultDataSet.defaultPersons.get(0);
        multyKeyPersonDao.insert(toInsert);
    }

    @Test
    public void updateSuccess() throws Exception {
        MultiKeysPerson toUpdate = MultiKeyDefaultDataSet.defaultPersons.get(0);
        toUpdate.setName("Update");
        boolean result = multyKeyPersonDao.update(toUpdate);
        assertTrue(result);
        IDataSet actual = dataSetFromConnection(dataConnection());
        ITable companyTable = actual.getTable(TABLE_NAME);
        assertEquals(toUpdate.getName(), companyTable.getValue(0, COLUMN_NAME));
        assertEquals(toUpdate.getAge(), companyTable.getValue(0, COLUMN_AGE));
        assertEquals(toUpdate.getId(), companyTable.getValue(0, COLUMN_ID));
    }

    @Test
    public void updateFail() throws Exception {
        MultiKeysPerson unknown = new MultiKeysPerson(100500, "unknown", 42);
        boolean result = multyKeyPersonDao.update(unknown);
        assertFalse(result);
        assertDataBaseNotChanged();
    }

    @Test
    public void deleteByKeySuccess() throws Exception {
        MultiKeysPerson toDelete = defaultPersons.get(2);
        boolean result = multyKeyPersonDao.deleteByKey(toDelete);
        assertTrue(result);
        IDataSet expected = MultiKeyDefaultDataSet.getDataSet(defaultPersons.subList(0, 2));
        Assertion.assertEquals(dataSetFromConnection(dataConnection()), expected);
    }

    @Test
    public void deleteByKeyFail() throws Exception {
        MultiKeysPerson unknown = new MultiKeysPerson(100500, "unknown", 42);
        boolean result = multyKeyPersonDao.deleteByKey(unknown);
        assertFalse(result);
        assertDataBaseNotChanged();
    }

    @Test
    public void selectByKeySuccess() throws Exception {
        MultiKeysPerson charlie = multyKeyPersonDao.selectByKey(new MultiKeysPerson.PersonBuilder().withId(3).withAge(42).build());
        assertThat(charlie.getName(), is("Charlie"));
        assertDataBaseNotChanged();
    }

    @Test
    public void selectByKeyFail() throws Exception {
        exception.expect(DataAccessException.class);
        multyKeyPersonDao.selectByKey(new MultiKeysPerson.PersonBuilder().withId(100500).build());
    }

    @Test
    public void selectAllTest() throws Exception {
        List<MultiKeysPerson> result = multyKeyPersonDao.selectAll();
        assertThat(result, Matchers.containsInAnyOrder(new MultiKeysPerson(1, "Bob", 18),
                new MultiKeysPerson(2, "Alice", 23), new MultiKeysPerson(3, "Charlie", 42)));
    }

    private static void assertDataBaseNotChanged() throws Exception {
        Assertion.assertEquals(dataSetFromConnection(dataConnection()), MultiKeyDefaultDataSet.getDataSet());
    }
}
