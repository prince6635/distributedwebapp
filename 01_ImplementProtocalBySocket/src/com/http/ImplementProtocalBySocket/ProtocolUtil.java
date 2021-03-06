package com.http.ImplementProtocalBySocket;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class ProtocolUtil {

	public static Request readRequest(InputStream input) throws IOException {
		// read encode
		byte[] encodeBytes = new byte[1];
		input.read(encodeBytes);
		byte encode = encodeBytes[0];

		// read command length
		byte[] commandLengthBytes = new byte[4];
		input.read(commandLengthBytes);
		int commandLength = ByteUtil.bytes2Int(commandLengthBytes);
		
		// read command
		byte[] commandBytes = new byte[4];
		input.read(commandBytes);
		String command = "";
		if(Encode.GBK.getValue() == encode){
			command = new String(commandBytes,"GBK");
		}else{
			command = new String(commandBytes,"UTF8");
		}
		
		// create Request object
		Request request = new Request();
		request.setCommand(command);
		request.setEncode(encode);
		request.setCommandLength(commandLength);
		
		return request;
	}
	

	public static Response readResponse(InputStream input) throws IOException {
		// read encode
		byte[] encodeByte = new byte[1];
		input.read(encodeByte);
		byte encode = encodeByte[0];
		
		// read response length
		byte[] responseLengthByte = new byte[4];
		input.read(responseLengthByte);
		int responseLength = ByteUtil.bytes2Int(responseLengthByte);
		
		// read response content
		byte[] responseBytes = new byte[responseLength];
		input.read(responseBytes);
		String responseStr = "";
		
		// set encode
		if(Encode.GBK.getValue() == encode){
			responseStr = new String(responseBytes,"GBK");
		}else{
			responseStr = new String(responseBytes,"UTF8");
		}
				
		// create response object
		Response response = new Response();
		response.setEncode(encode);
		response.setResponse(responseStr);
		response.setResponseLength(responseLength);
		
		return response;
	}

	public static void writeRequest(OutputStream output, Request request) throws IOException{
		// send request to the server 
		output.write(request.getEncode());
		output.write(ByteUtil.int2ByteArray(request.getCommandLength()));
		
		if(Encode.GBK.getValue() == request.getEncode()){
			output.write(request.getCommand().getBytes("GBK"));
		}else{
			output.write(request.getCommand().getBytes("UTF8"));
		}
		
		output.flush();
	}

	public static void writeResponse(OutputStream output, Response response) throws IOException{
		// send response to the client
		output.write(response.getEncode());
		//output.write(response.getResponseLength()); 会截取低8位传输，丢弃高24位
		output.write(ByteUtil.int2ByteArray(response.getResponseLength()));
		if(Encode.GBK.getValue() == response.getEncode()){
			output.write(response.getResponse().getBytes("GBK"));
		}else{
			output.write(response.getResponse().getBytes("UTF8"));
		}
		
		output.flush();	
	}
}
