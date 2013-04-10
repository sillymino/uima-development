
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
 *         &lt;element name="sourceText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dialect" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="chekTypo" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="chekSpell" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="checkGram" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "sourceText",
    "dialect",
    "chekTypo",
    "chekSpell",
    "checkGram"
})
@XmlRootElement(name = "CorrectWithParameters_x0020_")
public class CorrectWithParametersX0020 {

    protected String sourceText;
    protected int dialect;
    protected boolean chekTypo;
    protected boolean chekSpell;
    protected boolean checkGram;

    /**
     * Gets the value of the sourceText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceText() {
        return sourceText;
    }

    /**
     * Sets the value of the sourceText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceText(String value) {
        this.sourceText = value;
    }

    /**
     * Gets the value of the dialect property.
     * 
     */
    public int getDialect() {
        return dialect;
    }

    /**
     * Sets the value of the dialect property.
     * 
     */
    public void setDialect(int value) {
        this.dialect = value;
    }

    /**
     * Gets the value of the chekTypo property.
     * 
     */
    public boolean isChekTypo() {
        return chekTypo;
    }

    /**
     * Sets the value of the chekTypo property.
     * 
     */
    public void setChekTypo(boolean value) {
        this.chekTypo = value;
    }

    /**
     * Gets the value of the chekSpell property.
     * 
     */
    public boolean isChekSpell() {
        return chekSpell;
    }

    /**
     * Sets the value of the chekSpell property.
     * 
     */
    public void setChekSpell(boolean value) {
        this.chekSpell = value;
    }

    /**
     * Gets the value of the checkGram property.
     * 
     */
    public boolean isCheckGram() {
        return checkGram;
    }

    /**
     * Sets the value of the checkGram property.
     * 
     */
    public void setCheckGram(boolean value) {
        this.checkGram = value;
    }

}
