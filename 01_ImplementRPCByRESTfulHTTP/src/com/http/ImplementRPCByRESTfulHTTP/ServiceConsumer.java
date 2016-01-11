/**
 * 
 */
package com.http.ImplementRPCByRESTfulHTTP;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * @Description: ��������� 
 * @Author chenkangxian   
 * @Date 2013-7-13 ����5:13:10 
 * @Copyright: 2012 chenkangxian, All rights reserved.
 **/
/*
 * ����url
 * http://localhost:8080/testrestfulrpc/consumer.json
 */
@Controller
public class ServiceConsumer {

	@ResponseBody
	@RequestMapping(value="/consumer", method=RequestMethod.GET)
	public JsonResult consume() throws ClientProtocolException, IOException{
		
		//����
		String service = "sayhelloservice";
		String format = "json";
		String arg1 = "hello";
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss"); 
		Date now = new Date();
		String nowStr = dateFormat.format(now);
		
		String url = "http://localhost:8080//testrestfulrpc/provider/" + service + "/" + nowStr + "." + format ;

		//��װ����
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		
		List <NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("arg1", arg1));
		httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

		//������Ӧ
		HttpResponse response = httpClient.execute(httpPost);

		HttpEntity entity = response.getEntity();
		byte[] bytes = EntityUtils.toByteArray(entity);
		String jsonresult = new String(bytes, "utf8");
		
		JsonResult jsonResult = (JsonResult)JsonUtil.jsonToObject(jsonresult, JsonResult.class);
		
		return jsonResult;
	}
}
