package org.example.model;

import org.apache.commons.beanutils.PropertyUtils;
import org.example.annotation.Property;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InvalidPropertiesFormatException;
import java.util.Locale;
import java.util.Properties;
import java.util.zip.DataFormatException;

public class Utility {
    public static <T>T loadFromProperties(Class<T> cls, Path propertiesPath) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, DataFormatException {
        T c = cls.getConstructor().newInstance();
        Field[] fields = cls.getDeclaredFields();
        Properties properties = new Properties();
        if(!propertiesPath.toFile().exists()){
            throw new FileNotFoundException("Wrong path");
        }

        properties.load(new FileInputStream(propertiesPath.toFile()));

        for (Field field : fields){
            field.setAccessible(true);
            Annotation[] annotations = field.getDeclaredAnnotations();
            if(annotations.length != 0) {
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType().equals(Property.class)) {
                        Property prop = (Property) annotation;
                        if (!prop.name().isEmpty()) {
                            if(!properties.containsKey(field.getName()) && !properties.containsKey(prop.name()))
                                throw new InvalidPropertiesFormatException("Wrong key");
                            if (!prop.name().equals(field.getName()) && !properties.containsKey(field.getName())) {
                                changeKey(field, prop, properties, propertiesPath);
                            }
                        }
                        if (!prop.format().isEmpty()) {
                            field.set(c, stringToInstant(field, prop, properties));
                        } else if (field.getType().equals(Integer.class)) {
                            try{
                                field.set(c, Integer.valueOf(properties.getProperty(field.getName())));
                            } catch (NumberFormatException ex){
                                throw new NumberFormatException("Wrong Integer value");
                            }

                        } else {
                            field.set(c, properties.getProperty(field.getName()));
                        }
                    }
                }
            }
            else{
                if (field.getType().equals(Integer.class)) {
                    field.set(c, Integer.valueOf(properties.getProperty(field.getName())));
                } else {
                    field.set(c, properties.getProperty(field.getName()));
                }
            }
        }
        return c;
    }

    private static Instant stringToInstant(Field field, Property prop, Properties properties) throws DataFormatException {
        try {
            String pattern = prop.format();
            String timeProperty = properties.getProperty(field.getName());
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern, Locale.FRANCE);
            LocalDateTime localDateTime = LocalDateTime.parse(timeProperty, dateTimeFormatter);
            return localDateTime.toInstant(ZoneOffset.UTC);
        } catch (DateTimeParseException ex){
            throw new DataFormatException("Wrong format");
        }
    }

    private static void changeKey(Field field, Property prop, Properties properties, Path propertiesPath) throws IOException {
        String temp = properties.getProperty(prop.name());
        properties.remove(prop.name());
        properties.put(field.getName(), temp);
        properties.store(new FileWriter(propertiesPath.toFile()), "Changed key");
    }
}
