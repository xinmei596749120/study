package com.um.stbconfig;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * STBConfig元数据定义
 * 
 * @author Eniso
 *
 */
public class STBConfigMetaData {

	public static final String AUTHORITY = "stbconfig";
	public static final String DATABASE_NAME = "stbconfig.db";
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.unionman.stbconfig";
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.unionman.stbconfig";
	public static final int DATABASE_VERSION = 1;

	/**
	 * summary表
	 * 
	 * @author Eniso
	 *
	 */
	public static final class SummaryTable implements BaseColumns {
		public static final String TABLE_NAME = "summary";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

		public static final String MapID = "mapid";
		public static final String AuthURL = "AuthURL";
		public static final String STBID = "STBID";
		public static final String IP = "IP";
		public static final String MAC = "MAC";
		public static final String UserID = "UserID";
		public static final String UserPassword = "UserPassword";
		public static final String UserToken = "UserToken";
		public static final String LastchannelNum = "LastchannelNum";
		public static final String STBType = "STBType";
		public static final String SoftwareVersion = "SoftwareVersion";
		public static final String Reserved = "Reserved";
		public static final String NetworkAcount = "NetworkAcount";
		public static final String NetworkPassword = "NetworkPassword";

		public static final String DEFAULT_ORDERBY = "";

		public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + _ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + MapID + " TEXT," + AuthURL + " TEXT," + STBID + " TEXT," + IP
				+ " TEXT," + MAC + " TEXT," + UserID + " TEXT," + UserPassword + " TEXT," + UserToken + " TEXT,"
				+ LastchannelNum + " TEXT," + STBType + " TEXT," + SoftwareVersion + " TEXT," + Reserved + " TEXT,"
				+ NetworkAcount + " TEXT," + NetworkPassword + " TEXT" + ");";
	}

	/**
	 * startmethod表
	 * 
	 * @author Eniso
	 *
	 */
	public static final class StartmethodTable implements BaseColumns {
		public static final String TABLE_NAME = "startmethod";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

		public static final String MapID = "mapid";
		public static final String platform = "platform";
		public static final String isAlways = "isAlways";

		public static final String DEFAULT_ORDERBY = "";

		public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + _ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + MapID + " TEXT," + isAlways + " TEXT," + platform + " TEXT);";
	}

	/**
	 * authentication表
	 * 
	 * @author Eniso
	 *
	 */
	public static final class AuthenticationTable implements BaseColumns {
		public static final String TABLE_NAME = "authentication";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

		public static final String MapID = "mapid";
		public static final String Names = "name";
		public static final String Values = "value";

		public static final String DEFAULT_ORDERBY = "mapid asc";

		public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + _ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + MapID + " TEXT," + Names + " TEXT," + Values + " TEXT" + ");";
	}

}
