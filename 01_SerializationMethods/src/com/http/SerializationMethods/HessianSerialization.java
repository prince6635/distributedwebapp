package com.http.SerializationMethods;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

public class HessianSerialization {

	public static void main(String[] args) throws IOException {
		Person zee = new Person();
		zee.setAddress("US");
		zee.setAge(29);
		zee.setBirth(new Date());
		zee.setName("Zee");
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		// Serialization
		HessianOutput ho = new HessianOutput(os);
		ho.writeObject(zee);
		byte[] zeeBytes = os.toByteArray();
		
		ByteArrayInputStream is = new ByteArrayInputStream(zeeBytes);
		// Deserialization
		HessianInput hi = new HessianInput(is);
		Person newZee = (Person)hi.readObject();
		
		System.out.println("name : " + newZee.getName() + ", age : " + newZee.getAge());
	}

}
