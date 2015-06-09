package com.example.dexloadtest;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RefInvoke {
	
	public static Object invokeStaticMethod(String class_name, String method_name, Class[] pareTyple, Object[] pareValues)
	{
		Object result = null;
		
		try {
			Class obj_class;
			obj_class = Class.forName(class_name);
			Method method = obj_class.getMethod(method_name, pareTyple);
			result =  method.invoke(null, pareValues);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public static Object invokeMethod(String class_name, String method_name, Object obj, Class[] pareTyple, Object[] pareValues)
	{
		Object result = null;
		Class obj_class;
		try {
			obj_class = Class.forName(class_name);
			Method method = obj_class.getMethod(method_name, pareTyple);
			result = method.invoke(obj, pareValues);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return result;
	}
	
	public static Object getFieldObject(String class_name, Object obj, String fieldName)
	{
		Object result = null;
		
		Class obj_class;
		try {
			obj_class = Class.forName(class_name);
			Field field = obj_class.getDeclaredField(fieldName);
			field.setAccessible(true);
			result = field.get(obj);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	public static Object getStaticFieldObject(String class_name, String filedName)
	{
		Object result = null;
		
		Class obj_class;
		try {
			obj_class = Class.forName(class_name);
			Field field = obj_class.getDeclaredField(filedName);
			field.setAccessible(true);
			result = field.get(null);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
		
	}
	
	public static void setFieldObject(String class_name, String fieldName, Object obj, Object fieldValue)
	{
		Class obj_class;
		try {
			obj_class = Class.forName(class_name);
			Field field = obj_class.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(obj, fieldValue);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void setStaticObject(String class_name, String fieldName, Object fieldValue)
	{
		try {
			Class obj_class = Class.forName(class_name);
			Field field = obj_class.getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(null, fieldValue);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
