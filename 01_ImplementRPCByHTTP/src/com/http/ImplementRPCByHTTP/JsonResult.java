package com.http.ImplementRPCByHTTP;

public class JsonResult {
	// Result status code
	private int resultCode;

	// Result message
	private String message;
	
	// Result object
	private Object result;
	
	public int getResultCode() {
		return resultCode;
	}
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
}
