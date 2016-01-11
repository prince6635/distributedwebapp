package com.http.ImplementRPCByHTTP;

import java.util.Map;

public interface BaseService {

	public Object execute(Map<String,Object> args);
}
