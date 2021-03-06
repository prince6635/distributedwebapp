package com.http.ImplementProtocalBySocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

// Server side
public class Server {

	public static void main(String[] args) throws IOException {
			ServerSocket server = new ServerSocket(6688);
			
			while(true){
				Socket client = server.accept();
				
				// Read request data
				InputStream input = client.getInputStream();
				Request request = ProtocolUtil.readRequest(input);
				
				OutputStream output = client.getOutputStream();
				// Write response data
				Response response = new Response();				
				response.setEncode(Encode.UTF8.getValue());
				if(request.getCommand().equals("HELLO")){
					response.setResponse("hello!");
				}else{
					response.setResponse("bye bye!");
				}
				response.setResponseLength(response.getResponse().length());
				ProtocolUtil.writeResponse(output, response);

				client.shutdownOutput();
			}
	}

}
