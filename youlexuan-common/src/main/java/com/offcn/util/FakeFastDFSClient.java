package com.offcn.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class FakeFastDFSClient {
    public static String uploadFile(byte[] bytes, String extName) {

        String nginxPath = "D:\\Develop\\nginx-1.16.1\\html";
        File file = new File(nginxPath, UUID.randomUUID().toString() + "." +extName);

        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(bytes);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "/" + file.getName();
    }
}
