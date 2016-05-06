package im.kirillt.yandexkinopoisk.test.dao.multikey;

import im.kirillt.yandexkinopoisk.DAO.annotations.Column;
import im.kirillt.yandexkinopoisk.DAO.annotations.Key;
import im.kirillt.yandexkinopoisk.DAO.annotations.Table;

import static im.kirillt.yandexkinopoisk.test.dao.DefaultDataSet.*;

@Table(name = TABLE_NAME)
public class MultiKeysPerson {
    @Key
    private @Column(name = COLUMN_ID) int id;
    private @Column(name = COLUMN_NAME) String name;
    @Key
    private @Column(name = COLUMN_AGE) int age;
    public MultiKeysPerson() {}

    public MultiKeysPerson(int id, String name, int age) {
        this.age = age;
        this.id = id;
        this.name = name;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MultiKeysPerson person = (MultiKeysPerson) o;

        if (id != person.id) return false;
        if (age != person.age) return false;
        return name.equals(person.name);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + age;
        return result;
    }

    public static final class PersonBuilder {
        int age;
        int id;
        String name;

        public PersonBuilder() {
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

        public MultiKeysPerson build() {
            MultiKeysPerson person = new MultiKeysPerson();
            person.setAge(age);
            person.setId(id);
            person.setName(name);
            return person;
        }
    }
}
