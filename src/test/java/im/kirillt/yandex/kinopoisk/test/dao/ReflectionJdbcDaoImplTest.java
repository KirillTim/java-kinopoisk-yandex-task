package im.kirillt.yandex.kinopoisk.test.dao;

import im.kirillt.yandex.kinopoisk.DAO.ReflectionJdbcDao;
import im.kirillt.yandex.kinopoisk.DAO.ReflectionJdbcDaoImp;
import im.kirillt.yandex.kinopoisk.DAO.exceptions.DataAccessException;
import org.dbunit.Assertion;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.h2.tools.RunScript;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static org.h2.engine.Constants.UTF8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class ReflectionJdbcDaoImplTest {

    private static ReflectionJdbcDao<Person> personDao = null;

    @BeforeClass
    public static void createSchema() throws Exception {
        RunScript.execute(Utils.JDBC_URL, Utils.USER, Utils.PASSWORD, "src/test/resources/schema.sql", UTF8, false);
        personDao = new ReflectionJdbcDaoImp<>(Person.class, Utils.dataConnection());
    }

    @Before
    public void importDataSet() throws Exception {
        IDataSet dataSet = DefaultDataSet.getDataSet();
        Utils.cleanlyInsert(dataSet);
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void insertSuccess() throws Exception {
        Person toInsert = new Person.PersonBuilder().withId(100500).withName("Uniq Name").withAge(666).build();
        boolean result = personDao.insert(toInsert);
        assertTrue(result);
        IDataSet expected = DefaultDataSet.getDefaultBuilder().newRow(DefaultDataSet.TABLE_NAME)
                .with(DefaultDataSet.COLUMN_ID, toInsert.getId())
                .with(DefaultDataSet.COLUMN_NAME, toInsert.getName())
                .with(DefaultDataSet.COLUMN_AGE, toInsert.getAge()).add().build();
        Assertion.assertEquals(Utils.dataSetFromConnection(Utils.dataConnection()), expected);
    }

    @Test
    public void insertFail() throws Exception {
        exception.expect(DataAccessException.class);
        Person toInsert = DefaultDataSet.defaultPersons.get(0);
        personDao.insert(toInsert);
    }

    @Test
    public void updateSuccess() throws Exception {
        Person toUpdate = DefaultDataSet.defaultPersons.get(0);
        toUpdate.setName("Update");
        toUpdate.setAge(999);
        boolean result = personDao.update(toUpdate);
        assertTrue(result);
        IDataSet actual = Utils.dataSetFromConnection(Utils.dataConnection());
        ITable companyTable = actual.getTable(DefaultDataSet.TABLE_NAME);
        assertEquals(toUpdate.getName(), companyTable.getValue(0, DefaultDataSet.COLUMN_NAME));
        assertEquals(toUpdate.getAge(), companyTable.getValue(0, DefaultDataSet.COLUMN_AGE));
        assertEquals(toUpdate.getId(), companyTable.getValue(0, DefaultDataSet.COLUMN_ID));
    }

    @Test
    public void updateFail() throws Exception {
        Person unknown = new Person(100500, "unknown", 42);
        boolean result = personDao.update(unknown);
        assertFalse(result);
        assertDataBaseNotChanged();
    }

    @Test
    public void deleteByKeySuccess() throws Exception {
        Person toDelete = DefaultDataSet.defaultPersons.get(2);
        boolean result = personDao.deleteByKey(toDelete);
        assertTrue(result);
        IDataSet expected = DefaultDataSet.getDataSet(DefaultDataSet.defaultPersons.subList(0, 2));
        Assertion.assertEquals(Utils.dataSetFromConnection(Utils.dataConnection()), expected);
    }

    @Test
    public void deleteByKeyFail() throws Exception {
        Person unknown = new Person(100500, "unknown", 42);
        boolean result = personDao.deleteByKey(unknown);
        assertFalse(result);
        assertDataBaseNotChanged();
    }

    @Test
    public void selectByKeySuccess() throws Exception {
        Person charlie = personDao.selectByKey(new Person.PersonBuilder().withId(3).build());
        assertThat(charlie.getName(), is("Charlie"));
        assertThat(charlie.getAge(), is(42));
        assertDataBaseNotChanged();
    }

    @Test
    public void selectByKeyFail() throws Exception {
        exception.expect(DataAccessException.class);
        personDao.selectByKey(new Person.PersonBuilder().withId(100500).build());
    }

    @Test
    public void selectAllTest() throws Exception {
        List<Person> result = personDao.selectAll();
        assertThat(result, containsInAnyOrder(new Person(1, "Bob", 18), new Person(2, "Alice", 23), new Person(3, "Charlie", 42)));
    }

    private static void assertDataBaseNotChanged() throws Exception {
        Assertion.assertEquals(Utils.dataSetFromConnection(Utils.dataConnection()), DefaultDataSet.getDataSet());
    }
}
