/**
 * 
 */
package com.http.ImplementRPCByRESTfulHTTP;

import java.io.StringWriter;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * @Description: json������ 
 * @Author chenkangxian   
 * @Date 2013-6-24 ����8:47:31 
 * @Copyright: 2012 chenkangxian, All rights reserved.
 **/
public class JsonUtil {

	private static final ObjectMapper mapper = new ObjectMapper();

	public static Object jsonToObject(String json, Class cls) {

		try{
			//����json�������key value����˫���
			mapper.configure(org.codehaus.jackson.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

			// �����ƶ���object�е�����û��json����ĳ��key
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
