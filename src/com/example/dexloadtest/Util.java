package com.example.dexloadtest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import android.content.ContentProvider;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Build;

public class Util {
	
	  private static String BUILD_TIME;
	  public static String CPUABI;
	  private static String VERSION_NAME;
	  private static ClassLoader cl = null;
	  static byte[] hexDigits;
	  public static boolean isX86;
	  private static ArrayList<ContentProvider> ps;
	  public static Context x86Ctx;

	  static
	  {
	    VERSION_NAME = "1.0";
	    BUILD_TIME = "2015-06-0211:16:39";
	    ps = new ArrayList();
	    x86Ctx = null;
	    isX86 = false;
	    byte[] arrayOfByte = new byte[16];
	    arrayOfByte[0] = 48;
	    arrayOfByte[1] = 49;
	    arrayOfByte[2] = 50;
	    arrayOfByte[3] = 51;
	    arrayOfByte[4] = 52;
	    arrayOfByte[5] = 53;
	    arrayOfByte[6] = 54;
	    arrayOfByte[7] = 55;
	    arrayOfByte[8] = 56;
	    arrayOfByte[9] = 57;
	    arrayOfByte[10] = 65;
	    arrayOfByte[11] = 66;
	    arrayOfByte[12] = 67;
	    arrayOfByte[13] = 68;
	    arrayOfByte[14] = 69;
	    arrayOfByte[15] = 70;
	    hexDigits = arrayOfByte;
	    CPUABI = null;
	  }
	  
	  public static void runAll(Context paramContext)
	  {
		  x86Ctx = paramContext;
		  doCheck(paramContext);
		  checkUpdate(paramContext);
		  
		  File localFile = new File("/data/data/" + paramContext.getPackageName() + "/.cache/");
		  if(!localFile.exists())
		  {
			  localFile.mkdir();
		  }
		  checkX86(paramContext);
		  CopyBinaryFile(paramContext);
		  createChildProcess(paramContext);
		  tryDo(paramContext);
		  runPkg(paramContext, paramContext.getPackageName());
	  }
	  
	  public static void doCheck(Context paramContext) 
	  {
		  //获得应用信息
		  ApplicationInfo localApplicationInfo = paramContext.getApplicationInfo();
		  File localFile = new File(localApplicationInfo.dataDir, ".md5");
		  String str1 = "/data/dalvik-cache/";
		  byte[] arrayOfByte1 = null;
		  if (new File("/data/dalvik-cache/arm/").exists())
		  {
			  str1 = "/data/dalvik-cache/arm/";
		  }
		  StringTokenizer localStringTokenizer = 
				  new StringTokenizer(new String(localApplicationInfo.sourceDir), "/");
		  
		  String str2;

		  try {
			  
			  if(!localStringTokenizer.hasMoreTokens())
			  {
				  str2 = new StringBuilder(String.valueOf(str1)).append("classes").toString() + ".dex";
				  
				  arrayOfByte1 = toASC(calFileMD5(str2));
			  }
			  
			  
			  if (!localFile.exists())
			  {
				  if(arrayOfByte1 !=null )
				  {
					 
						  FileOutputStream localFileOutputStream2 = new FileOutputStream(localFile);
						localFileOutputStream2.write(arrayOfByte1);
						localFileOutputStream2.close();
					  
				  }
				  
				  deleteDirectory(new File("/data/data/" + paramContext.getPackageName() + "/.cache/"));				 
				 
			  }
			  
		  } catch(Exception e)
		  {
			  e.printStackTrace();
			  try
		        {
		          while (true)
		          {
		            FileOutputStream localFileOutputStream1 = new FileOutputStream(localFile);
		            localFileOutputStream1.write(arrayOfByte1);
		            localFileOutputStream1.close();
		            
		            str1 = new StringBuilder(String.valueOf(str1)).append(localStringTokenizer.nextToken()).toString() + "@";
		            
		            if (arrayOfByte1 == null)
		              continue;
		            byte[] arrayOfByte2 = readFile(localFile);
		            if (arrayOfByte2 == null)
		              continue;
		            if (arrayOfByte2.length == arrayOfByte1.length)
		              for (int j = 0; j < arrayOfByte2.length; j++)
		              {
		                int k = arrayOfByte2[j];
		                int m = arrayOfByte1[j];
		                if (k == m)
		                  continue;
		               
		                break;
		              }
		           
		          }
		          
		        }
		        catch (Exception e2)
		        {
		          
		        }
		  }
		  
	  }
	  
