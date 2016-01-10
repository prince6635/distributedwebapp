package com.http.ImplementProtocalBySocket;

// Protocol request data class
public class Request {
	private byte encode; 
	public byte getEncode() {
		return encode;
	}
	public void setEncode(byte encode) {
		this.encode = encode;
	}
	
	private String command;
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}

	private int commandLength;
	public int getCommandLength() {
		return commandLength;
	}
	public void setCommandLength(int commandLength) {
		this.commandLength = commandLength;
	}
}
