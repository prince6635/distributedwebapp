/**
 * 
 */
package com.http.ImplementRPCByRESTfulHTTP;

import java.util.Map;

/**
 * @Description: �����ӿ� 
 * @Author chenkangxian   
 * @Date 2013-6-24 ����8:23:26 
 * @Copyright: 2012 chenkangxian, All rights reserved.
 **/
public interface BaseService {

	public Object execute(Map<String,Object> args);
}
