
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
 *         &lt;element name="AddToIgnoreErrorsResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "addToIgnoreErrorsResult"
})
@XmlRootElement(name = "AddToIgnoreErrorsResponse")
public class AddToIgnoreErrorsResponse {

    @XmlElement(name = "AddToIgnoreErrorsResult")
    protected String addToIgnoreErrorsResult;

    /**
     * Gets the value of the addToIgnoreErrorsResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAddToIgnoreErrorsResult() {
        return addToIgnoreErrorsResult;
    }

    /**
     * Sets the value of the addToIgnoreErrorsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAddToIgnoreErrorsResult(String value) {
        this.addToIgnoreErrorsResult = value;
    }

}
