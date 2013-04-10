
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
 *         &lt;element name="DetectCorrectErrorsResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "detectCorrectErrorsResult"
})
@XmlRootElement(name = "DetectCorrectErrorsResponse")
public class DetectCorrectErrorsResponse {

    @XmlElement(name = "DetectCorrectErrorsResult")
    protected String detectCorrectErrorsResult;

    /**
     * Gets the value of the detectCorrectErrorsResult property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDetectCorrectErrorsResult() {
        return detectCorrectErrorsResult;
    }

    /**
     * Sets the value of the detectCorrectErrorsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDetectCorrectErrorsResult(String value) {
        this.detectCorrectErrorsResult = value;
    }

}
