package com.http.ImplementRPCByHTTP;

import java.util.Map;

public class SayHelloService implements BaseService {

	@Override
	public Object execute(Map<String, Object> args) {
		// NOTE: request.getParameterMap() is an array
		String[] helloArg = (String[])args.get("arg1");
		
		if("hello".equals(helloArg[0])){
			return "hello";
		}else{
			return "bye bye";
		}
	}

}
