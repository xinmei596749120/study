package com.um.stbconfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * 
 * STBConfig内容提供者， 实现了STBConfig增删改查</br>
 * 设计有如下特点：</br>
 * 1、Summary，最多只有一条记录，有些老项目（四川移动、广东移动等）会用到</br>
 * a)使用insert，如果记录存在，则自动改为update</br>
 * b)使用update，如果记录不存在，则自动改为insert</br>
 * 2、StartMethod，已废弃</br>
 * 3、Authentication，新项目采用这个，而不是Summary</br>
 * Authentication采用name和value方式存储数据，设计上与Settings相似</br>
 * 兼容华为实现的认证客户端</br>
 * 注释：</br>
 * 为了数据管理上方便，增加了mapid字段</br>
 * mapid能被{@code _BASE}整除，表示根uri，否则表示子uri</br>
 * Authentication含有很多子uri</br>
 * 
 * @author Eniso
 *
 */

public class STBConfigProvider extends ContentProvider {

	private static final String LOG_TAG = "STBConfigProvider";

	// 基数定义
	private static final int _BASE = 1000;

	// summary定义
	private static final int SUMMARY = 1 * _BASE;
	private static final int SUMMARY_ID = SUMMARY + 999;

	// startmethod定义
	private static final int STARTMETHOD = 2 * _BASE;
	private static final int STARTMETHOD_ID = STARTMETHOD + 999;

	// authentication定义
	private static final int AUTHENTICATION = 3 * _BASE;
	private static final int AUTHENTICATION_ID = AUTHENTICATION + 999;
	private static final int USERNAME_ID = AUTHENTICATION + 1;
	private static final int PASSWORD_ID = AUTHENTICATION + 2;
	private static final int USERTOKEN_ID = AUTHENTICATION + 3;
	private static final int EPGSERVER_ID = AUTHENTICATION + 4;
	private static final int EDSSERVER_ID = AUTHENTICATION + 5;
	private static final int EDSSERVER2_ID = AUTHENTICATION + 6;
	private static final int CLIENT_ID = AUTHENTICATION + 7;
	private static final int CLIENT_SECRET_ID = AUTHENTICATION + 8;
	private static final int DEVICETYPE_ID = AUTHENTICATION + 9;
	private static final int DEVICEVERSION_ID = AUTHENTICATION + 10;
	private static final int AUTHSTATUS_ID = AUTHENTICATION + 11;
	private static final int AUTHERRORCODE_ID = AUTHENTICATION + 12;
	private static final int AUTHERRORTYPE_ID = AUTHENTICATION + 13;
	private static final int AUTHERRORNAME_ID = AUTHENTICATION + 14;
	private static final int AUTHERRORDESC_ID = AUTHENTICATION + 15;
	private static final int AUTHERRORRESOLVE_ID = AUTHENTICATION + 16;
	private static final int AREA_ID = AUTHENTICATION + 17;
	private static final int GROUP_ID = AUTHENTICATION + 18;
	private static final int AUTHERROR_EXTENDDESCRIPTION_ID = AUTHENTICATION + 19;
	private static final int CHANNELLIST_ID = AUTHENTICATION + 20;
	private static final int NEEDCOPYUSERPWD_ID = AUTHENTICATION + 21;
	private static final int TVDESKTOPID = AUTHENTICATION + 22;
	private static final int TVDESKTOPGETURL = AUTHENTICATION + 23;
	private static final int CNDSLB = AUTHENTICATION + 24;
	private static final int JSESSIONID = AUTHENTICATION + 25;
	private static final int STBMAC = AUTHENTICATION + 26;
	private static final int LICENSEPLATE = AUTHENTICATION + 27;
	private static final int DOMAIN = AUTHENTICATION + 28;
	private static final int STATUS = AUTHENTICATION + 29;
        private static final int RESULT = AUTHENTICATION + 30;

