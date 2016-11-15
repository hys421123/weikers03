package com.team.witkers.utils;

import android.util.Log;

import java.io.File;

/**
 * Created by zcf on 2016/4/17.
 */
public class FileUtil {

    private static String TAG = "图片相关:";
    /**
     * 生成文件夹
     *
     * @param filePath
     */
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
//                T.showShort(App.getContext(),"文件夹创建成功");
            } else {
//                T.showShort(App.getContext(),"文件夹已存在");
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }

    /**
     * 生成文件
     *
     * @param filePath
     * @param fileName
     * @return
     */
    public static File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            Log.e(TAG, "图片保存出错：" + e.toString());
        }
        return file;
    }
}
