package com.http.ImplementRPCByTCP;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

// Service provider
public class Provider {
	
	// All services
	private static Map<String, Object> services = new HashMap<String, Object>();
	static{
		services.put(SayHelloService.class.getName(), new SayHelloServiceImpl());
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{

		ServerSocket server = new ServerSocket(6688);
		
		while(true){
			Socket client = server.accept();
			
			// Read service info
			ObjectInputStream input = new ObjectInputStream(client.getInputStream());
			String interfaceName = input.readUTF(); // interface name
			String methodName = input.readUTF(); // method name
			Class<?>[] parameterTypes = (Class<?>[])input.readObject(); // parameter types
			Object[] arguments = (Object[])input.readObject(); // parameter objects
			
			// Execute service call
			Class serviceInterfaceClass = Class.forName(interfaceName); // get interface's class
			Object service = services.get(interfaceName); // get service implemented object
			Method method = serviceInterfaceClass.getMethod(methodName, parameterTypes); // get the serive method to call
			Object result = method.invoke(service, arguments);
			
			ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
			output.writeObject(result);
		}
	}

}