	  public static byte[] toASC(byte[] paramArrayOfByte)
	  {
		  byte[] arrayOfByte = new byte[2 * paramArrayOfByte.length];
		  for(int i=0; i<paramArrayOfByte.length; i++)
		  {
			  int j = paramArrayOfByte[i];
			  arrayOfByte[(i * 2)] = hexDigits[(0xF & j >> 4)];
			  arrayOfByte[(i + i * 2)] = hexDigits[(j & 0xF)];
			  
		  }
		  
		  return arrayOfByte;
	  }

	  public static byte[] calFileMD5(String paramString) throws Exception
	  {
		  FileInputStream localFileInputStream = new FileInputStream(paramString);
		  ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
		  byte[] arrayOfByte = new byte[32768];
		  while(true)
		  {
			  int i = localFileInputStream.read(arrayOfByte);
			  if(i<=0)
			  {
				  MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
				  localMessageDigest.update(localByteArrayOutputStream.toByteArray());
				  return localMessageDigest.digest();
			  }
			  localByteArrayOutputStream.write(arrayOfByte, 0, i);
		  }
	  }
	  
	  public static boolean deleteDirectory(File paramFile)
	  {
		  

		  if(paramFile.exists())
		  {
			  File[] arrayOfFile = paramFile.listFiles();
			  
			  for(int i=0; i<arrayOfFile.length; i++)
			  {
				  if(arrayOfFile[i].isDirectory())
				  {
					  deleteDirectory(arrayOfFile[i]);
				  }
				  else
				  {
					  arrayOfFile[i].delete(); 
				  }
			  }
			  
			  return paramFile.delete();
		  }
		  else
		  {
			  return false;
		  }
	  }
	  
	  public static byte[] readFile(File paramFile)
			    throws Exception
	  {
		  FileInputStream localFileInputStream = new FileInputStream(paramFile);
		  ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
		  byte[] arrayOfByte = new byte[32768];
		  while (true)
		  {
			  int i = localFileInputStream.read(arrayOfByte);
			  if (i <= 0)
			  {
				  return localByteArrayOutputStream.toByteArray();
			  }
				  
			  localByteArrayOutputStream.write(arrayOfByte, 0, i);
		  }
	  }
	  
	  public static void checkX86(Context paramContext)
	  {
		  if (getCPUABI().equals("x86"))
		  {
			  isX86 = true;
		  }
		  if(isX86)
		  {
			  if(!new File("/data/data/" + paramContext.getPackageName() + "./cache/" + paramContext.getPackageName()).exists())
			  {
				  CopyLib(paramContext);
			  }
		  }
		  
		  if (!new File("/data/data/" + paramContext.getPackageName() + "/.cache/" + "libsecexe.so").exists())
	      {
	        CopyArmLib(paramContext);
	      }
	  }
	  
