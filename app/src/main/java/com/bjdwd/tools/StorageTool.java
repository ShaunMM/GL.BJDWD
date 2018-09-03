package com.bjdwd.tools;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.UUID;
import java.util.zip.ZipException;

/**
 * Created by Dell on 2017/12/12.
 */
public class StorageTool {

    public static String FILEPATH = "BJDWD/Files";

    //解压带中文的zip文件
    public static void unZipFile(String archive, String decompressDir) throws IOException {
        BufferedInputStream bi;
        ZipFile zf = new ZipFile(archive, "GBK");
        Enumeration e = zf.getEntries();
        while (e.hasMoreElements()) {
            ZipEntry ze2 = (ZipEntry) e.nextElement();
            String entryName = ze2.getName();
            String path = decompressDir + "/" + entryName;
            if (ze2.isDirectory()) {
                System.out.println("正在创建解压目录 - " + entryName);
                File decompressDirFile = new File(path);
                if (!decompressDirFile.exists()) {
                    decompressDirFile.mkdirs();
                }
            } else {
                System.out.println("正在创建解压文件 - " + entryName);
                String fileDir = path.substring(0, path.lastIndexOf("/"));
                File fileDirFile = new File(fileDir);
                if (!fileDirFile.exists()) {
                    fileDirFile.mkdirs();
                }
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(decompressDir + "/" + entryName));
                bi = new BufferedInputStream(zf.getInputStream(ze2));
                byte[] readContent = new byte[1024];
                int readCount = bi.read(readContent);
                while (readCount != -1) {
                    bos.write(readContent, 0, readCount);
                    readCount = bi.read(readContent);
                }
                bos.close();
            }
        }
        zf.close();
    }

    //遍历指定文件夹获取。htm文件
    public static String getFileDir(String filePath) {
        String filepath = "";
        try {
            File f = new File(filePath);
            File[] files = f.listFiles();// 列出所有文件
            if (files != null) {
                int count = files.length;// 文件个数
                for (int i = 0; i < count; i++) {
                    File file = files[i];
                    if (!file.getName().endsWith(".files")) {
                        if (file.getName().endsWith(".htm") || file.getName().endsWith(".html")) {
                            //filepath=file.getName().substring(0,file.getName().lastIndexOf("."));
                            filepath = file.getPath();
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return filepath;
    }

    //获得guid
    public static String getuuid() {
        String guid = UUID.randomUUID() + "";
        return guid;
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
