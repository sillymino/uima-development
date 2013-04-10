
package org.barcelonamedia.cotigclient;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="CorrectWithParameters_x0020_Result" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "correctWithParametersX0020Result"
})
@XmlRootElement(name = "CorrectWithParameters_x0020_Response")
public class CorrectWithParametersX0020Response {

    @XmlElement(name = "CorrectWithParameters_x0020_Result")
    protected String correctWithParametersX0020Result;

    /**
     * Gets the value of the correctWithParametersX0020Result property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCorrectWithParametersX0020Result() {
        return correctWithParametersX0020Result;
    }

    /**
     * Sets the value of the correctWithParametersX0020Result property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCorrectWithParametersX0020Result(String value) {
        this.correctWithParametersX0020Result = value;
    }

}
