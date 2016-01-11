package com.http.SerializationMethods;

import java.util.Date;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XMLSerialization {

	public static void main(String[] args) {
		Person zee = new Person();
		zee.setAddress("US");
		zee.setAge(29);
		zee.setBirth(new Date());
		zee.setName("Zee");
		
		// Serialization
		XStream xStream = new XStream(new DomDriver());
		xStream.alias("person", Person.class);
		String personXML = xStream.toXML(zee);
		
		// Deserialization
		Person newZee = (Person)xStream.fromXML(personXML);
		
		System.out.println(personXML);
		System.out.println(newZee.getBirth());	
	}

}
