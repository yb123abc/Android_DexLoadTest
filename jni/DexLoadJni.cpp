#include "DexLoadJni.h"
#include <stdio.h>
#include <string>
#include "android/log.h"
using namespace std;

JNIEXPORT void JNICALL Java_com_android_dexshell_DexLoadJni_changeClassLoader (JNIEnv *env, jclass arg, jobject context)
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

	if(context == NULL)
	{
		__android_log_write(ANDROID_LOG_DEBUG, "Tag", "context is null!!!!!!");
	}
	jstring jstrPackageName = (jstring)env->CallObjectMethod(context, methodGetPackageName);

	__android_log_write(ANDROID_LOG_DEBUG, "Tag", "Hehehehehe!!!");

	//获得包列表

	jfieldID jfieldIDPackages = env->GetFieldID(jclassActivityThread, "mPackages", "Landroid/util/ArrayMap;");
	jobject mPackages = env->GetObjectField(currentActivityThread, jfieldIDPackages);

	__android_log_write(ANDROID_LOG_DEBUG, "Tag", "Hehehehehe1!!!");
	//将数据保存到弱引用中
	jclass jclassArrayMap = env->FindClass("android/util/ArrayMap");
	jmethodID jmethodIDGet = env->GetMethodID(jclassArrayMap, "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
	jobject jobjectWR =  env->CallObjectMethod(mPackages, jmethodIDGet, jstrPackageName);

	__android_log_write(ANDROID_LOG_DEBUG, "Tag", "Hehehehehe15!!!");
	jclass jclassDexClassLoader = env->FindClass("dalvik/system/DexClassLoader");
	__android_log_write(ANDROID_LOG_DEBUG, "Tag", "Hehehehehe16!!!");
	jmethodID jmethodID_Init_DexClassLoader =
			env->GetMethodID(jclassDexClassLoader, "<init>",
					"(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/ClassLoader;)Ldalvik/system/DexClassLoader;");

	__android_log_write(ANDROID_LOG_DEBUG, "Tag", "Hehehehehe17!!!");
//	jclass jclassLoadedApk = env->FindClass("android/app/LoadedApk");
	jclass jclassWeakReference = env->FindClass("java/lang/ref/WeakReference");
	__android_log_write(ANDROID_LOG_DEBUG, "Tag", "Hehehehehe18!!!");

	__android_log_write(ANDROID_LOG_DEBUG, "Tag", "Hehehehehe19!!!");
	jmethodID jmethodID_wr_get = env->GetMethodID(jclassWeakReference, "get", "()Ljava/lang/Object;");

	__android_log_write(ANDROID_LOG_DEBUG, "Tag", "Hehehehehe2!!!");
	//新建DexClassLoader
	jobject jobjectLoadedApk = env->CallObjectMethod(jobjectWR, jmethodID_wr_get);

	jboolean isCopy;
	const char* strPackageName = env->GetStringUTFChars(jstrPackageName, &isCopy);
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
												jobjectLoadedApk);

	__android_log_write(ANDROID_LOG_DEBUG, "Tag", "Hehehehehe!!!3");
	//设置android.app.LoadedApk的没ClassLoader为新的DexClassLoader
//	jfieldID jfieldID_LoadedApk_mClassLoader = env->GetFieldID(jclassLoadedApk, "mClassLoader", "Ljava/lang/Object;");
//	env->SetObjectField(jobjectLoadedApk, jfieldID_LoadedApk_mClassLoader, jobjectDexClassLoader);

}

JNIEXPORT void JNICALL Java_com_android_dexshell_DexLoadJni_changeApplication(JNIEnv *env, jclass arg, jobject context, jobject application)
{

}

static JNINativeMethod methods[] = {
		{"changeClassLoader","(Landroid/content/Context;)V",(void*)Java_com_android_dexshell_DexLoadJni_changeClassLoader },
		{"changeApplication","(Landroid/content/Context;Landroid/app/Application;)V",(void*)Java_com_android_dexshell_DexLoadJni_changeApplication },
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
