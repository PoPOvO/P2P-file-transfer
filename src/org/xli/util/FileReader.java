package org.xli.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileReader {
	public static String readFile(String filePath) {
		File file = new File(filePath);
		FileInputStream fis = null;
		
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
		}
		
		byte[] setion = new byte[1024];
		StringBuilder sb = new StringBuilder();
		
		try {
			while (fis.read(setion, 0, setion.length) > 0) {
				String temp = new String(setion).trim();
				sb.append(temp);
			}

			return sb.toString();
		} catch (IOException e) {
		} finally {
			try {
				if (fis != null) {
					fis.close();					
				}
			} catch (IOException e) {
			}
		}
		
		return null;
	}
}
