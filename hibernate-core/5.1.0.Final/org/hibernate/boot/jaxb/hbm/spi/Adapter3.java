//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.02.10 at 11:20:59 AM CST 
//


package org.hibernate.boot.jaxb.hbm.spi;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;

public class Adapter3
    extends XmlAdapter<String, ExecuteUpdateResultCheckStyle>
{


    public ExecuteUpdateResultCheckStyle unmarshal(String value) {
        return (org.hibernate.boot.jaxb.hbm.internal.ExecuteUpdateResultCheckStyleConverter.fromXml(value));
    }

    public String marshal(ExecuteUpdateResultCheckStyle value) {
        return (org.hibernate.boot.jaxb.hbm.internal.ExecuteUpdateResultCheckStyleConverter.toXml(value));
    }

}