//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.2 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.06.24 at 12:42:06 AM CEST 
//


package com.redhat.atm.model.ed254;

import java.math.BigInteger;
import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
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
 *       &lt;sequence&gt;
 *         &lt;element ref="{urn:x-eurocae-example:service:aman:pub}aptto"/&gt;
 *         &lt;choice minOccurs="0"&gt;
 *           &lt;element ref="{urn:x-eurocae-example:service:aman:pub}pfl"/&gt;
 *           &lt;element ref="{urn:x-eurocae-example:service:aman:pub}ttl"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="order" use="required" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" /&gt;
 *       &lt;attribute name="pointName" use="required" type="{urn:x-eurocae-example:service:aman:pub}icaoPointNameType" /&gt;
 *       &lt;attribute name="pointUsage" type="{urn:x-eurocae-example:service:aman:pub}PointUsageType" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "aptto",
    "pfl",
    "ttl"
})
@XmlRootElement(name = "point")
public class Point {

    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar aptto;
    protected String pfl;
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger ttl;
    @XmlAttribute(name = "order", required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger order;
    @XmlAttribute(name = "pointName", required = true)
    protected String pointName;
    @XmlAttribute(name = "pointUsage")
    protected PointUsageType pointUsage;

    /**
     * Gets the value of the aptto property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getAptto() {
        return aptto;
    }

    /**
     * Sets the value of the aptto property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setAptto(XMLGregorianCalendar value) {
        this.aptto = value;
    }

    /**
     * Gets the value of the pfl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPfl() {
        return pfl;
    }

    /**
     * Sets the value of the pfl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPfl(String value) {
        this.pfl = value;
    }

    /**
     * Gets the value of the ttl property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTtl() {
        return ttl;
    }

    /**
     * Sets the value of the ttl property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTtl(BigInteger value) {
        this.ttl = value;
    }

    /**
     * Gets the value of the order property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getOrder() {
        return order;
    }

    /**
     * Sets the value of the order property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setOrder(BigInteger value) {
        this.order = value;
    }

    /**
     * Gets the value of the pointName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPointName() {
        return pointName;
    }

    /**
     * Sets the value of the pointName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPointName(String value) {
        this.pointName = value;
    }

    /**
     * Gets the value of the pointUsage property.
     * 
     * @return
     *     possible object is
     *     {@link PointUsageType }
     *     
     */
    public PointUsageType getPointUsage() {
        return pointUsage;
    }

    /**
     * Sets the value of the pointUsage property.
     * 
     * @param value
     *     allowed object is
     *     {@link PointUsageType }
     *     
     */
    public void setPointUsage(PointUsageType value) {
        this.pointUsage = value;
    }

}
