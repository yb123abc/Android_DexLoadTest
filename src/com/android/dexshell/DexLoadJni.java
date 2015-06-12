package com.android.dexshell;

import android.app.Application;
import android.content.Context;

public class DexLoadJni {
	
	static
	{
		System.loadLibrary("DexLoadJni");
	}
	
	
	static public native void changeClassLoader(Context context);
	
	static public native void changeApplication(Context context, Application application);

}
