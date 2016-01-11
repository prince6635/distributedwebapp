package com.http.SerializationMethods;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

// Java serialization
public class JavaSerialization {

	public static void main(String[] args) throws IOException, ClassNotFoundException{
		Person zee = new Person();
		zee.setAddress("US");
		zee.setAge(29);
		zee.setBirth(new Date());
		zee.setName("Zee");
		
		// Serialization
		ByteArrayOutputStream os = new ByteArrayOutputStream(); // byte array output stream
		ObjectOutputStream out = new ObjectOutputStream(os); // object output stream
		// Write Person object to byte array output stream to serialize
		out.writeObject(zee);
		
		// Deserialization
		byte[] zeeBytes = os.toByteArray();
		ByteArrayInputStream is = new ByteArrayInputStream(zeeBytes);
		ObjectInputStream in = new ObjectInputStream(is);
		Person newZee = (Person)in.readObject();
		System.out.println("name: " + newZee.getName() + ", age: " + newZee.getAge());
	}

}
