package com.mec.deal_mapping.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class PackageScanner {
	public PackageScanner() {
	}
	
	public abstract void dealClass(Class<?> klass);
	
//    1:com.mec:
//	2:E:\JavaEE\后台\Java后台开发基础&JavaSE提高
//	\Test\20171018@IoC@Bean工厂已完成\bin\com\mec
	private void dealPackage(URL url) {
		try {
			JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
			JarFile jarFile = jarURLConnection.getJarFile();
			Enumeration<JarEntry> jarEntries = jarFile.entries();
			while(jarEntries.hasMoreElements()) {
				JarEntry jarEntry = jarEntries.nextElement();
				if(jarEntry.isDirectory()) {
					continue;
				}
				String entryName = jarEntry.getName();
				int dotIndex = entryName.indexOf(".class");
				if(dotIndex == -1 
						|| !entryName.endsWith(".class")
						|| entryName.contains("$")
						|| entryName.endsWith("Exception.class")) {
					continue;
				}
				String className = entryName.substring(0, dotIndex);
				className = className.replace("/", ".");
				try {
					Class<?> klass = Class.forName(className);
					dealClass(klass);
				} catch (ClassNotFoundException e) {
				}
			}
		} catch (IOException e) {
		}
	}
	
	private void dealPackage(String packageName, String path) {
		File curFile = new File(path);
		//返回一个目录下所有文件和目录的绝对路径
		curFile.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				if(file.isFile()) {
					String fileName = file.getName();
					int index = fileName.indexOf(".class");
					if(index == -1 || !fileName.endsWith(".class")) {
						return false;
					}
					String simpleClassName = fileName.substring(0, index);
					String className = packageName + "." + simpleClassName;
					try {
						Class<?> clazz = Class.forName(className);
						dealClass(clazz);
					} catch (ClassNotFoundException e) {
					}
					
					return false;
				} else if(file.isDirectory()) {
					String dirName = file.getName();
					String nextPackageName = packageName + "." + dirName;
					String pathName = path + "\\" + dirName;
					dealPackage(nextPackageName, pathName);
					
					return false;
				}
				return false;
			}
		});
	}
	
	public void startScan(Class<?> klass) {
		if(klass == null) {
			return;
		}
		startScan(klass.getPackage().getName());
	}
	
	public void startScan(String packageName) {
		String name = packageName.replace(".", "/");
		
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		try {
			Enumeration<URL> urls = classLoader.getResources(name);
			while(urls.hasMoreElements()) {
				URL url = urls.nextElement();
				String protocol = url.getProtocol();
				if(protocol.equals("file")) {
					File file;
					try {
						file = new File(url.toURI());
						String absolutePath = file.getAbsolutePath();
						dealPackage(packageName, absolutePath);
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
				} else if(protocol.equals("jar")) {
					dealPackage(url);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
