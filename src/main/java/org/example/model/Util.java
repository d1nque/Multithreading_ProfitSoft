package org.example.model;

import org.example.annotation.Property;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Util {

    private String stringProperty;
    @Property(name = "numberProperty")
    private Integer numProperty;
    @Property(format = "dd.MM.yyyy HH:mm")
    private Instant timeProperty;

    public String getStringProperty() {
        return stringProperty;
    }

    public void setStringProperty(String stringProperty) {
        this.stringProperty = stringProperty;
    }

    public Integer getNumProperty() {
        return numProperty;
    }

    public void setNumProperty(Integer numberProperty) {
        this.numProperty = numberProperty;
    }

    public Instant getTimeProperty() {
        return timeProperty;
    }

    public void setTimeProperty(Instant timeProperty) {
        this.timeProperty = timeProperty;
    }

    public Util() {

    }

    @Override
    public String toString() {
        return "Util{" +
                "stringProperty='" + stringProperty + '\'' +
                ", numProperty=" + numProperty +
                ", timeProperty=" + timeProperty +
                '}';
    }
}
