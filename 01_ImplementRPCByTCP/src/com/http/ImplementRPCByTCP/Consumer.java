package com.http.ImplementRPCByTCP;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.UnknownHostException;

public class Consumer {
	
	public static void main(String[] args)  throws UnknownHostException, IOException, SecurityException, NoSuchMethodException, ClassNotFoundException{
		// Interface name
		String interfaceName = SayHelloService.class.getName();
		
		// The service method that needs to be called
		Method method = SayHelloService.class.getMethod("sayHello", java.lang.String.class);

		// Arguments that needs to be passed with the method
		Object[] arguments = {"hello"};
		
		// Pass service interface, method, and arguments remotely
		Socket socket = new Socket("127.0.0.1", 6688);
		ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
		output.writeUTF(interfaceName);
		output.writeUTF(method.getName());
		output.writeObject(method.getParameterTypes());
		output.writeObject(arguments);
		
		// Read the result after executing the service method remotely
		ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
		Object result = input.readObject();
		System.out.println(result);
		
	}

}
