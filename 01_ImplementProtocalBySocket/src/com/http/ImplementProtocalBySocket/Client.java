package com.http.ImplementProtocalBySocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

// Client side
public class Client {

	public static void main(String[] args) throws UnknownHostException, IOException {
		// Request
		Request request = new Request();
		request.setCommand("Hello");
		request.setCommandLength(request.getCommand().length());
		request.setEncode(Encode.UTF8.getValue());
		
		System.out.println("commandLength: " + request.getCommandLength());
		System.out.println("command: " + request.getCommand());
		
		Socket client = new Socket("127.0.0.1", 6688);
		OutputStream output = client.getOutputStream();		
		
		// Send request
		ProtocolUtil.writeRequest(output, request);
		
		// Read response data
		InputStream input = client.getInputStream();
		Response response = ProtocolUtil.readResponse(input);
		client.shutdownOutput();
		
		System.out.println("responselength : " + response.getResponseLength());
		System.out.println("response : " + response.getResponse());
	}

}
