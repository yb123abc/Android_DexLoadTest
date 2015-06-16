package com.android.dexshell;

import android.app.Application;
import android.content.Context;

public class DexLoadJni {
	
	static
	{
		System.loadLibrary("DexLoadJni");
	}
	
	
	static public native void changeClassLoader(Context context, int os_version);
	
	static public native void changeApplication(Context context, String strApplication, int os_version);

}