	private static final UriMatcher sUriMatcher;
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		// 添加summary
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "summary", SUMMARY);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "summary/#", SUMMARY_ID);

		// 添加startmethod
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "startmethod", STARTMETHOD);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "startmethod/#", STARTMETHOD_ID);

		// 添加authentication
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication", AUTHENTICATION);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication/#", AUTHENTICATION_ID);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication/username", USERNAME_ID);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication/password", PASSWORD_ID);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication/user_token", USERTOKEN_ID);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication/epg_server", EPGSERVER_ID);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication/eds_server", EDSSERVER_ID);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication/eds_server2", EDSSERVER2_ID);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication/client_id", CLIENT_ID);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication/client_secret", CLIENT_SECRET_ID);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication/deviceType", DEVICETYPE_ID);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication/deviceVersion", DEVICEVERSION_ID);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication/authStatus", AUTHSTATUS_ID);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication/authErrorCode", AUTHERRORCODE_ID);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication/authErrorType", AUTHERRORTYPE_ID);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication/authErrorName", AUTHERRORNAME_ID);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication/authErrorDesc", AUTHERRORDESC_ID);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication/authErrorResolve", AUTHERRORRESOLVE_ID);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication/areaId", AREA_ID);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication/groupId", GROUP_ID);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication/authErrorExtendDescription",
				AUTHERROR_EXTENDDESCRIPTION_ID);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication/ChannelList", CHANNELLIST_ID);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication/needCopyUserPwd", NEEDCOPYUSERPWD_ID);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication/TVDesktopID", TVDESKTOPID);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication/TVDesktopGetURL", TVDESKTOPGETURL);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication/cdn_slb", CNDSLB);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication/jsessionid", JSESSIONID);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication/STBMAC", STBMAC);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication/LicensePlate", LICENSEPLATE);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication/domain", DOMAIN);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication/result", RESULT);
		sUriMatcher.addURI(STBConfigMetaData.AUTHORITY, "authentication/status", STATUS);
	}

	private static final Map<String, String> summaryProjMap;
	private static final Map<String, String> startmethodProjMap;
	private static final Map<String, String> authenticationProjMap;
	static {
		// 添加summary projection
		summaryProjMap = new HashMap<String, String>();
		summaryProjMap.put(STBConfigMetaData.SummaryTable._ID, STBConfigMetaData.SummaryTable._ID);
		summaryProjMap.put(STBConfigMetaData.SummaryTable.MapID, STBConfigMetaData.SummaryTable.MapID);
		summaryProjMap.put(STBConfigMetaData.SummaryTable.AuthURL, STBConfigMetaData.SummaryTable.AuthURL);
		summaryProjMap.put(STBConfigMetaData.SummaryTable.STBID, STBConfigMetaData.SummaryTable.STBID);
		summaryProjMap.put(STBConfigMetaData.SummaryTable.IP, STBConfigMetaData.SummaryTable.IP);
		summaryProjMap.put(STBConfigMetaData.SummaryTable.MAC, STBConfigMetaData.SummaryTable.MAC);
		summaryProjMap.put(STBConfigMetaData.SummaryTable.UserID, STBConfigMetaData.SummaryTable.UserID);
		summaryProjMap.put(STBConfigMetaData.SummaryTable.UserPassword, STBConfigMetaData.SummaryTable.UserPassword);
		summaryProjMap.put(STBConfigMetaData.SummaryTable.UserToken, STBConfigMetaData.SummaryTable.UserToken);
		summaryProjMap.put(STBConfigMetaData.SummaryTable.LastchannelNum,
				STBConfigMetaData.SummaryTable.LastchannelNum);
		summaryProjMap.put(STBConfigMetaData.SummaryTable.STBType, STBConfigMetaData.SummaryTable.STBType);
		summaryProjMap.put(STBConfigMetaData.SummaryTable.SoftwareVersion,
				STBConfigMetaData.SummaryTable.SoftwareVersion);
		summaryProjMap.put(STBConfigMetaData.SummaryTable.Reserved, STBConfigMetaData.SummaryTable.Reserved);
		summaryProjMap.put(STBConfigMetaData.SummaryTable.NetworkAcount, STBConfigMetaData.SummaryTable.NetworkAcount);
		summaryProjMap.put(STBConfigMetaData.SummaryTable.NetworkPassword,
				STBConfigMetaData.SummaryTable.NetworkPassword);

		// 添加startmethod projection
		startmethodProjMap = new HashMap<String, String>();
		startmethodProjMap.put(STBConfigMetaData.StartmethodTable._ID, STBConfigMetaData.StartmethodTable._ID);
		startmethodProjMap.put(STBConfigMetaData.StartmethodTable.MapID, STBConfigMetaData.StartmethodTable.MapID);
		startmethodProjMap.put(STBConfigMetaData.StartmethodTable.isAlways,
				STBConfigMetaData.StartmethodTable.isAlways);
		startmethodProjMap.put(STBConfigMetaData.StartmethodTable.platform,
				STBConfigMetaData.StartmethodTable.platform);

		// 添加authentication
		authenticationProjMap = new HashMap<String, String>();
		authenticationProjMap.put(STBConfigMetaData.AuthenticationTable._ID, STBConfigMetaData.AuthenticationTable._ID);
		authenticationProjMap.put(STBConfigMetaData.AuthenticationTable.MapID,
				STBConfigMetaData.AuthenticationTable.MapID);
		authenticationProjMap.put(STBConfigMetaData.AuthenticationTable.Names,
				STBConfigMetaData.AuthenticationTable.Names);
		authenticationProjMap.put(STBConfigMetaData.AuthenticationTable.Values,
				STBConfigMetaData.AuthenticationTable.Values);
	}

	private DataBaseHelper mDBHelper;

	@Override
	public boolean onCreate() {
		mDBHelper = new DataBaseHelper(getContext());
		return (mDBHelper != null);
	}

	@Override
	public String getType(Uri uri) {
		// 能被_BASE整除为CONTENT_TYPE，否则为CONTENT_ITEM_TYPE
		if (sUriMatcher.match(uri) % _BASE == 0) {
			return STBConfigMetaData.CONTENT_TYPE;
		} else {
			return STBConfigMetaData.CONTENT_ITEM_TYPE;
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		Log.i(LOG_TAG, "STBConfig insert " + uri);

		// 1、拷贝一份数据
		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			throw new SQLException("Fail to insert to table: null values");
		}

		String table = null;
		// 2、确定表的名称、一些预置的段和mapid
		int code = sUriMatcher.match(uri);
		if (code >= SUMMARY && code <= SUMMARY_ID) {
			// summary表有且仅有一条记录，因此这里只执行一次
			table = STBConfigMetaData.SummaryTable.TABLE_NAME;
			// 没有设置则预置
			if (values.containsKey(STBConfigMetaData.SummaryTable.MAC) == false) {
				values.put(STBConfigMetaData.SummaryTable.MAC, SystemProperties.get("ro.mac", ""));
			}
			if (values.containsKey(STBConfigMetaData.SummaryTable.STBID) == false) {
				String sn = SystemProperties.get("ro.serialno", "");
				if (sn.length() > 32) {
					sn = sn.substring(0, 32);
				}
				values.put(STBConfigMetaData.SummaryTable.STBID, sn);
			}
			// summary只有一行，因此固定mapid为SUMMARY
			values.put("mapid", String.valueOf(SUMMARY));
		} else if (code >= STARTMETHOD && code <= STARTMETHOD_ID) {
			table = STBConfigMetaData.StartmethodTable.TABLE_NAME;
			// startmethod只有一行，因此固定mapid为STARTMETHOD
			values.put("mapid", String.valueOf(STARTMETHOD));
		} else if (code >= AUTHENTICATION && code <= AUTHENTICATION_ID) {
			table = STBConfigMetaData.AuthenticationTable.TABLE_NAME;
			String name = values.getAsString("name");

			Log.d(LOG_TAG, "insert value:"+name);
			if (name == null) {
				throw new IllegalArgumentException("Invalid value: " + values);
			}
			if (code == AUTHENTICATION) {
				// 如果uri没有指定name，则从value的name字段获取
				uri = Uri.parse(uri + "/" + name);
			} else {
				List<String> segments = uri.getPathSegments();
				if (segments != null && segments.size() > 1 && !name.equals(segments.get(1))) {
					// 如果uri指定name，但与value的name字段不相同
					throw new IllegalArgumentException("matched error uri's name(" + segments.get(1)
							+ ") != data's name(" + values.getAsString("name") + ")");
				}
			}
			values.put("mapid", String.valueOf(sUriMatcher.match(uri)));
		} else {
			throw new IllegalArgumentException("Unknown uri " + uri);
		}

		// 3、判断数据是否存在，如果存在，则使用update更新数据
		ContentResolver cr = getContext().getContentResolver();
		if (cr.query(uri, null, null, null, null).moveToFirst()) {
			Log.i(LOG_TAG, uri + " already exits, now execute 'update' rather than 'insert'");
			if (cr.update(uri, values, null, null) == -1)
				return null;
			else
				return uri;
		}

		// 4、数据不存在，则插入数据
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		long rowId = db.insert(table, null, values);
		if (rowId > 0) {
			Uri retUri = ContentUris.withAppendedId(uri, rowId);
			getContext().getContentResolver().notifyChange(uri, null);
			return retUri;
		}

		throw new SQLException("Insert failed " + uri);
	}

	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		
		Log.i(LOG_TAG, "STBConfig update " + uri+" values:"+values+" where:"+where);
		int j = 0;
