package im.kirillt.yandexkinopoisk.test.dao;

import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.h2.jdbcx.JdbcDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

class Utils {

    private static final String JDBC_DRIVER = org.h2.Driver.class.getName();
    static final String JDBC_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    static final String USER = "sa";
    static final String PASSWORD = "";

    static IDataSet readDataSet() throws Exception {
        return new FlatXmlDataSetBuilder().build(new File("src/test/resources/PersonTable.xml"));
    }

    static void cleanlyInsert(IDataSet dataSet) throws Exception {
        IDatabaseTester databaseTester = new JdbcDatabaseTester(JDBC_DRIVER, JDBC_URL, USER, PASSWORD);
        databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
        databaseTester.setDataSet(dataSet);
        databaseTester.onSetup();
    }


    static Connection dataConnection() throws SQLException {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL(JDBC_URL);
        dataSource.setUser(USER);
        dataSource.setPassword(PASSWORD);
        return dataSource.getConnection();
    }
}
