package com.bjdwd.tools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.bjdwd.activitys.HtmlWebViewActivity;

import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

/**
 * Created by dell on 2017/3/30.
 */

public class FileShowTool {

    //打开文件
    public static void setFileShow(Context context, Map fileInfoMap) {
        String fileExtension = fileInfoMap.get("Extension").toString();
        fileExtension.toLowerCase();
        String filepath = fileInfoMap.get("LocaPath").toString();
        if (fileExtension.equals(".zip") || fileExtension.equals(".rar")) {
            if (filepath != null && !filepath.equals("")) {
                File file = new File(filepath);
                if (file != null && file.isFile() == true) {
                    Intent zrintent = new Intent();
                    zrintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    zrintent.setAction(android.content.Intent.ACTION_VIEW);
                    zrintent.setDataAndType(Uri.fromFile(file), "application/x-gzip");
                    context.startActivity(zrintent);
                }
            } else {
                ShowToastTool.showToast(context, "该文件存在问题");
            }

        } else if (fileExtension.equals(".htm")) {
            if (filepath != null && !filepath.equals("")) {
                Intent htmlintent = new Intent(context, HtmlWebViewActivity.class);
                htmlintent.putExtra("FilePath", filepath);
                context.startActivity(htmlintent);
            } else {
                ShowToastTool.showToast(context, "该文件存在问题");
            }

        } else if (fileExtension.equals(".mp4") || fileExtension.equals(".mp3")) {
            if (filepath != null && !filepath.equals("")) {
                Intent videointent = new Intent(Intent.ACTION_VIEW);
                videointent.setDataAndType(Uri.parse(filepath), "video/*");
                context.startActivity(videointent);
            } else {
                ShowToastTool.showToast(context, "该文件存在问题");
            }
        } else if (fileExtension.equals(".doc") || fileExtension.equals(".ppt") || fileExtension.equals(".xls") || fileExtension.equals(".pdf") || fileExtension.equals(".rtf")) {
            if (filepath != null && !filepath.equals("")) {
                WpsTool.wpsOpenFile(filepath, context);
            } else {
                ShowToastTool.showToast(context, "该文件存在问题");
            }
        } else if (fileExtension.equals(".png") || fileExtension.equals(".jpg")) {
            if (filepath != null && !filepath.equals("")) {
                File file = new File(filepath);
                if (file != null && file.isFile() == true) {
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), "image/*");
                    context.startActivity(intent);
                }
            } else {
                ShowToastTool.showToast(context, "该文件存在问题");
            }
        } else {
            ShowToastTool.showToast(context, "该文件存在问题");
        }
    }

    //获取文件的解码字体
    public static String getFileIncode(File file) {

        if (!file.exists()) {
            System.err.println("getFileIncode: file not exists!");
            return null;
        }

        byte[] buf = new byte[4096];
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            // (1)
            UniversalDetector detector = new UniversalDetector(null);
            // (2)
            int nread;
            while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
                detector.handleData(buf, 0, nread);
            }
            // (3)
            detector.dataEnd();
            // (4)
            String encoding = detector.getDetectedCharset();
            if (encoding != null) {
                System.out.println("Detected encoding = " + encoding);
            } else {
                System.out.println("No encoding detected.");
            }
            // (5)
            detector.reset();
            fis.close();
            return encoding;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}