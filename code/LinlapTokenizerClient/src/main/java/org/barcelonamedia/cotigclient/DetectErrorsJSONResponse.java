
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
 *         &lt;element name="DetectErrorsJSONResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "detectErrorsJSONResult"
})
@XmlRootElement(name = "DetectErrorsJSONResponse")
public class DetectErrorsJSONResponse {

    @XmlElement(name = "DetectErrorsJSONResult")
    protected String detectErrorsJSONResult;

    /**
     * Gets the value of the detectErrorsJSONResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDetectErrorsJSONResult() {
        return detectErrorsJSONResult;
    }

    /**
     * Sets the value of the detectErrorsJSONResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDetectErrorsJSONResult(String value) {
        this.detectErrorsJSONResult = value;
    }

}
