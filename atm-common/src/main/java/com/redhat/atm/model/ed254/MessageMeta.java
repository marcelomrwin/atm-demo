//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.2 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.06.24 at 12:42:06 AM CEST 
//


package com.redhat.atm.model.ed254;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="creationTime" use="required" type="{urn:x-eurocae-example:service:aman:pub}TimestampType" /&gt;
 *       &lt;attribute name="isFirstAfterOutage" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="managedAerodrome" use="required" type="{urn:x-eurocae-example:service:aman:pub}icaoAerodromeType" /&gt;
 *       &lt;attribute name="publicationTime" use="required" type="{urn:x-eurocae-example:service:aman:pub}TimestampType" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "messageMeta")
public class MessageMeta {

    @XmlAttribute(name = "creationTime", required = true)
    protected XMLGregorianCalendar creationTime;
    @XmlAttribute(name = "isFirstAfterOutage", required = true)
    protected boolean isFirstAfterOutage;
    @XmlAttribute(name = "managedAerodrome", required = true)
    protected String managedAerodrome;
    @XmlAttribute(name = "publicationTime", required = true)
    protected XMLGregorianCalendar publicationTime;

    /**
     * Gets the value of the creationTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreationTime() {
        return creationTime;
    }

    /**
     * Sets the value of the creationTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreationTime(XMLGregorianCalendar value) {
        this.creationTime = value;
    }

    /**
     * Gets the value of the isFirstAfterOutage property.
     * 
     */
    public boolean isIsFirstAfterOutage() {
        return isFirstAfterOutage;
    }

    /**
     * Sets the value of the isFirstAfterOutage property.
     * 
     */
    public void setIsFirstAfterOutage(boolean value) {
        this.isFirstAfterOutage = value;
    }

    /**
     * Gets the value of the managedAerodrome property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getManagedAerodrome() {
        return managedAerodrome;
    }

    /**
     * Sets the value of the managedAerodrome property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setManagedAerodrome(String value) {
        this.managedAerodrome = value;
    }

    /**
     * Gets the value of the publicationTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPublicationTime() {
        return publicationTime;
    }

    /**
     * Sets the value of the publicationTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPublicationTime(XMLGregorianCalendar value) {
        this.publicationTime = value;
    }

}
