package com.um.stbconfig;

import java.lang.reflect.Method;

/**
 * 系统属性相关
 * 
 * @author Eniso
 *
 */
public class SystemProperties {

	public static final int PROP_NAME_MAX = 31;
	public static final int PROP_VALUE_MAX = 91;

	private static final Method $get;
	private static final Method $getString;
	private static final Method $getInt;
	private static final Method $getLong;
	private static final Method $getBoolean;
	private static final Method $set;
	private static final Method $addChangeCallback;
	private static final Method $callChangeCallbacks;

	static {
		Class<?> cls;
		Method _get = null;
		Method _getString = null;
		Method _getInt = null;
		Method _getLong = null;
		Method _getBoolean = null;
		Method _set = null;
		Method _addChangeCallback = null;
		Method _callChangeCallbacks = null;
		try {
			cls = Class.forName("android.os.SystemProperties");
			_get = cls.getDeclaredMethod("get", String.class);
			_getString = cls.getDeclaredMethod("get", String.class, String.class);
			_getInt = cls.getDeclaredMethod("getInt", String.class, int.class);
			_getLong = cls.getDeclaredMethod("getLong", String.class, long.class);
			_getBoolean = cls.getDeclaredMethod("getBoolean", String.class, boolean.class);
			_set = cls.getDeclaredMethod("set", String.class, String.class);
			_addChangeCallback = cls.getDeclaredMethod("addChangeCallback", Runnable.class);
			_callChangeCallbacks = cls.getDeclaredMethod("callChangeCallbacks");
		} catch (Exception e) {
			e.printStackTrace();
		}
		$get = _get;
		$getString = _getString;
		$getInt = _getInt;
		$getLong = _getLong;
		$getBoolean = _getBoolean;
		$set = _set;
		$addChangeCallback = _addChangeCallback;
		$callChangeCallbacks = _callChangeCallbacks;
	}

	public static String get(String key) {
		try {
			return (String) $get.invoke(null, key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String get(String key, String def) {
		try {
			return (String) $getString.invoke(null, key, def);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return def;
	}

	public static int getInt(String key, int def) {
		try {
			return ((Integer) $getInt.invoke(null, key, def)).intValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return def;
	}

	public static long getLong(String key, long def) {
		try {
			return ((Long) $getLong.invoke(null, key, def)).longValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return def;
	}

	public static boolean getBoolean(String key, boolean def) {
		try {
			return ((Boolean) $getBoolean.invoke(null, key, def)).booleanValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return def;
	}

	public static void set(String key, String val) {
		try {
			$set.invoke(null, key, val);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void addChangeCallback(Runnable callback) {
		try {
			$addChangeCallback.invoke(null, callback);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void callChangeCallbacks() {
		try {
			$callChangeCallbacks.invoke(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
