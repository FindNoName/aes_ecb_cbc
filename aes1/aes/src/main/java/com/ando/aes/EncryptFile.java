package com.ando.aes;

import java.io.*;

/**
 * 文件加密工具类
 * 提供对文件进行AES加密的功能，支持ECB和CBC两种模式
 */
public class EncryptFile {

    public static void encryptFileECB(String srcFilePath, String destFilePath, String key) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(srcFilePath);
            fileOutputStream = new FileOutputStream(destFilePath);
            
            // 读取整个文件内容
            byte[] fileContent = new byte[fileInputStream.available()];
            fileInputStream.read(fileContent);
            
            // 将字节数组转换为字符串
            String plainText = new String(fileContent, "UTF-8");
            
            // 加密
            String cipher = Encrypt.encryptECB(plainText, key);
            
            // 写入加密后的内容
            fileOutputStream.write(cipher.getBytes("UTF-8"));
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) fileInputStream.close();
                if (fileOutputStream != null) fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void encryptFileCBC(String srcFilePath, String destFilePath, String key) throws Exception {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(srcFilePath);
            fileOutputStream = new FileOutputStream(destFilePath);
            
            // 使用ByteArrayOutputStream来收集所有数据
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fileInputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            
            // 获取完整的文件内容
            byte[] fileContent = baos.toByteArray();
            String plainText = new String(fileContent, "UTF-8");
            
            // 加密
            String cipher = Encrypt.encryptCBC(plainText, key);
            
            // 写入加密后的内容
            fileOutputStream.write(cipher.getBytes("UTF-8"));
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) fileInputStream.close();
                if (fileOutputStream != null) fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}