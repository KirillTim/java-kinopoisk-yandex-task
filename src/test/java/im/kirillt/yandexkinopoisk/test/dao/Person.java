package im.kirillt.yandexkinopoisk.test.dao;

import im.kirillt.yandexkinopoisk.DAO.annotations.Column;
import im.kirillt.yandexkinopoisk.DAO.annotations.Key;
import im.kirillt.yandexkinopoisk.DAO.annotations.Table;

@Table(name = "Person")
public class Person {
    @Key
    @Column(name = "id") int id;
    @Column(name = "name") String name;
    @Column(name = "last_Name") String lastName;
    @Column(name = "age") int age;
    public Person() {}
}
