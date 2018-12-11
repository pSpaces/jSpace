package org.jspace.tests;
import org.jspace.Tuple;
import org.jspace.Template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.jspace.protocol.DataProperties;

@DisplayName("Properties for the data field of the message")
class TestDataProperties {
    static DataProperties noData;
    static DataProperties withTuple;
    static DataProperties withTuples;
    static DataProperties withTemplate;
    static Tuple tuple1;
    static Tuple tuple2;
    static Tuple[] tuples;
    static Template template;

    @BeforeAll
    static void initAllObjects() {
        noData = new DataProperties();
        tuple1 = new Tuple("Hello", "World!");
        tuple2 = new Tuple("Other", "Tuple!");
        tuples = new Tuple[] {tuple1, tuple2};
        template = new Template(1,2,3);
        withTuple = new DataProperties("tuple", tuple1);
        withTuples = new DataProperties("tuples", tuples);
        withTemplate = new DataProperties("template", template);
    }

    @Test
    @DisplayName("Get string from type field of DataProperties()")
    void testGetEmptyType() {
        assertNull(noData.getType());
    }

    @Test
    @DisplayName("Get object from value field of DataProperties()")
    void testGetEmptyValue() {
        assertNull(noData.getType());
    }

    @Test
    @DisplayName("Get string from type field of DataProperties('tuple', tuple1)")
    void testGetTypeTuple() {
        assertEquals("tuple", withTuple.getType());
    }

    @Test
    @DisplayName("Get tuple from value field of DataProperties('tuple', tuple1)")
    void testGetValueTuple() {
        assertEquals(tuple1, withTuple.getValue());
    }

    @Test
    @DisplayName("Get string from type field of DataProperties('tuples', tuples")
    void testGetTypeTuples() {
        assertEquals("tuples", withTuples.getType());
    }

    @Test
    @DisplayName("Get list of tuples from type field of DataProperties('tuples', tuples")
    void testGetValueTuples() {
        assertEquals(tuples, withTuples.getValue());
    }

    @Test
    @DisplayName("Get string from the type field of DataProperties('template', template)")
    void testGetTypeTemplate() {
        assertEquals("template", withTemplate.getType());
    }

    @Test
    @DisplayName("Get template from the value field of DataProperties('template', template)")
    void testGetValueTemplate() {
        assertEquals(template, withTemplate.getValue());
    }

    @Test
    @DisplayName("Type field of DataProperties() is settable to a string")
    void testSetType() {
        noData.setType("type");
        assertEquals("type", noData.getType());
    }

    @Test
    @DisplayName("Value field of DataProperties() is settable to an object")
    void testSetValue() {
        noData.setValue(tuple1);
        assertEquals(tuple1, noData.getValue());
    }
}
