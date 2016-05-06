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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static final class PersonBuilder {
        int age;
        int id;
        String name;
        String lastName;

        private PersonBuilder() {
        }

        public static PersonBuilder aPerson() {
            return new PersonBuilder();
        }

        public PersonBuilder withAge(int age) {
            this.age = age;
            return this;
        }

        public PersonBuilder withId(int id) {
            this.id = id;
            return this;
        }

        public PersonBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public PersonBuilder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Person build() {
            Person person = new Person();
            person.setAge(age);
            person.setId(id);
            person.setName(name);
            person.setLastName(lastName);
            return person;
        }
    }
}
