package com.http.ImplementRPCByRESTfulHTTP;

import java.util.Map;

public class SayHelloService implements BaseService{

	public Object execute(Map<String, Object> args) {
		//request.getParameterMap() ȡ����Ϊarray,�˴���Ҫע��
		String[] helloArg = (String[]) args.get("arg1");
		
		if("hello".equals(helloArg[0])){
			return "hello";
		}else{
			return "bye bye";
		}
	}

}
