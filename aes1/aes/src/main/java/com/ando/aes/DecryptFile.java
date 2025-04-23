package com.ando.aes;

import java.io.*;

/**
 * 文件解密工具类
 * 提供对文件进行AES解密的功能，支持ECB和CBC两种模式
 */

public class DecryptFile {


    public static void decryptFileECB(String srcFilePath, String destFilePath, String key) {
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
            String cipher = new String(fileContent, "UTF-8");
            
            // 解密
            String plainText = Decrypt.decryptECB(cipher, key);
            
            // 写入解密后的内容
            fileOutputStream.write(plainText.getBytes("UTF-8"));
            
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

    public static void decryptFileCBC(String srcFilePath, String destFilePath, String key) throws Exception {
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
            String cipher = new String(fileContent, "UTF-8");
            
            // 解密
            String plainText = Decrypt.decryptCBC(cipher, key);
            
            // 写入解密后的内容
            fileOutputStream.write(plainText.getBytes("UTF-8"));
            
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
