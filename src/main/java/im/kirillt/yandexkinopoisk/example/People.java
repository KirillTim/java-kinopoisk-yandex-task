package im.kirillt.yandexkinopoisk.example;

import im.kirillt.yandexkinopoisk.DAO.annotations.Column;
import im.kirillt.yandexkinopoisk.DAO.annotations.Key;
import im.kirillt.yandexkinopoisk.DAO.annotations.Table;

@Table(name="people")
public class People {
    @Key
    @Column(name = "id")
    int id;
    @Column(name = "name")
    String name;
    @Column(name = "sname")
    String sname;
    public People(int id, String name, String sName) {
        this.id = id;
        this.sname = sName;
        this.name = name;
    }
    public People() {};
    @Override
    public String toString() {
        return "id="+id+", name="+name+", sname="+sname;
    }
}