//		for(j = 0;j < whereArgs.length;j++){
//			Log.d(LOG_TAG, "j:"+j+ "whereArgs:"+whereArgs[j]);
//		}

		// 1、先确定表的名称和selection条件
		String table = null;
		String selection = null;
		int code = sUriMatcher.match(uri);
		if (code >= SUMMARY && code <= SUMMARY_ID) {
			table = STBConfigMetaData.SummaryTable.TABLE_NAME;
			if (code == SUMMARY_ID) {
				// 采用_id过滤方式
				if (uri.getPathSegments().size() > 1)
					selection = "_id=" + uri.getPathSegments().get(1);
			} else {
				selection = "mapid=" + SUMMARY;
			}
		} else if (code >= STARTMETHOD && code <= STARTMETHOD_ID) {
			table = STBConfigMetaData.StartmethodTable.TABLE_NAME;
			if (code == STARTMETHOD_ID) {
				// 采用_id过滤方式
				if (uri.getPathSegments().size() > 1)
					selection = "_id=" + uri.getPathSegments().get(1);
			} else {
				selection = "mapid=" + STARTMETHOD;
			}
		} else if (code >= AUTHENTICATION && code <= AUTHENTICATION_ID) {
			table = STBConfigMetaData.AuthenticationTable.TABLE_NAME;
			String name = values.getAsString("name");
			if(name == null){
				if(whereArgs != null && whereArgs.length > 0){
					values.put("name", whereArgs[0]);
					name = values.getAsString("name");
				 }
			}
			if(name == null){
				if(where != null){
					Log.d(LOG_TAG, "where != null");
					String[] wheres = where.split("=");
					if(wheres.length == 2){
						Log.d(LOG_TAG, "where[0]"+wheres[0]+" where[1]"+wheres[1]);
						if(!wheres[1].equals("?")){
							Log.d(LOG_TAG, "wheres[1]:"+wheres[1]);
							name = wheres[1].replace("'", "");
							Log.d(LOG_TAG, "w name:"+name);
						}
					}
				}
			}
			if (name == null) {
				//return -1;
				throw new IllegalArgumentException("Invalid value: " + values);
			}
			if (code == AUTHENTICATION_ID) {
				// 采用_id过滤方式
				if (uri.getPathSegments().size() > 1)
					selection = "_id=" + uri.getPathSegments().get(1);
			} else {
				if (code == AUTHENTICATION) {
					// 针对不指定name的情况，加上name，然后重新匹配code
					// content://stbconfig/authentication ->
					// content://stbconfig/authentication/{name}
					uri = Uri.parse(uri + "/" + name);
					code = sUriMatcher.match(uri);
					if (code == UriMatcher.NO_MATCH) {
						throw new IllegalArgumentException("Invalid name: " + name);
					}
				}
				selection = "mapid=" + code;
			}
		} else {
			throw new IllegalArgumentException("Unknown uri " + uri);
		}

		// 2、判断数据是否存在，如果不存在，则使用insert创建
		ContentResolver cr = getContext().getContentResolver();
		if (cr.query(uri, null, selection, null, null).moveToFirst() == false) {
			Log.i(LOG_TAG, uri + " doesn't exits, now execute 'insert' rather than 'update'");
			if (cr.insert(uri, values) != null)
				return 1;
			else
				return -1;
		}

		// 3、数据已经存在，则更新数据
		int count = -1;
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		if (code % _BASE == 0) {
			count = db.update(table, values, where, whereArgs);
		} else {
			count = db.update(table, values, selection + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : ""),
					whereArgs);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Log.i(LOG_TAG, "STBConfig query " + uri);
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		// 1、确定orderBy、setTables、appendWhere、setProjectionMap
		String orderBy = (TextUtils.isEmpty(sortOrder)) ? null : sortOrder;
		int code = sUriMatcher.match(uri);
		if (code >= SUMMARY && code <= SUMMARY_ID) {
			Log.i(LOG_TAG, "query SUMMARY " + code);
			orderBy = (orderBy == null) ? STBConfigMetaData.SummaryTable.DEFAULT_ORDERBY : orderBy;
			queryBuilder.setTables(STBConfigMetaData.SummaryTable.TABLE_NAME);
			if (code == SUMMARY_ID) {
				// 采用_id过滤方式
				if (uri.getPathSegments().size() > 1)
					queryBuilder.appendWhere("_id=" + uri.getPathSegments().get(1));
			} else {
				queryBuilder.appendWhere("mapid=" + SUMMARY);
			}
			queryBuilder.setProjectionMap(summaryProjMap);
		} else if (code >= STARTMETHOD && code <= STARTMETHOD_ID) {
			Log.i(LOG_TAG, "query STARTMETHOD " + code);
			orderBy = (orderBy == null) ? STBConfigMetaData.StartmethodTable.DEFAULT_ORDERBY : orderBy;
			queryBuilder.setTables(STBConfigMetaData.StartmethodTable.TABLE_NAME);
			if (code == STARTMETHOD_ID) {
				// 采用_id过滤方式
				if (uri.getPathSegments().size() > 1)
					queryBuilder.appendWhere("_id=" + uri.getPathSegments().get(1));
			} else {
				queryBuilder.appendWhere("mapid=" + STARTMETHOD);
			}
			queryBuilder.setProjectionMap(startmethodProjMap);
		} else if (code >= AUTHENTICATION && code <= AUTHENTICATION_ID) {
			Log.i(LOG_TAG, "query AUTHENTICATION " + code);
			orderBy = (orderBy == null) ? STBConfigMetaData.AuthenticationTable.DEFAULT_ORDERBY : orderBy;
			queryBuilder.setTables(STBConfigMetaData.AuthenticationTable.TABLE_NAME);
			if (code == AUTHENTICATION_ID) {
				// 采用_id过滤方式
				if (uri.getPathSegments().size() > 1)
					queryBuilder.appendWhere("_id=" + uri.getPathSegments().get(1));
			} else if (code != AUTHENTICATION) {
				queryBuilder.appendWhere("mapid=" + sUriMatcher.match(uri));
			}
			queryBuilder.setProjectionMap(authenticationProjMap);
		} else {
			Log.e(LOG_TAG, "query <Unknown> " + code);
			throw new IllegalArgumentException("Unknown uri " + uri);
		}

		// 2、查询数据库
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, orderBy);
		Log.i(LOG_TAG, "query result count " + cursor.getCount());
		return cursor;
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		Log.i(LOG_TAG, "STBConfig delete " + uri);

		// 1、先确定表的名称和selection条件
		String table = null;
		String selection = null;
		int code = sUriMatcher.match(uri);
		if (code >= SUMMARY && code <= SUMMARY_ID) {
			table = STBConfigMetaData.SummaryTable.TABLE_NAME;
			if (code == SUMMARY_ID) {
				// 采用_id过滤方式
				if (uri.getPathSegments().size() > 1)
					selection = "_id=" + uri.getPathSegments().get(1);
			} else {
				selection = "mapid=" + SUMMARY;
			}
		} else if (code >= STARTMETHOD && code <= STARTMETHOD_ID) {
			table = STBConfigMetaData.StartmethodTable.TABLE_NAME;
			if (code == STARTMETHOD_ID) {
				// 采用_id过滤方式
				if (uri.getPathSegments().size() > 1)
					selection = "_id=" + uri.getPathSegments().get(1);
			} else {
				selection = "mapid=" + STARTMETHOD;
			}
		} else if (code >= AUTHENTICATION && code <= AUTHENTICATION_ID) {
			table = STBConfigMetaData.AuthenticationTable.TABLE_NAME;
			if (code == AUTHENTICATION_ID) {
				// 采用_id过滤方式
				if (uri.getPathSegments().size() > 1)
					selection = "_id=" + uri.getPathSegments().get(1);
			} else if (code != AUTHENTICATION) {
				selection = "mapid=" + sUriMatcher.match(uri);
			}
		}

		int count = -1;
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		if (code % _BASE == 0) {
			count = db.delete(table, where, whereArgs);
		} else {
			count = db.delete(table, selection + (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : ""), whereArgs);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}

/**
 * 数据库助手
 * 
 * @author Eniso
 *
 */
class DataBaseHelper extends SQLiteOpenHelper {

	public DataBaseHelper(Context context) {
		super(context, STBConfigMetaData.DATABASE_NAME, null, STBConfigMetaData.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(STBConfigMetaData.SummaryTable.SQL_CREATE_TABLE);
		db.execSQL(STBConfigMetaData.StartmethodTable.SQL_CREATE_TABLE);
		db.execSQL(STBConfigMetaData.AuthenticationTable.SQL_CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + STBConfigMetaData.SummaryTable.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + STBConfigMetaData.StartmethodTable.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + STBConfigMetaData.AuthenticationTable.TABLE_NAME);
		onCreate(db);
	}

}