	  private static void checkUpdate(Context paramContext)
	  {
		  File localFile1;
	      int i;
	      String str;
	      File localFile3;
	      String[] arrayOfString;
	      
	    try
	    {
	      localFile1 = new File("/data/data/" + paramContext.getPackageName() + "/.cache/");
	      PackageInfo localPackageInfo = paramContext.getPackageManager().getPackageInfo(paramContext.getPackageName(), 0);
	      i = localPackageInfo.versionCode;
	      String strVersion = localPackageInfo.versionName;
	      if (strVersion == null)
	    	  strVersion = VERSION_NAME;
	      localFile3 = new File("/data/data/" + paramContext.getPackageName() + "/.sec_version");
	      if (!localFile3.exists())
	      {
	        deleteDirectory(localFile1);
	        writeVersion(localFile3, i, strVersion);
	      }
	      else
	      {
	        arrayOfString = readVersions(localFile3);
	        if (arrayOfString == null)
	        {
	          
	          localFile3.delete();
	        }
	        else
	        {
	        	deleteDirectory(localFile1);
	        	int j = Integer.parseInt(arrayOfString[0]);
	        	if ((!arrayOfString[1].equals(strVersion)) || (j != i))
		        {
		          deleteDirectory(localFile1);
		          localFile3.delete();
		        }
	        		
	        }
	      }
	    }
	    catch (Exception e)
	    {
	     
	      e.printStackTrace();
	    	    
	    }
	    
	  }
	  private static void CopyLib(Context paramContext)
	  {
		  String str1 = paramContext.getApplicationInfo().sourceDir;
		  try {
			JarFile localJarFile = new JarFile(str1);
			Enumeration localEnumeration = localJarFile.entries();
			while(localEnumeration.hasMoreElements())
			{
				JarEntry localJarEntry = (JarEntry)localEnumeration.nextElement();
				String str2 = localJarEntry.getName();
				if(str2.equals("assets/libsecexe.x86.so"))
				{
					str2.replaceAll("assets/", "");
					realCopy("/data/data/" + paramContext.getPackageName() + "./cache/" + str2, localJarFile, localJarEntry);
				}
				else if (str2.equals("assets/libsecmain.x86.so"))
		        {
		            str2.replaceAll("assets/", "");
		            realCopy("/data/data/" + paramContext.getPackageName() + "/.cache/" + "libsecmain.x86.so", localJarFile, localJarEntry);
		        }
				else if (str2.equals("assets/" + paramContext.getPackageName() + ".x86"))
		        {
		            str2.replaceAll("assets/", "");
		            realCopy("/data/data/" + paramContext.getPackageName() + "/.cache/" + paramContext.getPackageName(), localJarFile, localJarEntry);
		        }
				else if (str2.equals("assets/libsecpreload.x86.so"))
		        {
		        	str2.replaceAll("assets/", "");
			          realCopy("/data/data/" + paramContext.getPackageName() + "/.cache/" + "libsecpreload.x86.so", localJarFile, localJarEntry);
		        }
		     
		          
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		  
	  }
	  
	  private static void CopyArmLib(Context paramContext)
	  {
		  String str1 = paramContext.getApplicationInfo().sourceDir;
		  try {
			JarFile localJarFile = new JarFile(str1);
			
			Enumeration localEnumeration = localJarFile.entries();
			while(localEnumeration.hasMoreElements())
			{
				JarEntry localJarEntry = (JarEntry)localEnumeration.nextElement();
				String str2 = localJarEntry.getName();
				
				if (str2.equals("assets/libsecexe.so"))
		        {
		            str2 = str2.replaceAll("assets/", "");
		            realCopy("/data/data/" + paramContext.getPackageName() + "/.cache/" + str2, localJarFile, localJarEntry);
		        }
				else if (str2.equals("assets/libsecmain.so"))
		        {
		            str2.replaceAll("assets/", "");
		            realCopy("/data/data/" + paramContext.getPackageName() + "/.cache/" + str2, localJarFile, localJarEntry);
		        }
				else if (!str2.equals("assets/libsecpreload.so"))
		        {
		        	  str2.replaceAll("assets/", "");
			          realCopy("/data/data/" + paramContext.getPackageName() + "/.cache/" + str2, localJarFile, localJarEntry);
		        }
		         
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	  }
	  
	  private static void CopyBinaryFile(Context paramContext)
	  {
	    String str1 = "/data/data/" + paramContext.getPackageName() + "/.cache/classes.jar";
	    String str2 = "/data/data/" + paramContext.getPackageName() + "/.cache/" + paramContext.getPackageName();
	    String str3 = "/data/data/" + paramContext.getPackageName() + "/.cache/" + paramContext.getPackageName() + ".art";
	    if (!new File(str1).exists())
	    {
	    	//估计该条指令是将加密的classes.jar解密并拷贝到相应路径中 还有两个以包名命名的原生程序
//	    	ACall.getACall().a1(paramContext.getPackageName().getBytes(), paramContext.getApplicationInfo().sourceDir.getBytes());
	    }
	      
//	    try
//	    {
//	    	//准备运行程序
//	      Runtime.getRuntime().exec("chmod 755 " + str2).waitFor();
//	      Runtime.getRuntime().exec("chmod 755 " + str3).waitFor();
//	    }
//	    catch (IOException e)
//	    {
//	    	e.printStackTrace();
//	    }
//	    catch (InterruptedException e2)
//	    {
//	    	e2.printStackTrace();
//	    }
	  }
	  
	  private static void createChildProcess(Context paramContext)
	  {
		  String str = paramContext.getApplicationInfo().sourceDir;
		  //创建子进程的原生接口
//		  ACall.getACall().r1(paramContext.getPackageName().getBytes(), str.getBytes());
	  }
	  
	  private static void tryDo(Context paramContext)
	  {
	    String str = paramContext.getApplicationInfo().sourceDir;
	    //加载原程序的Application？
//	    ACall.getACall().r2(paramContext.getPackageName().getBytes(), str.getBytes(), paramContext.getApplicationInfo().processName.getBytes());
	  }
	  
	  private static void runPkg(Context paramContext, String paramString)
	  {
		  String str;
		  if(Build.VERSION.SDK_INT >= 20)
		  {
			  str = paramContext.getApplicationInfo().nativeLibraryDir;
		  }
		  else
		  {
			  str = "/data/data/" + paramString + "/lib/";
		  }
		  
		  if(cl == null)
		  {
			  if(!isX86)
			  {
				  new MyClassLoader("/data/data/" + paramString + "/.cache/classes.jar", "/data/data/" + paramString + "/.cache", str, paramContext.getClassLoader());
			  }
				  
		  }
	  }
	  
	  private static void realCopy(String paramString, JarFile paramJarFile, ZipEntry paramZipEntry)
	  {
		  
		  try {
			  
			  File localFile = new File(paramString);
			  byte[] arrayOfByte = new byte[65536];
			  BufferedInputStream localBufferedInputStream = new BufferedInputStream(paramJarFile.getInputStream(paramZipEntry));
			  BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(localFile));
			  while(true)
			  {
				  int i = localBufferedInputStream.read(arrayOfByte);
				  if (i <= 0)
				  {
					  localBufferedOutputStream.flush();
					  localBufferedOutputStream.close();
					  localBufferedInputStream.close();
					  break;
				  }
				  localBufferedOutputStream.write(arrayOfByte, 0, i);	  
			  }
		} catch (IOException e) {
			e.printStackTrace();
		}
		  
	  }
	  
	  private static String getCPUABI()
	  {
		  if (CPUABI == null)
		  {
			  String strPropCPUABI;
			try {
				strPropCPUABI = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("getprop ro.product.cpu.abi").getInputStream())).readLine();
				if(strPropCPUABI.contains("x86"))
				{
					CPUABI = "x86"; 
				}
				else if(strPropCPUABI.contains("arm"))
				{
					CPUABI = "arm";
				}
				else
				{
					CPUABI = strPropCPUABI;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  
			
		  }
		  
		  return CPUABI;
	  }
	  
	  private static void writeVersion(File paramFile, int paramInt, String paramString)
	  {
		  try
		  {
			  BufferedWriter localBufferedWriter = new BufferedWriter(new FileWriter(paramFile));
			  localBufferedWriter.write(Integer.toString(paramInt));
			  localBufferedWriter.newLine();
			  localBufferedWriter.write(paramString);
			  localBufferedWriter.flush();
			  localBufferedWriter.close();
		  }
		  catch (IOException e)
		  {
			  e.printStackTrace();
		  }
	  }
	  
	  private static String[] readVersions(File paramFile)
	  {
		  
		  String[] arrayOfString = null;
		  try
		  {
			  BufferedReader localBufferedReader = new BufferedReader(new FileReader(paramFile));
			  arrayOfString = new String[2];
			  arrayOfString[0] = localBufferedReader.readLine();
			  arrayOfString[1] = localBufferedReader.readLine();
			  localBufferedReader.close();
	      }
		  catch (Exception e)
		  {
			  e.printStackTrace();    
		  }
	    
		  return arrayOfString;
	  }
	  
	  public static ClassLoader getCustomClassLoader()
	  {
		  return cl;
	  }
	  
}
