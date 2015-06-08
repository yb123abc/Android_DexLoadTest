package com.example.dexloadtest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import dalvik.system.DexClassLoader;

public class MyClassLoader extends DexClassLoader {

	private ClassLoader mClassLoader = null;
	
	public MyClassLoader(String dexPath, String optimizedDirectory,
			String libraryPath, ClassLoader parent) {
		super(dexPath, optimizedDirectory, libraryPath, parent);
		// TODO Auto-generated constructor stub
		this.mClassLoader = parent;
		Thread.currentThread().setContextClassLoader(this);
		
	}

	@Override
	public URL getResource(String resName) {
		// TODO Auto-generated method stub
		return this.mClassLoader.getResource(resName);
	}

	@Override
	public Enumeration<URL> getResources(String resName) throws IOException {
		// TODO Auto-generated method stub
		return this.mClassLoader.getResources(resName);
	}

	@Override
	public InputStream getResourceAsStream(String resName) {
		// TODO Auto-generated method stub
		return this.mClassLoader.getResourceAsStream(resName);
	}
	
	

}
