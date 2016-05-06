package im.kirillt.yandexkinopoisk.test;

import im.kirillt.yandexkinopoisk.DAO.annotations.Column;
import im.kirillt.yandexkinopoisk.DAO.annotations.Key;
import im.kirillt.yandexkinopoisk.DAO.annotations.Table;

/**
 * Created by kirill on 06.05.16.
 */
@Table(name = "Person")
public class Person {
    @Key
    @Column(name = "id") int id;
    @Column(name = "name") String name;
    @Column(name = "lastName") String lastName;
    @Column(name = "age") int age;
    Person() {}
}
