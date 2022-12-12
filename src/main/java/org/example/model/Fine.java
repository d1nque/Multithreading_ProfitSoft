package org.example.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * Удобно принимать значения и так же маршить красив)
 */
@JacksonXmlRootElement(localName = "Fine")
public class Fine {

    @JacksonXmlProperty
    public String type;
    @JacksonXmlProperty
    public double fine_amount;

    public Fine(String type, double fine_amount) {
        this.type = type;
        this.fine_amount = fine_amount;
    }
    public Fine() {
        this.type = null;
        this.fine_amount = 0;
    }

    @Override
    public String toString() {
        return "Fine{" +
                "type='" + type + '\'' +
                ", fine_amount=" + fine_amount +
                '}';
    }
}
