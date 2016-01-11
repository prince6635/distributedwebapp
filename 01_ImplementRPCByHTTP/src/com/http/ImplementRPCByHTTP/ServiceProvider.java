package com.http.ImplementRPCByHTTP;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Service provider
public class ServiceProvider extends HttpServlet {

	private Map<String, BaseService> serviceMap;

	@Override
	public void init() throws ServletException {
		// Initialize server map
		serviceMap = new HashMap<String, BaseService>();
		serviceMap.put("com.http.sayHelloService", new SayHelloService());
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Basic parameters
		String serviceName = req.getParameter("service");
		String format = req.getParameter("format");
		
		Map parameters = req.getParameterMap();
		
		BaseService service = serviceMap.get(serviceName);
		Object result = service.execute(parameters);
		
		// Generate json result
		JsonResult jsonResult = new JsonResult();
		jsonResult.setResult(result);
		jsonResult.setMessage("success");
		jsonResult.setResultCode(200);
		
		String json = JsonUtil.getJson(jsonResult);
		resp.getWriter().write(json);
	}
}
