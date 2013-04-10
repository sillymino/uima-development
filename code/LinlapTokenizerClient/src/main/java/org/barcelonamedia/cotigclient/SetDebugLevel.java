
package org.barcelonamedia.cotigclient;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="newDebugLevel" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "newDebugLevel"
})
@XmlRootElement(name = "setDebugLevel")
public class SetDebugLevel {

    protected int newDebugLevel;

    /**
     * Gets the value of the newDebugLevel property.
     * 
     */
    public int getNewDebugLevel() {
        return newDebugLevel;
    }

    /**
     * Sets the value of the newDebugLevel property.
     * 
     */
    public void setNewDebugLevel(int value) {
        this.newDebugLevel = value;
    }

}
