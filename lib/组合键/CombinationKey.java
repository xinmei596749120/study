package com.android.internal.policy.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.util.Log;

/**
 * 匹配组合键，以及执行相应操作
 *
 * 配置文件格式说明：
 *   <CombinationKey name="MyActivity" type="activity">
 *   name可随意定义，type支持activity、service、broadcast、property、command
 *   activity、service、broadcast会首先执行property和command（如果存在）
 *
 *   <KeyCodes desc="9090">
 *      <key>16</key>
 *      <key>7</key>
 *      <key>16</key>
 *      <key>7</key>
 *   </KeyCodes>
 *   组合键定义，key对应Android键值
 *
 *   <PackageName>com.android.settings</PackageName>
 *   Package名，type="activity"或type="service"时有效，非必选，例如：调用浏览器
 *
 *   <ActivityName>com.android.settings.Settings</ActivityName>
 *   Activity名，type="activity"时，如果此项为空，那么会选择第一个Activity启动
 *
 *   <ServiceName>com.android.settings.Settings</ServiceName>
 *   Service名，type="service"时必选
 *
 *   <Action>android.intent.action.MASTER_CLEAR</Action>
 *   Action值，type="broadcast"时必选
 *
 *   <Data>http://www.example.com</Data>
 *   Data传递数据，必须符合URI规范，否则异常，可选
 *
 *   <Permission>android.permission.ACCESS_NETWORK_STATE</Permission>
 *   广播接收者权限，type="broadcast"时有效，参考Manifest.permission.*，可选
 *
 *   <Category>
 *      <name>android.intent.category.LAUNCHER</name>
 *      <name>android.intent.category.DEFAULT</name>
 *      <name>android.intent.category.MONKEY</name>
 *   </Category>
 *   Category，可选
 *
 *   <Flags>
 *      <name desc="Intent.FLAG_ACTIVITY_NEW_TASK">0x10000000</name>
 *   </Flags>
 *   Flags，参考Intent.FLAG_*，整型数字，可选
 *
 *   <Extra name="playUrl">http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8</Extra>
 *   <Extra name="play">pause</Extra>
 *   Extra传递数据，允许多项，可选
 *
 *   <Property key="persist.sys.debugenable">1</Property>
 *   Property设置，允许多项，可选
 *
 *   <Command>
 *      <cmd>setprop ctl.stop umcped</cmd>
 *      <cmd>rm -rf /data/umcpe/*</cmd>
 *      <cmd>reboot</cmd>
 *   </Command>
 *   Command执行命令组，可选
 *
 * @author Eniso
 *
 */
public class CombinationKey {

    private static final String TAG = "CombinationKey";

    private static final Integer[] EMPTY_INTEGER_ARRAY = new Integer[0];
    private static final List<String> EMPTY_STRING_LIST = new ArrayList<String>();
    private static final Map<String, String> EMPTY_MAP = new HashMap<String, String>();

    private Integer[] keys = null;

    @Attribute(name = "name", required = false)
    private String name;

    @Attribute(name = "type", required = false)
    private String type;

    @ElementList(name = "KeyCodes", entry = "key", required = false)
    private List<Integer> keyCodes;

    @Element(name = "PackageName", required = false)
    private String packageName;

    @Element(name = "ActivityName", required = false)
    private String activityName;

    @Element(name = "ServiceName", required = false)
    private String serviceName;

    @Element(name = "Action", required = false)
    private String action;

    @Element(name = "Data", required = false)
    private String data;

    @Element(name = "Permission", required = false)
    private String permission;

    @ElementList(name = "Category", entry = "name", required = false)
    private List<String> category;

    @ElementList(name = "Flags", entry = "name", required = false)
    private List<String> flags;

    @ElementMap(entry = "Extra", key = "name", attribute = true, inline = true, required = false)
    private Map<String, String> extra;

    @ElementMap(entry = "Property", key = "key", attribute = true, inline = true, required = false)
    private Map<String, String> property;

    @ElementList(name = "Command", entry = "cmd", required = false)
    private List<String> command;

    public String getName() {
        return name == null ? "" : name;
    }

    public String getType() {
        return type == null ? "" : type;
    }

    public Integer[] getKeyCodes() {
        if (keys == null)
            keys = (keyCodes == null) ? EMPTY_INTEGER_ARRAY : keyCodes.toArray(EMPTY_INTEGER_ARRAY);
        return keys;
    }

    public String getPackageName() {
        return packageName == null ? "" : packageName;
    }

    public String getActivityName() {
        return activityName == null ? "" : activityName;
    }

    public String getServiceName() {
        return serviceName == null ? "" : serviceName;
    }

    public String getAction() {
        return action;
    }

    public String getData() {
        return data;
    }

    public String getPermission() {
        return permission == null ? "" : permission;
    }

    public List<String> getCategory() {
        return category == null ? EMPTY_STRING_LIST : category;
    }

