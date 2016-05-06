package im.kirillt.yandexkinopoisk.test.dao;

import im.kirillt.yandexkinopoisk.DAO.ReflectionJdbcDao;
import im.kirillt.yandexkinopoisk.DAO.ReflectionJdbcDaoImp;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.RunScript;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import static im.kirillt.yandexkinopoisk.test.dao.Utils.*;
import static org.h2.engine.Constants.UTF8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ReflectionJdbcDaoImplTest {

    @BeforeClass
    public static void createSchema() throws Exception {
        RunScript.execute(JDBC_URL, USER, PASSWORD, "src/test/resources/schema.sql", UTF8, false);
    }

    @Before
    public void importDataSet() throws Exception {
        IDataSet dataSet = readDataSet();
        cleanlyInsert(dataSet);
    }

    @Test
    public void findsAndReadsExistingPersonById() throws Exception {
        ReflectionJdbcDao<Person> personDao = new ReflectionJdbcDaoImp<>(Person.class, dataConnection());
        Person key = new Person();
        key.id = 3;
        Person charlie = personDao.selectByKey(key);

        assertThat(charlie.name, is("Charlie"));
        assertThat(charlie.lastName, is("Brown"));
        assertThat(charlie.age, is(42));
    }
}
