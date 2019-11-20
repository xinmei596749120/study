package com.android.internal.policy.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Persister;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;

/**
 * 实现按键的记录，以及组合键配置文件的解析
 * {@code KEY_CAPACITY} 存储按键的最大容量
 * {@code MAX_KEY_TIME_MILLIS} 按键最大时间间隔（毫秒）
 * {@code COMBINATION_KEYS_FILE} 组合键配置文件
 *
 * @author Eniso
 *
 */
@Root(name = "CombinationKeys")
public class CombinationKeys {

    private static final String TAG = "CombinationKeys";
    private static final int KEY_CAPACITY = 16;
    private static final long MAX_KEY_TIME_MILLIS = 1000;
    private static final File COMBINATION_KEYS_FILE = new File("/system/etc/combinationKeys.xml");
    private static final List<Integer> mKeyList = new ArrayList<Integer>(KEY_CAPACITY);
    private static volatile CombinationKeys mInstance;
    private static long mKeyTime = 0;

    @ElementList(entry = "CombinationKey", inline = true, required = false)
    private List<CombinationKey> mCombinationKeys = new ArrayList<CombinationKey>();

    public static void execute(Context context, KeyEvent event) {
        final CombinationKeys instance = get();
        if (instance == null || instance.mCombinationKeys == null || instance.mCombinationKeys.size() < 1)
            return;

        int keyCode = event.getKeyCode();
        int action = event.getAction();

        if (action == KeyEvent.ACTION_UP) {
            /* 记录按键 */
            final long now = SystemClock.uptimeMillis();
            if (now > (mKeyTime + MAX_KEY_TIME_MILLIS))
                mKeyList.clear();
            while (mKeyList.size() >= KEY_CAPACITY)
                mKeyList.remove(0);
            mKeyList.add(keyCode);
            mKeyTime = now;

            boolean needsClear = false;
            final Integer[] inputKeys = mKeyList.toArray(new Integer[0]);

            /* 传给每个CombinationKey处理 */
            for (CombinationKey ck : mInstance.mCombinationKeys) {
                if (ck.execute(context, inputKeys))
                    needsClear = true;
            }

            /* 如果有一个以上的CombinationKey成功匹配按键列表，那么清空按键列表 */
            if (needsClear)
                mKeyList.clear();
        }
    }

    private static CombinationKeys get() {
        if (mInstance == null && COMBINATION_KEYS_FILE.exists()) {
            synchronized (CombinationKeys.class) {
                if (mInstance == null) {
                    InputStream is = null;
                    try {
                        is = new FileInputStream(COMBINATION_KEYS_FILE);
                        /* 解析配置文件 */
                        mInstance = new Persister().read(CombinationKeys.class, is);
                    } catch (Exception e) {
                        Log.e(TAG, "ERROR Parse " + COMBINATION_KEYS_FILE, e);
                    } finally {
                        try {
                            if (is != null)
                                is.close();
                        } catch (Exception e) {}
                    }
                }
            }
        }

        return mInstance;
    }

}
