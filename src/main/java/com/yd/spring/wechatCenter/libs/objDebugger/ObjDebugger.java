package com.yd.spring.wechatCenter.libs.objDebugger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ObjDebugger {
	public ObjDebugger(Object obj) {
		Class classSelf = obj.getClass();
		String className = classSelf.getName();

		System.out.println("\nObject Debugger:\n\tClassName: " + className);

		System.out.println("\tFields:");
		Field[] classFields = classSelf.getDeclaredFields();
		for (Field field : classFields) {
			field.setAccessible(true);
			Object value = null;
			try {
				value = field.get(obj);
			} catch (Exception e) {

			}

			String fieldName = field.getName();
			System.out.println("\t\t" + fieldName);
			System.out.println("\t\t\tType：" + field.getType().getCanonicalName());

			System.out.println("\t\t\tValue：" + value);
		}

		System.out.println("\tMethods:");
		Method[] methodFields = classSelf.getMethods();
		for (Method method : methodFields) {
			System.out.println("\t\t" + method.getName());
			System.out.println("\t\t\tType：" + method.getReturnType());
		}
	}
}
