package com.example.dexloadtest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.android.dexshell.RefInvoke;

import dalvik.system.DexClassLoader;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.ArrayMap;

public class DexLoadApplication extends Application {

	String dexFileName;
	@Override
	protected void attachBaseContext(Context base) {
		// TODO Auto-generated method stub
		super.attachBaseContext(base);
		
		try {
			String dexPath = "/data/data/" + this.getPackageName() + "/cache";
			String libPath = "/data/data/" + this.getPackageName() + "/lib";
			File dexPathFile = new File(dexPath);
			if(!dexPathFile.exists())
			{
				dexPathFile.mkdir();
			}
			
			dexFileName = dexPath + "/" +"classes.jar";
			File dexFile = new File(dexFileName);
			if(!dexFile.exists())
			{
				
					dexFile.createNewFile();
					byte[] dexdata = this.readDexFile();
					this.copyDexFile(dexdata);
				
			}
			
			//配置动态加载程序
			Object currentActivityThread = RefInvoke.invokeStaticMethod(
					"android.app.ActivityThread", "currentActivityThread", new Class[]{}, new Object[]{});
			String packageName = this.getPackageName();
			ArrayMap mPackages = (ArrayMap) RefInvoke.getFieldObject(
					"android.app.ActivityThread", currentActivityThread, "mPackages");
			WeakReference wr = (WeakReference) mPackages.get(packageName);
			DexClassLoader dLoader = new DexClassLoader(dexFileName, dexPath, 
					libPath, (ClassLoader)RefInvoke.getFieldObject(
							"android.app.LoadedApk", wr.get(), "mClassLoader"));
			RefInvoke.setFieldObject("android.app.LoadedApk", "mClassLoader", 
					wr.get(), dLoader);
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		//如果源应用配置有Application对象,则替换为原应用Applicaiton,以便不影响源程序逻辑
				String appClassName = null;
				
				try {
					ApplicationInfo ai = this.getPackageManager().getApplicationInfo(
							this.getPackageName(), PackageManager.GET_META_DATA);
					Bundle bundle = ai.metaData;
					if(bundle!=null && bundle.containsKey("APPLICATION_CLASS_NAME"))
					{
						appClassName = bundle.getString("APPLICATION_CLASS_NAME");
					}
					else
					{
						return;
					}
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Object currentActivityThread = RefInvoke.invokeStaticMethod(
						"android.app.ActivityThread", "currentActivityThread", 
						new Class[]{}, new Object[]{});
				Object mBoundApplication = RefInvoke.getFieldObject(
						"android.app.ActivityThread", currentActivityThread, "mBoundApplication");;
				Object loadedApkInfo = RefInvoke.getFieldObject(
						"android.app.ActivityThread$AppBindData", 
						mBoundApplication, "info");
				RefInvoke.setFieldObject(
						"android.app.LoadedApk", "mApplication", 
						loadedApkInfo,	null);
				Object oldApplication = RefInvoke.getFieldObject(
						"android.app.ActivityThread", currentActivityThread, 
						"mInitialApplication");
//				ArrayList<Application> mAllApplications = (ArrayList<Application>) RefInvoke
//						.getFieldObject("android.app.ActivityThread", currentActivityThread, "mApplications");
				ApplicationInfo appinfo_In_LoadedApk = (ApplicationInfo) RefInvoke
						.getFieldObject("android.app.LoadedApk", loadedApkInfo, 
								"mApplicationInfo");
				ApplicationInfo appinfo_In_AppBindData = (ApplicationInfo) RefInvoke
						.getFieldObject("android.app.ActivityThread$AppBindData", 
								mBoundApplication, "appInfo");
				appinfo_In_LoadedApk.className = appClassName;
				appinfo_In_AppBindData.className = appClassName;
				Application app = (Application) RefInvoke.invokeMethod("android.app.LoadedApk", 
						"makeApplication", loadedApkInfo, 
						new Class[] {boolean.class, Instrumentation.class},
						new Object[] {false, null});
				RefInvoke.setFieldObject("android.app.ActivityThread", 
						"mInitialApplication", currentActivityThread, app);
				
				ArrayMap mProviderMap = (ArrayMap) RefInvoke.getFieldObject(
						"android.app.ActivityThread", currentActivityThread, 
						"mProviderMap");
				Iterator it = mProviderMap.values().iterator();
				while(it.hasNext())
				{
					Object providerClientRecord = it.next();
					Object localProvider = RefInvoke.getFieldObject(
							"android.app.ActivityThread$ProviderClientRecord", 
							providerClientRecord, "mLocalProvider");
					RefInvoke.setFieldObject("android.content.ContentProvider", 
							"mContext", localProvider, app);
				}
				app.onCreate();
		
	}

	
	private  byte[] readDexFile()
	{
		ByteArrayOutputStream dexByteArrayOutputStream = new ByteArrayOutputStream();
		InputStream is = null;
		
		try {
			String strSourceDir = this.getApplicationInfo().sourceDir;
			JarFile localJarFile = new JarFile(strSourceDir);
			Enumeration<JarEntry> localJarEntries= localJarFile.entries();
			while(localJarEntries.hasMoreElements())
			{
				JarEntry localJarEntry = localJarEntries.nextElement();
				if (localJarEntry.getName().equals("assets/classes.jar"))
				{
					is = localJarFile.getInputStream(localJarEntry);
				}
			}
			
			if (is!=null)
			{
				int i;
				byte arrayOfByte[] = new byte[1024];
				while(true)
				{
					i = is.read(arrayOfByte);
					if(i == -1)
					{
						break;
					}
					dexByteArrayOutputStream .write(arrayOfByte, 0, i);
				}
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return decrypt(dexByteArrayOutputStream.toByteArray());
	}
	
	private byte[] decrypt(byte[] data)
	{
		return data;
	}
	
	private void copyDexFile(byte[] dataByte)
	{
		
		File dexFile = new File(dexFileName);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(dexFile);
			fos.write(dataByte);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}


