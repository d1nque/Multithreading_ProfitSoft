package org.example.model;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.InvalidPropertiesFormatException;
import java.util.zip.DataFormatException;

import static org.junit.jupiter.api.Assertions.*;

class UtilityTest {

    @Test
    void simpleTest() throws DataFormatException, IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Util util = Utility.loadFromProperties(Util.class, Paths.get("src/main/resources/1.properties"));

        assertEquals("value1", util.getStringProperty());
        assertEquals(103, util.getNumProperty());
        assertEquals(Instant.parse("2022-11-29T18:30:00Z"), util.getTimeProperty());
    }

    @Test
    void dataFormatTest() throws DataFormatException, IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        try{
            Utility.loadFromProperties(Util.class, Paths.get("src/main/resources/1.properties"));
        } catch (DataFormatException ex){
            assertEquals("Wrong format", ex.getMessage());
        }
    }

    @Test
    void correctInteger() throws DataFormatException, IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        try{
            Utility.loadFromProperties(Util.class, Paths.get("src/main/resources/1.properties"));
        } catch (NumberFormatException ex){
            assertEquals("Wrong Integer value", ex.getMessage());
        }
    }

    @Test
    void noKey() throws DataFormatException, IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        try{
            //if Properties name и field не найдены в properties
            Utility.loadFromProperties(Util.class, Paths.get("src/main/resources/1.properties"));
        } catch (InvalidPropertiesFormatException ex){
            assertEquals("Wrong key", ex.getMessage());
        }
    }

    @Test
    void wrongPath() throws DataFormatException, IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        try{
            Utility.loadFromProperties(Util.class, Paths.get("src/main/resources/12.properties"));
        } catch (FileNotFoundException ex){
            assertEquals("Wrong path", ex.getMessage());
        }
    }
}