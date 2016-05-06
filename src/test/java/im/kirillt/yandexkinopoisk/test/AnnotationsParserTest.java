package im.kirillt.yandexkinopoisk.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;

import im.kirillt.yandexkinopoisk.DAO.annotations.Column;
import im.kirillt.yandexkinopoisk.DAO.annotations.Key;
import im.kirillt.yandexkinopoisk.DAO.annotations.Table;
import im.kirillt.yandexkinopoisk.DAO.annotations.parser.AnnotationsParser;
import im.kirillt.yandexkinopoisk.DAO.annotations.parser.exceptions.NotATableException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Field;
import java.util.List;


public class AnnotationsParserTest {

    private static final String TABLE_NAME = "Table";
    private static final String COLUMN_ID = "Id";
    private static final String COLUMN_DATA = "data";

    @Table(name = TABLE_NAME)
    private class WithTableAndColumns {
        @Key
        @Column(name = COLUMN_ID)
        int id;

        @Column(name = COLUMN_DATA)
        String data;
        WithTableAndColumns() {};
    }

    private class WithoutAnnotations {
        int id;
        String data;
        WithoutAnnotations() {};
    }

    @Test
    public void ParserGetTableSuccess() {
        assertEquals(AnnotationsParser.getTable(WithTableAndColumns.class), TABLE_NAME);
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void ParserGetTableFail() {
        exception.expect(NotATableException.class);
        AnnotationsParser.getTable(WithoutAnnotations.class);
    }

    @Test
    public void ParserGetKeys() {
        Field withTableKey = null;
        try {
            withTableKey = WithTableAndColumns.class.getDeclaredField("id");
        } catch (NoSuchFieldException ignore) {}
        List<Field> answer = AnnotationsParser.getKeys(WithTableAndColumns.class);
        assertEquals(answer.size(), 1);
        assertEquals(answer.get(0), withTableKey);

        answer = AnnotationsParser.getKeys(WithoutAnnotations.class);
        assertTrue(answer.isEmpty());
    }

    @Test
    public void ParserGetColumns() {
        Field idField = null;
        Field dataField = null;
        try {
            idField = WithTableAndColumns.class.getDeclaredField("id");
            dataField = WithTableAndColumns.class.getDeclaredField("data");
        } catch (NoSuchFieldException ignore) {}
        List<Field> answer = AnnotationsParser.getColumns(WithTableAndColumns.class);
        assertEquals(answer.size(), 2);
        assertTrue(answer.contains(idField));
        assertTrue(answer.contains(dataField));

        answer = AnnotationsParser.getColumns(WithoutAnnotations.class);
        assertTrue(answer.isEmpty());
    }
}
