package com.example.dexloadtest;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import dalvik.system.DexClassLoader;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

public class ProxyApplication extends Application {

	private static final String appkey = "APPLICATION_CLASS NAME";
	private String apkFileName;
	private String odexPath;
	private String libPath;
	
	protected void attachBaseContext(Context base)
	{
		super.attachBaseContext(base);
		try{
			File odex = this.getDir("payload_odex", MODE_PRIVATE);
			File libs = this.getDir("payload_lib", MODE_PRIVATE	);
			odexPath = odex.getAbsolutePath();
			libPath = libs.getAbsolutePath();
			apkFileName = odex.getAbsolutePath() + "/payload.apk";
			File dexFile = new File(apkFileName);
			if(!dexFile.exists())
			{
				dexFile.createNewFile();
				//读取程序粗拉萨色素.dex文件
				byte[] dexdata = this.readDexFileFromApk();
				//分离出解壳后的apk文件已用于动态加载
				this.splitPayLoadFromDex(dexdata);
				//配置动态加载程序
				Object currentActivityThread = RefInvoke.invokeStaticMethod(
						"android.app.ActivityThread", "currentActivityThread", new Class[]{}, new Object[]{});
				String packageName = this.getPackageName();
				HashMap mPackages = (HashMap) RefInvoke.getFieldObject(
						"android.app.ActivityThread", currentActivityThread, "mPackages");
				WeakReference wr = (WeakReference) mPackages.get(packageName);
				DexClassLoader dLoader = new DexClassLoader(apkFileName, odexPath, 
						libPath, (ClassLoader)RefInvoke.getFieldObject(
								"android.app.LoadedApk", wr.get(), "mClassLoader"));
				RefInvoke.setFieldObject("android.app.LoadedApk", "mClassLoader", 
						wr.get(), dLoader);
						
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void onCreate()
	{
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
		ArrayList<Application> mAllApplications = (ArrayList<Application>) RefInvoke
				.getFieldObject("android.app.ActivityThread", currentActivityThread, "mApplications");
		ApplicationInfo appinfo_In_LoadedApk = (ApplicationInfo) RefInvoke
				.getFieldObject("android.app.LoadedApk", loadedApkInfo, 
						"mApplicationInfo");
		ApplicationInfo appinfo_In_AppBindData = (ApplicationInfo) RefInvoke
				.getFieldObject("android.app.Activitythread$AppBindData", 
						mBoundApplication, "appInfo");
		appinfo_In_LoadedApk.className = appClassName;
		appinfo_In_AppBindData.className = appClassName;
		Application app = (Application) RefInvoke.invokeMethod("android.app.LoadedApk", 
				"makeApplication", loadedApkInfo, 
				new Class[] {boolean.class, Instrumentation.class},
				new Object[] {false, null});
		RefInvoke.setFieldObject("android.app.ActivityThread", 
				"mInitialApplication", currentActivityThread, app);
		
		HashMap mProviderMap = (HashMap) RefInvoke.getFieldObject(
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
	
	
	private byte[] readDexFileFromApk() throws IOException
	{
		ByteArrayOutputStream dexByteArrayOutputStream = new ByteArrayOutputStream();
		ZipInputStream localZipInputStream = new ZipInputStream(
				new BufferedInputStream(new FileInputStream(
						this.getApplicationInfo().sourceDir)));
		while(true)
		{
			ZipEntry localZipEntry = localZipInputStream.getNextEntry();
			if(localZipEntry == null)
			{
				localZipInputStream.close();
				break;
			}
			if(localZipEntry.getName().equals("classes.dex"))
			{
				byte[] arrayOfByte = new byte[1024];
				while(true)
				{
					int i = localZipInputStream.read(arrayOfByte);
					if(i == -1)
					{
						break;
					}
					dexByteArrayOutputStream.write(arrayOfByte, 0, i);
				}
			}
			localZipInputStream.closeEntry();
		}
		localZipInputStream.closeEntry();
		return dexByteArrayOutputStream.toByteArray();
	}
	
	private void splitPayLoadFromDex(byte[] data) throws IOException
	{
		byte[] apkdata = decrypt(data);
		int ablen = apkdata.length;
		byte[] dexlen = new byte[4];
		System.arraycopy(apkdata,  ablen -4, dexlen, 0, 4);
		ByteArrayInputStream bais = new ByteArrayInputStream(dexlen);
		DataInputStream in = new DataInputStream(bais);
		//获得数据长度
		int readInt = in.readInt();
		System.out.println(Integer.toHexString(readInt));
		
		//提取数据,保存文件
		byte[] newdex = new byte[readInt];
		System.arraycopy(apkdata, ablen-4-readInt, newdex, 0, readInt);
		File file = new File(apkFileName);
		try
		{
			FileOutputStream localFileOutputStream = new FileOutputStream(file);
			localFileOutputStream.write(newdex);
			localFileOutputStream.close();
			
		} catch(IOException e)
		{
			throw new RuntimeException(e);
		}
		
		//从文件中提取so文件
		ZipInputStream localZipInputStream = new ZipInputStream(
				new BufferedInputStream(new FileInputStream(file)));
		while(true)
		{
			ZipEntry localZipEntry = localZipInputStream.getNextEntry();
			if(localZipEntry == null)
			{
				localZipInputStream.close();
				break;
			}
			String name = localZipEntry.getName();
			if(name.startsWith("lib/") && name.endsWith(".so"))
			{
				File storeFile = new File(libPath + "/" 
						+ name.substring(name.lastIndexOf('/')));
				storeFile.createNewFile();
				FileOutputStream fos = new FileOutputStream(storeFile);
				byte[] arrayOfByte = new byte[1024];
				while(true)
				{
					int i = localZipInputStream.read(arrayOfByte);
					if(i == -1)
					{
						break;
					}
					fos.write(arrayOfByte, 0, i);
				}
				fos.flush();
				fos.close();
			}
			localZipInputStream.closeEntry();
		}
		localZipInputStream.close();
		
		
	}
	
	private byte[] decrypt(byte[] data)
	{
		return data;
	}
}
