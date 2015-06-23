#include "DexLoadJni.h"
#include <stdio.h>
#include <string>
#include "android/log.h"
using namespace std;

JNIEXPORT void JNICALL Java_com_android_dexshell_DexLoadJni_changeClassLoader (JNIEnv *env, jclass arg, jobject context, jint os_version)
{
	__android_log_write(ANDROID_LOG_DEBUG, "Tag", "Hello JNI!!!!!!");
	//获得ActivityThread对象
	jclass jclassActivityThread = env->FindClass("android/app/ActivityThread");
	jmethodID methodCurrentActivityThread =
			env->GetStaticMethodID(jclassActivityThread, "currentActivityThread", "()Landroid/app/ActivityThread;");
	jobject currentActivityThread = env->CallStaticObjectMethod(jclassActivityThread, methodCurrentActivityThread);
	//获得包名
	jclass jclassContext = env->FindClass("android/content/Context");

	jmethodID methodGetPackageName = env->GetMethodID(jclassContext, "getPackageName", "()Ljava/lang/String;");

	jstring jstrPackageName = (jstring)env->CallObjectMethod(context, methodGetPackageName);


	//获得包列表

	jfieldID jfieldIDPackages = env->GetFieldID(jclassActivityThread, "mPackages", "Landroid/util/ArrayMap;");
	jobject mPackages = env->GetObjectField(currentActivityThread, jfieldIDPackages);

	//将数据保存到弱引用中
	jclass jclassMap;

	if(os_version >= 21)
	{
		jclassMap = env->FindClass("android/util/ArrayMap");
	}
	else
	{
		jclassMap = env->FindClass("java/util/HashMap");
	}

	jmethodID jmethodIDGet = env->GetMethodID(jclassMap, "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
	jobject jobjectWR =  env->CallObjectMethod(mPackages, jmethodIDGet, jstrPackageName);

	jclass jclassDexClassLoader = env->FindClass("dalvik/system/DexClassLoader");

	jmethodID jmethodID_Init_DexClassLoader =
			env->GetMethodID(jclassDexClassLoader, "<init>",
					"(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/ClassLoader;)V");



	jclass jclassWeakReference = env->FindClass("java/lang/ref/WeakReference");


	jmethodID jmethodID_wr_get = env->GetMethodID(jclassWeakReference, "get", "()Ljava/lang/Object;");


	//新建DexClassLoader
	jclass jclassLoadedApk = env->FindClass("android/app/LoadedApk");
	jobject jobjectLoadedApk = env->CallObjectMethod(jobjectWR, jmethodID_wr_get);

	jfieldID jfieldID_LoadedAPK_mClassLoader = env->GetFieldID(jclassLoadedApk, "mClassLoader", "Ljava/lang/ClassLoader;");
	jobject jobjectClassLoader = env->GetObjectField(jobjectLoadedApk, jfieldID_LoadedAPK_mClassLoader);

	jboolean isCopy;
	char* strPackageName = (char*)env->GetStringUTFChars(jstrPackageName, &isCopy);
	string strDexPath = string("/data/data/") + strPackageName + "/cache";
	string strLibPath = string("/data/data/") + strPackageName + "/lib";
	string strDexFileName = strDexPath + "/classes.jar";
	jstring jstrDexPath = env->NewStringUTF(strDexPath.c_str());
	jstring jstrLibPath = env->NewStringUTF(strLibPath.c_str());
	jstring jstrDexFileName =env->NewStringUTF(strDexFileName.c_str());


	jobject jobjectDexClassLoader = env->NewObject(jclassDexClassLoader,
												jmethodID_Init_DexClassLoader,
												jstrDexFileName,
												jstrDexPath,
												jstrLibPath,
												jobjectClassLoader);


	//设置android.app.LoadedApk的没ClassLoader为新的DexClassLoader
//	jfieldID jfieldID_LoadedApk_mClassLoader = env->GetFieldID(jclassLoadedApk, "mClassLoader", "Ljava/lang/Object;");
	env->SetObjectField(jobjectLoadedApk, jfieldID_LoadedAPK_mClassLoader, jobjectDexClassLoader);

	//释放引用的资源
	env->ReleaseStringUTFChars(jstrPackageName, strPackageName);
	env->DeleteLocalRef(jstrDexPath);
	env->DeleteLocalRef(jstrDexFileName);
	env->DeleteLocalRef(jstrLibPath);

	env->DeleteLocalRef(jclassActivityThread);
	env->DeleteLocalRef(jclassMap);
	env->DeleteLocalRef(jclassContext);
	env->DeleteLocalRef(jclassDexClassLoader);
	env->DeleteLocalRef(jclassLoadedApk);
	env->DeleteLocalRef(jclassWeakReference);

	env->DeleteLocalRef(currentActivityThread);
	env->DeleteLocalRef(jobjectClassLoader);
	env->DeleteLocalRef(jobjectDexClassLoader);
	env->DeleteLocalRef(jobjectLoadedApk);
	env->DeleteLocalRef(jobjectWR);
}

JNIEXPORT void JNICALL Java_com_android_dexshell_DexLoadJni_changeApplication(JNIEnv *env, jclass arg, jobject context, jstring jstrApplication, jint os_version)
{

	__android_log_write(ANDROID_LOG_DEBUG, "Tag", "1");
	//获得ActivityThread对象
	jclass jclassActivityThread = env->FindClass("android/app/ActivityThread");
	jmethodID methodCurrentActivityThread =
	env->GetStaticMethodID(jclassActivityThread, "currentActivityThread", "()Landroid/app/ActivityThread;");
	jobject currentActivityThread = env->CallStaticObjectMethod(jclassActivityThread, methodCurrentActivityThread);

	//mBoundApplication
	jfieldID jfieldID_mBoundApplication =
			env->GetFieldID(jclassActivityThread, "mBoundApplication", "Landroid/app/ActivityThread$AppBindData;");
	jobject jobjectBoundApplication =
			env->GetObjectField(currentActivityThread, jfieldID_mBoundApplication);

	//loadedApkInfo对象
	jclass jclassAppBindData = env->FindClass("android/app/ActivityThread$AppBindData");
	jfieldID jfieldID_info = env->GetFieldID(jclassAppBindData, "info", "Landroid/app/LoadedApk;");
	jobject jobjectLoadedApkInfo = env->GetObjectField(jobjectBoundApplication, jfieldID_info);
	//loadedApkInfo.mApplicaiton = null
	jclass jclassLoadedApk = env->FindClass("android/app/LoadedApk");
	jfieldID jfieldID_LoadedApk_mApplication =
			env->GetFieldID(jclassLoadedApk, "mApplication", "Landroid/app/Application;");
	env->SetObjectField(jobjectLoadedApkInfo, jfieldID_LoadedApk_mApplication, NULL);

	//oldApplication
	jfieldID jfieldID_ActivityThread_mInitialApplication =
			env->GetFieldID(jclassActivityThread, "mInitialApplication", "Landroid/app/Application;");
	jobject jobjectOldApplication = env->GetObjectField(currentActivityThread, jfieldID_ActivityThread_mInitialApplication);

	//appinfo_In_LoadedApk
	jfieldID jfieldID_LoadedApk_mApplicationInfo =
			env->GetFieldID(jclassLoadedApk, "mApplicationInfo", "Landroid/content/pm/ApplicationInfo;");
	jobject jobject_appinfo_In_LoadedApk =
			env->GetObjectField(jobjectLoadedApkInfo, jfieldID_LoadedApk_mApplicationInfo);

	//appinfo_In_AppBindData
	jfieldID jfieldID_AppBindData_appInfo =
			env->GetFieldID(jclassAppBindData, "appInfo", "Landroid/content/pm/ApplicationInfo;");
	jobject jobject_appinfo_In_AppBindData =
			env->GetObjectField(jobjectBoundApplication, jfieldID_AppBindData_appInfo);

	// set className
	jclass jclassApplicationInfo = env->FindClass("android/content/pm/ApplicationInfo");
	jfieldID jfieldID_appinfo_className =
			env->GetFieldID(jclassApplicationInfo, "className", "Ljava/lang/String;");
	env->SetObjectField(jobject_appinfo_In_LoadedApk, jfieldID_appinfo_className,jstrApplication);
	env->SetObjectField(jobject_appinfo_In_AppBindData, jfieldID_appinfo_className, jstrApplication);

	__android_log_write(ANDROID_LOG_DEBUG, "Tag", "2");
	//create app
	jmethodID jmethodID_LoadedApp_makeApplication =
			env->GetMethodID(jclassLoadedApk, "makeApplication", "(ZLandroid/app/Instrumentation;)Landroid/app/Application;");
	jobject jobjectApplicaiton =
			env->CallObjectMethod(jobjectLoadedApkInfo, jmethodID_LoadedApp_makeApplication, JNI_FALSE, NULL);
	env->SetObjectField(currentActivityThread, jfieldID_ActivityThread_mInitialApplication, jobjectApplicaiton);

	//get currentActivityThread.mProviderMap
	jfieldID jfieldID_ActivityThread_mProviderMap;
	jclass jclassProviderMap;
	jobject jobjectProviderMap;
	if(os_version >= 21)
	{
		jfieldID_ActivityThread_mProviderMap =
				env->GetFieldID(jclassActivityThread, "mProviderMap", "Landroid/util/ArrayMap;");
		jclassProviderMap = env->FindClass("android/util/ArrayMap");
	}
	else
	{
		jfieldID_ActivityThread_mProviderMap =
				env->GetFieldID(jclassActivityThread, "mProviderMap", "Ljava/util/HashMap;");
		jclassProviderMap = env->FindClass("java/util/HashMap");
	}

	jobjectProviderMap =
			env->GetObjectField(currentActivityThread, jfieldID_ActivityThread_mProviderMap);

	jclass jclassIterator = env->FindClass("java/util/Iterator");
	jclass jclassCollection = env->FindClass("java/util/Collection");
	jmethodID jmethodID_ProviderMap_values =
			env->GetMethodID(jclassProviderMap, "values", "()Ljava/util/Collection;");
	jmethodID jmethodID_Collection_iterator =
			env->GetMethodID(jclassCollection, "iterator", "()Ljava/util/Iterator;");
	jobject jobjectValues =
			env->CallObjectMethod(jobjectProviderMap, jmethodID_ProviderMap_values);
	jobject jobjectIt =
			env->CallObjectMethod(jobjectValues, jmethodID_Collection_iterator);
	__android_log_write(ANDROID_LOG_DEBUG, "Tag", "3");
	jmethodID jmethodID_it_hasNext =
			env->GetMethodID(jclassIterator, "hasNext", "()Z");
	bool hasNext = env->CallBooleanMethod(jobjectIt, jmethodID_it_hasNext);
	jmethodID jmethodID_it_next =
			env->GetMethodID(jclassIterator, "next", "()Ljava/lang/Object;");

	jclass jclassProviderClientRecord =
			env->FindClass("android/app/ActivityThread$ProviderClientRecord");
	jfieldID jfieldIDLocalProvider =
					env->GetFieldID(jclassProviderClientRecord,
							"mLocalProvider",
							"Landroid/content/ContentProvider;");
	jobject jobjectLocalProvider = NULL;
	while(hasNext)
	{
		jobjectLocalProvider =
				env->CallObjectMethod(jobjectIt, jmethodID_it_next);

		env->SetObjectField(context, jfieldIDLocalProvider, jobjectApplicaiton);
		if(jobjectLocalProvider != NULL)
		{
			env->DeleteLocalRef(jobjectLocalProvider);
		}
		hasNext = env->CallBooleanMethod(jobjectIt, jmethodID_it_hasNext);
	}

	jclass jclassApplication = env->FindClass("android/app/Application");
	jmethodID jmethodID_app_onCreate =
			env->GetMethodID(jclassApplication, "onCreate", "()V");
	env->CallVoidMethod(jobjectApplicaiton, jmethodID_app_onCreate);

	//释放资源
	env->DeleteLocalRef(jclassActivityThread);
	env->DeleteLocalRef(jclassAppBindData);
	env->DeleteLocalRef(jclassApplicationInfo);
	env->DeleteLocalRef(jclassCollection);
	env->DeleteLocalRef(jclassIterator);
	env->DeleteLocalRef(jclassLoadedApk);
	env->DeleteLocalRef(jclassProviderMap);
	env->DeleteLocalRef(jclassProviderClientRecord);
	env->DeleteLocalRef(jclassApplication);

	env->DeleteLocalRef(currentActivityThread);
	env->DeleteLocalRef(jobjectApplicaiton);
	env->DeleteLocalRef(jobjectBoundApplication);
	env->DeleteLocalRef(jobjectLoadedApkInfo);
	env->DeleteLocalRef(jobjectOldApplication);
	env->DeleteLocalRef(jobjectProviderMap);
	env->DeleteLocalRef(jobjectValues);
	env->DeleteLocalRef(jobject_appinfo_In_AppBindData);
	env->DeleteLocalRef(jobject_appinfo_In_LoadedApk);
	env->DeleteLocalRef(jobjectIt);

}

static JNINativeMethod methods[] = {
		{"changeClassLoader","(Landroid/content/Context;I)V",(void*)Java_com_android_dexshell_DexLoadJni_changeClassLoader },
		{"changeApplication","(Landroid/content/Context;Ljava/lang/String;I)V",(void*)Java_com_android_dexshell_DexLoadJni_changeApplication },
};

static const char *classPathName = "com/android/dexshell/DexLoadJni";

static int registerNativeMethods(JNIEnv* env, const char* className,
    JNINativeMethod* gMethods, int numMethods)
{
    jclass clazz;

    clazz = env->FindClass(className);
    if (clazz == NULL)
        return JNI_FALSE;

    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0)
    {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}


static int registerNatives(JNIEnv* env)

{

  if (!registerNativeMethods(env, classPathName,

                 methods, sizeof(methods) / sizeof(methods[0]))) {

    return JNI_FALSE;

  }

  return JNI_TRUE;

}

jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    JNIEnv* env = NULL;
    jint result = -1;

    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK)
    	return result;

    if (!registerNatives(env))
    	return result;

    /* success -- return valid version number */
    result = JNI_VERSION_1_4;

    return result;
}
