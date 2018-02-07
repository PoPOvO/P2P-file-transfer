package org.xli.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesReader {
	private static Map<String, String> propertiesMap;
	
	static {
		InputStream is = PropertiesReader.class.getClassLoader().getResourceAsStream("./net_config.properties");
		if (is != null) {
			propertiesMap = new HashMap<>();
			Properties properties = new Properties();
			try {
				properties.load(is);
			} catch (IOException e) {
			}
			@SuppressWarnings("unchecked")
			Enumeration<String> enums = (Enumeration<String>) properties.propertyNames();
			while (enums.hasMoreElements()) {
				String key = enums.nextElement();
				
				if (key != null) {
					propertiesMap.put(key, properties.getProperty(key));
				}
			}
		}
	}
	
	public static String getProperty(String key) {
		return propertiesMap.get(key);
	}
}
