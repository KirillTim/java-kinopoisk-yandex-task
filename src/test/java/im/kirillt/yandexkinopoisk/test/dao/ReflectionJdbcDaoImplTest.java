package im.kirillt.yandexkinopoisk.test.dao;

import im.kirillt.yandexkinopoisk.DAO.ReflectionJdbcDao;
import im.kirillt.yandexkinopoisk.DAO.ReflectionJdbcDaoImp;
import org.dbunit.Assertion;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.h2.tools.RunScript;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static im.kirillt.yandexkinopoisk.test.dao.DefaultDataSet.*;
import static im.kirillt.yandexkinopoisk.test.dao.Utils.*;
import static org.h2.engine.Constants.UTF8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class ReflectionJdbcDaoImplTest {

    private static ReflectionJdbcDao<Person> personDao = null;

    @BeforeClass
    public static void createSchema() throws Exception {
        RunScript.execute(JDBC_URL, USER, PASSWORD, "src/test/resources/schema.sql", UTF8, false);
        personDao = new ReflectionJdbcDaoImp<>(Person.class, dataConnection());
    }

    @Before
    public void importDataSet() throws Exception {
        IDataSet dataSet = DefaultDataSet.getDataSet();
        cleanlyInsert(dataSet);
    }

    @Test
    public void insertSuccess() throws Exception {
        Person toInsert = new Person.PersonBuilder().withId(100500).withName("Uniq Name").withAge(666).build();
        boolean result = personDao.insert(toInsert);
        assertTrue(result);
        IDataSet expected =  DefaultDataSet.getDefaultBuilder().newRow(TABLE_NAME)
                .with(COLUMN_ID, toInsert.getId())
                .with(COLUMN_NAME, toInsert.getName())
                .with(COLUMN_AGE, toInsert.getAge()).add().build();
        Assertion.assertEquals(dataSetFromConnection(dataConnection()), expected);
    }

    @Test
    public void insertFail() throws Exception {
        Person toInsert = DefaultDataSet.defaultPersons[0];
        boolean result = personDao.insert(toInsert);
        assertFalse(result);
        assertDataBaseNotChanged();
    }

    @Test
    public void updateSuccess() throws Exception {
        Person toUpdate = DefaultDataSet.defaultPersons[0];
        toUpdate.setName("Update");
        toUpdate.setAge(999);
        boolean result = personDao.update(toUpdate);
        assertTrue(result);
        IDataSet actual = dataSetFromConnection(dataConnection());
        ITable companyTable = actual.getTable(TABLE_NAME);
        assertEquals(toUpdate.getName(), companyTable.getValue(0, COLUMN_NAME));
        assertEquals(toUpdate.getAge(), companyTable.getValue(0, COLUMN_AGE));
        assertEquals(toUpdate.getId(), companyTable.getValue(0, COLUMN_ID));
    }

    @Test
    public void updateFail() throws Exception {
        Person unknown = new Person(100500, "unknown", 42);
        boolean result = personDao.update(unknown);
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
        Person unknown = personDao.selectByKey(new Person.PersonBuilder().withId(100500).build());
        assertNull(unknown);
        assertDataBaseNotChanged();
    }

    private static void assertDataBaseNotChanged() throws Exception {
        Assertion.assertEquals(dataSetFromConnection(dataConnection()), DefaultDataSet.getDataSet());
    }
}
