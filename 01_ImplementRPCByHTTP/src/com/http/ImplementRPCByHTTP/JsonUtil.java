package com.http.ImplementRPCByHTTP;

import java.io.StringWriter;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;

public class JsonUtil {

	private static final ObjectMapper mapper = new ObjectMapper();

	public static Object jsonToObject(String json, Class cls) {

		try{
			//允许json串里面的key value不带双引号
			mapper.configure(org.codehaus.jackson.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

			// 允许制定的object中的属性没有json串中某个key
			mapper.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			return mapper.readValue(json, cls);
			
		}catch(Exception e){}

		return null;

	}

	public static String getJson(Object object)  {

		try{
			String json = null;

			StringWriter sw = new StringWriter();
			JsonGenerator gen = new JsonFactory().createJsonGenerator(sw);
			mapper.writeValue(gen, object);
			gen.close();
			json = sw.toString();
			return json;
			
		}catch(Exception e){}

		return null;
	}
}
