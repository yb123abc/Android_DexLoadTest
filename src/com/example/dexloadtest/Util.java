package com.example.dexloadtest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.StringTokenizer;

import android.content.ContentProvider;
import android.content.Context;
import android.content.pm.ApplicationInfo;

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
}
