package im.kirillt.yandex.kinopoisk.example;

import com.mysql.cj.jdbc.MysqlDataSource;
import im.kirillt.yandex.kinopoisk.DAO.ReflectionJdbcDao;
import im.kirillt.yandex.kinopoisk.DAO.ReflectionJdbcDaoImp;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


public class Main {

    private static final String USER = "sqluser";
    private static final String PWD = "sqluserpw";
    //Just a little `example`
    //msqy database required
    public static void main(String[] args) throws SQLException, InstantiationException, IllegalAccessException {
        DataSource dataSource = getMySQLDataSource();
        Connection connection = dataSource.getConnection();
        ReflectionJdbcDao<People> dao = new ReflectionJdbcDaoImp<>(People.class, connection);

        List<People> rows = dao.selectAll();
        rows.forEach(System.out::println);

        People toIns = null;

        if (rows.isEmpty()) {
            toIns = new People(1, "a", "b");
        } else {
            People last = rows.get(rows.size()-1);
            toIns = new People(last.id+1, last.name+"a", last.sname+"b");
        }
        dao.insert(toIns);

        rows = dao.selectAll();
        rows.forEach(System.out::println);

        toIns.name = "updated";
        dao.update(toIns);

        rows = dao.selectAll();
        rows.forEach(System.out::println);
    }

    public static DataSource getMySQLDataSource() {
        MysqlDataSource mysqlDS = new MysqlDataSource();
        mysqlDS.setURL("jdbc:mysql://localhost/user?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
        mysqlDS.setUser(USER);
        mysqlDS.setPassword(PWD);
        return mysqlDS;
    }
}
