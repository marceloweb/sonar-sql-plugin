//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.03.25 at 09:04:39 PM EET 
//


package org.sonar.plugins.sql.models.rules;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.plexus.util.StringUtils;


/**
 * <p>Java class for textCheckType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="textCheckType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Default"/>
 *     &lt;enumeration value="Contains"/>
 *     &lt;enumeration value="Regexp"/>
 *     &lt;enumeration value="Strict"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "textCheckType")
@XmlEnum
public enum TextCheckType {

    @XmlEnumValue("Default")
    DEFAULT("Default"),
    @XmlEnumValue("Contains")
    CONTAINS("Contains"),
    @XmlEnumValue("Regexp")
    REGEXP("Regexp"),
    @XmlEnumValue("Strict")
    STRICT("Strict");
    private final String value;

    TextCheckType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TextCheckType fromValue(String v) {
        if (StringUtils.isBlank(v)) {
            return TextCheckType.DEFAULT;
        }
        
        for (TextCheckType c: TextCheckType.values()) {
            if (c.value.equalsIgnoreCase(v) || c.name().equalsIgnoreCase(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
