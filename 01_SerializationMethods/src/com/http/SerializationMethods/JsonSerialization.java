package com.http.SerializationMethods;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;

public class JsonSerialization {

	public static void main(String[] args) throws IOException {
		Person zee = new Person();
		zee.setAddress("US");
		zee.setAge(29);
		zee.setBirth(new Date());
		zee.setName("Zee");
		
		// Serialization
		String zeeJson = null;
		ObjectMapper mapper = new ObjectMapper();
		StringWriter sw = new StringWriter();
		JsonGenerator gen = new JsonFactory().createJsonGenerator(sw);
		mapper.writeValue(gen, zee);
		gen.close();
		zeeJson = sw.toString();
		
		// Deserialization
		Person newZee = (Person)mapper.readValue(zeeJson, Person.class);
		System.out.println(zeeJson);
		System.out.println(newZee.getName());
	}

}
