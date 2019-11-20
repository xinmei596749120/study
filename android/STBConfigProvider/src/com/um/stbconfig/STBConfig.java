package com.um.stbconfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * STBConfig操作类
 * 
 * @author Eniso
 *
 */
public class STBConfig {

	private static final String LOG_TAG = "STBConfig";
	public static final Uri SUMMARY_URI = Uri.parse("content://stbconfig/summary");
	public static final Uri AUTHENTICATION_URI = Uri.parse("content://stbconfig/authentication");
	public static final Uri STARTMETHOD_URI = Uri.parse("content://stbconfig/startmethod");

	public static String getString(ContentResolver resolver, Uri uri, String key) {
		try {
			if (!TextUtils.isEmpty(key)) {
				if (uri.toString().contains(AUTHENTICATION_URI.toString())) {
					if (!uri.toString().endsWith("/" + key)) {
						uri = Uri.parse(uri + "/" + key);
					}
					key = "value";
				}
			}
			ArrayList<HashMap<String, String>> res = query(resolver, uri, key, 0);
			if (res.size() > 0)
				return res.get(0).get(key);
			else
				return null;
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean putString(ContentResolver resolver, Uri uri, String name, String value) {
		try {
			ContentValues values = new ContentValues();
			if (uri.toString().contains(AUTHENTICATION_URI.toString())) {
				values.put("name", name);
				values.put("value", value);
			} else {
				values.put(name, value);
			}
			resolver.update(uri, values, null, null);
			Log.w(LOG_TAG, "Set key " + name + " in " + uri);
			return true;
		} catch (Exception e) {
			Log.w(LOG_TAG, "Can't set key " + name + " in " + uri, e);
			return false;
		}
	}

	@SuppressLint("NewApi")
	public static boolean putString(ContentResolver resolver, Uri uri, ContentValues values) {
		try {
			resolver.update(uri, values, null, null);
			Log.w(LOG_TAG, "Set keys " + values.keySet() + " in " + uri);
			return true;
		} catch (Exception e) {
			Log.w(LOG_TAG, "Can't set keys " + values.keySet() + " in " + uri, e);
			return false;
		}
	}

	public static boolean putString(ContentResolver resolver, Uri uri, List<ContentValues> valuesList) {
		if (valuesList == null || valuesList.size() == 0) {
			Log.w(LOG_TAG, "nothing change");
			return true;
		}
		Iterator<ContentValues> it = valuesList.iterator();
		while (it.hasNext()) {
			ContentValues values = it.next();
			try {
				resolver.update(uri, values, null, null);
				Log.w(LOG_TAG, "Set key " + values.getAsString("name") + " in " + uri);
			} catch (Exception e) {
				Log.w(LOG_TAG, "Can't set key " + values.getAsString("name") + " in " + uri, e);
				return false;
			}
		}
		return true;
	}

	private static ArrayList<HashMap<String, String>> query(ContentResolver resolver, Uri uri, String key, int count) {
		ArrayList<HashMap<String, String>> res = new ArrayList<HashMap<String, String>>();
		Cursor cursor = resolver.query(uri, null, null, null, null);
		if (cursor == null) {
			Log.i(LOG_TAG, "query stbconfig null");
			return res;
		}
		String[] columns = cursor.getColumnNames();
		while (cursor.moveToNext()) {
			if (key == null) {
				// 不过滤
				HashMap<String, String> tmp = new HashMap<String, String>();
				for (String column : columns) {
					tmp.put(column, cursor.getString(cursor.getColumnIndex(column)));
				}
				res.add(tmp);
			} else {
				// 过滤
				for (String column : columns) {
					if (column.equalsIgnoreCase(key)) {
						HashMap<String, String> tmp = new HashMap<String, String>();
						tmp.put(column, cursor.getString(cursor.getColumnIndex(column)));
						res.add(tmp);
						break;
					}
				}
			}
			if (count > 0 && res.size() >= count) {
				break;
			}
		}
		cursor.close();
		return res;
	}

}
