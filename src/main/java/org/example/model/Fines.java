package org.example.model;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Сделан для "красивого" вывода  xml, то что пришло)
 */
@JacksonXmlRootElement
public class Fines {

    @JacksonXmlProperty(localName = "Fine")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Fine> fines = new ArrayList<>();

    public List<Fine> getFines() {
        return fines;
    }

    public void setFines(List<Fine> fines) {
        this.fines = fines;
    }
}