    public List<String> getFlags() {
        return flags == null ? EMPTY_STRING_LIST : flags;
    }

    public Map<String, String> getExtra() {
        return extra == null ? EMPTY_MAP : extra;
    }

    public Map<String, String> getProperty() {
        return property == null ? EMPTY_MAP : property;
    }

    public List<String> getCommand() {
        return command == null ? EMPTY_STRING_LIST : command;
    }

    public boolean execute(Context context, Integer[] inputKeys) {
        if (arrayIndexOf(inputKeys, getKeyCodes()) >= 0) {
            Log.i(TAG, "Matched CombinationKey: " + getName());
            if (getType().equals("activity")) {
                startActivity(context);
            } else if (getType().equals("service")) {
                startService(context);
            } else if (getType().equals("broadcast")) {
                sendBroadcast(context);
            } else if (getType().equals("property") || getType().equals("command")) {
                setProperties();
                executeCommands();
            } else {
                Log.e(TAG, "ERROR Unsupported type: " + getType());
                return false;
            }
            return true;
        }
        return false;
    }

    private Intent buildIntent(Intent intent, ComponentName cn) {
        setProperties();
        executeCommands();

        if (intent == null)
            intent = new Intent(getAction(), (getData() == null) ? null : Uri.parse(getData()));
        if (cn != null)
            intent.setComponent(cn);

        for (String category : getCategory()) {
            if (category != null)
                intent.addCategory(category);
        }

        for (String flags : getFlags()) {
            Log.i(TAG, "buildIntent addFlags " + flags);
            if (flags != null && flags.length() > 0) {
                if (flags.startsWith("0x") || flags.startsWith("0X")) {
                    if (flags.length() > 2)
                        intent.addFlags(Integer.parseInt(flags.substring(2), 16));
                } else {
                    intent.addFlags(Integer.parseInt(flags, 10));
                }
            }
        }

        Map<String, String> extra = getExtra();
        for (String name : extra.keySet()) {
            if (name != null)
                intent.putExtra(name, extra.get(name));
        }
        return intent;
    }

    private void startActivity(Context context) {
        try {
            Intent intent;
            if (getActivityName().length() > 0)
                intent = buildIntent(null, new ComponentName(getPackageName(), getActivityName()));
            else
                intent = buildIntent(context.getPackageManager().getLaunchIntentForPackage(getPackageName()), null);

            Log.i(TAG, "startActivity " + intent.getComponent());
            context.startActivityAsUser(intent, UserHandle.CURRENT);
        } catch (Exception e) {
            Log.w(TAG, "ERROR startActivity failed", e);
        }
    }

    private void startService(Context context) {
        try {
            ComponentName cn = new ComponentName(getPackageName(), getServiceName());
            Intent intent = buildIntent(null, cn);

            Log.i(TAG, "startService " + cn);
            context.startServiceAsUser(intent, UserHandle.CURRENT);
        } catch (Exception e) {
            Log.w(TAG, "ERROR startService failed", e);
        }
    }

    private void sendBroadcast(Context context) {
        try {
            Intent intent = buildIntent(null, null);
            Log.i(TAG, "sendBroadcast " + intent);

            String permission = getPermission();
            if (permission.length() > 0)
                context.sendBroadcastAsUser(intent, UserHandle.ALL, permission);
            else
                context.sendBroadcastAsUser(intent, UserHandle.ALL);
        } catch (Exception e) {
            Log.w(TAG, "ERROR sendBroadcast failed", e);
        }
    }

    private void setProperties() {
        Map<String, String> property = getProperty();
        for (String key : property.keySet()) {
            if (key == null)
                continue;

            String value = property.get(key);
            Log.i(TAG, "set property '" + key + "' => '" + value + "'");
            try {
                SystemProperties.set(key, value);
            } catch (Exception e) {
                Log.w(TAG, "ERROR setProperties failed", e);
            }
        }
    }

    private void executeCommands() {
        for (String command : getCommand()) {
            if (command == null || command.length() <= 0)
                continue;

            Log.i(TAG, "execute command '" + command + "'");
            try {
                Runtime.getRuntime().exec(command);
            } catch (Exception e) {
                Log.w(TAG, "ERROR executeCommands failed", e);
            }
        }
    }

    private static final int arrayIndexOf(Integer[] source, Integer[] target) {
        if (source == null || target == null || source.length == 0 || target.length == 0
                || source.length < target.length)
            return -1;

        try {
            int first = target[0].intValue();
            int max = source.length - target.length;
            for (int i = 0; i <= max; i++) {
                if (source[i].intValue() != first)
                    while (++i <= max && source[i].intValue() != first);

                if (i <= max) {
                    int j = i + 1;
                    int end = j + target.length - 1;
                    for (int k = 1; j < end && source[j].intValue() == target[k].intValue(); j++, k++);
                    if (j == end)
                        return i;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "ERROR arrayIndexOf", e);
        }

        return -1;
    }

}
