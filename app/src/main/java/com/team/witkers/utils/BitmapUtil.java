package com.team.witkers.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by xuyue on 16/1/7.
 */
public class BitmapUtil {
    private static String TAG = BitmapUtil.class.getSimpleName();

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    // 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响
    private static Bitmap createScaleBitmap(Bitmap src, int dstWidth,
                                            int dstHeight) {
        Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
        if (src != dst) { // 如果没有缩放，那么不回收
            src.recycle(); // 释放Bitmap的native像素数组
        }
        return dst;
    }

    // 从Resources中加载图片
    public static Bitmap decodeSampledBitmap(Resources res,  int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options); // 读取图片长款
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight); // 计算inSampleSize
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeResource(res, resId, options); // 载入一个稍大的缩略图
        return createScaleBitmap(src, reqWidth, reqHeight); // 进一步得到目标大小的缩略图
    }

    public static Bitmap decodeSampledBitmap(Bitmap bitmap, int quality, int reqWidth, int reqHeight) {
        //先进行质量压缩
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
        if (bos != null) {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        if (bis != null) {
            try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //再进行尺寸压缩
        return createScaleBitmap(BitmapFactory.decodeStream(bis), reqWidth, reqHeight);
    }


    // 从sd卡上加载图片
    public static Bitmap decodeSampledBitmap(String pathName, int reqWidth, int reqHeight) {
        /**
         * 设置inJustDecodeBounds为true后，decodeFile并不分配空间，但可计算出原始图片的长度和宽度，
         * 即opts.width和opts.height。
         * 有了这两个参数，再通过一定的算法，即可得到一个恰当的inSampleSize。
         */
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeFile(pathName, options);
        return createScaleBitmap(src, reqWidth, reqHeight);
    }


    /**
     * 从sd卡上加载图片
     *
     * @param pathName  文件路径
     * @param zoomScale 缩放比例
     *                  表示缩略图大小为原始图片大小的几分之一，即如果这个值为2，
     *                  则取出的缩略图的宽和高都是原始图片的1/2，图片大小就为原始大小的1/4
     * @return
     */
    public static Bitmap decodeSampledBitmap(String pathName, int zoomScale) {
        /**
         * 设置inJustDecodeBounds为true后，decodeFile并不分配空间，但可计算出原始图片的长度和宽度，
         * 即opts.width和opts.height。
         * 有了这两个参数，再通过一定的算法，即可得到一个恰当的inSampleSize。
         */
        final BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = zoomScale;
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeFile(pathName, options);
        return src;
    }


    public static void saveBitmapFile(Bitmap bitmap, String filePath, String fileName) {

        File f = FileUtil.makeFilePath(filePath, fileName);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void saveBitmapFile(Bitmap bitmap, File file) {

        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 图片旋转
     *
     * @param bmp    要旋转的图片
     * @param degree 图片旋转的角度，负值为逆时针旋转，正值为顺时针旋转
     * @return
     */
    public static Bitmap rotateBitmap(Bitmap bmp, float degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
    }

    public static String bitmapBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        return Base64.encodeToString(bytes, 0);
    }

    public static Bitmap base64ToBitmap(String iconBase64) {
        byte[] bitmapArray;
        bitmapArray = Base64.decode(iconBase64, 0);
        return BitmapFactory
                .decodeByteArray(bitmapArray, 0, bitmapArray.length);
    }

    /**
     * 通过图片路径获得bitmap（无压缩形式）
     *
     * @param path 图片路径
     * @return Bitmap
     */
    public static Bitmap getBitmapFromLocal(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        return bitmap;
    }

    /**
     * 通过bitmap得到输出流（无压缩形式）
     *
     * @param bitmap bitmap对象
     * @return OutputStream
     */
    public static ByteArrayOutputStream getOutStreamFromBitmap(Bitmap bitmap) {
        return getOutStreamFromBitmap(bitmap, 100);
    }

    /**
     * 通过bitmap得到输出流（质量压缩）
     *
     * @param bitmap  bitmap对象
     * @param quality 要压缩到的质量（0-100）
     * @return OutputStream
     */
    public static ByteArrayOutputStream getOutStreamFromBitmap(Bitmap bitmap, int quality) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
        if (bos != null) {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bos;
    }

    /**
     * 通过流获得bitmap
     *
     * @param os 输出流
     * @return Bitmap
     */
    public static Bitmap getBitmapFromOutStream(ByteArrayOutputStream os) {
        ByteArrayInputStream bis = new ByteArrayInputStream(os.toByteArray());
        if (bis != null) {
            try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return BitmapFactory.decodeStream(bis);
    }

    /**
     * 通过路径得到图片并对图片进行压缩，并再生成图片（质量压缩）
     *
     * @param imagePath 图片的路径
     * @param savePath  新图片的保存路径
     * @param quality   要压缩到的质量
     * @return Boolean true 成功false失败
     */
    public static boolean writeCompressImage2File(String imagePath, String savePath, int quality) {
        if (TextUtils.isEmpty(imagePath)) {
            return false;
        }
        String path = savePath.substring(0, savePath.lastIndexOf("/"));
        String filename = savePath.substring(savePath.lastIndexOf("/") + 1, savePath.length());
        return writeImage2File(getOutStreamFromBitmap(getBitmapFromLocal(imagePath), quality), path, filename);
    }

    /**
     * 把bitmap写入指定目录下，重新生成图片
     *
     * @param bitmap    bitmap对象
     * @param savePath  新图片保存路径
     * @param imageName 新图片的名字，会根据时间来命名
     * @return Boolean true 成功false失败
     */
    public static boolean writeImage2File(Bitmap bitmap, String savePath, String imageName) {
        return writeImage2File(getOutStreamFromBitmap(bitmap), savePath, imageName);
    }

    /**
     * 通过输出流，重组图片，并保存带指定目录下
     *
     * @param bos       图片输入流
     * @param savePath  新图片的保存路径
     * @param imageName 新图片的名字，字段为空时，会根据时间来命名
     * @return Boolean true 成功false失败
     */
    public static boolean writeImage2File(ByteArrayOutputStream bos, String savePath, String imageName) {
        if (TextUtils.isEmpty(savePath)) {
            return false;
        }
        File file = new File(savePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        FileOutputStream fos;
        try {
//            if(TextUtils.isEmpty(imageName)){
//                imageName = System.currentTimeMillis()+"";
//            }
            File f = new File(file, imageName);
            fos = new FileOutputStream(f);
            fos.write(bos.toByteArray());
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public Bitmap ratio(Bitmap image, float pixelW, float pixelH) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, os);
        if (os.toByteArray().length / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            os.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, os);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = pixelH;// 设置高度为240f时，可以明显看到图片缩小了
        float ww = pixelW;// 设置宽度为120f，可以明显看到图片缩小了
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0) be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        is = new ByteArrayInputStream(os.toByteArray());
        bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        //压缩好比例大小后再进行质量压缩
//      return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
    }

}
