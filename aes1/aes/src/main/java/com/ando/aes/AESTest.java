package com.ando.aes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AESTest {

    public static void main(String[] args) {
        try {
            // 指定文件路径和密钥
            String inputFilePath = "aes/gg/test.txt";  // 要加密的原始文件
            String ecbEncryptedFilePath = "aes/gg/ecb_encrypted.txt";  // ECB模式加密输出
            String ecbDecryptedFilePath = "aes/gg/ecb_decrypted.txt";  // ECB模式解密输出
            String cbcEncryptedFilePath = "aes/gg/cbc_encrypted.txt";  // CBC模式加密输出
            String cbcDecryptedFilePath = "aes/gg/cbc_decrypted.txt";  // CBC模式解密输出
            String key = "1234567890123456"; // 16位密钥
            
            // 创建测试文件（如果不存在）
            createTestFile(inputFilePath, "这是一个测试文件，用于测试AES加密和解密功能。\n这里包含中文和符号！@#￥%……&*（）");
            
            // 读取原始文件内容
            byte[] fileContent = Files.readAllBytes(Paths.get(inputFilePath));
            String plainText = new String(fileContent, "UTF-8");
            System.out.println("原始文件内容: " + plainText);
            
            // 测试ECB模式
            System.out.println("\n===== ECB模式 =====");
            testECBModeFile(inputFilePath, ecbEncryptedFilePath, ecbDecryptedFilePath, key);
            
            // 测试CBC模式
            System.out.println("\n===== CBC模式 =====");
            testCBCModeFile(inputFilePath, cbcEncryptedFilePath, cbcDecryptedFilePath, key);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void createTestFile(String filePath, String content) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs(); // 确保目录存在
                Files.write(Paths.get(filePath), content.getBytes("UTF-8"));
                System.out.println("已创建测试文件: " + filePath);
            }
        } catch (Exception e) {
            System.out.println("创建测试文件失败: " + e.getMessage());
        }
    }
    
    private static void testECBModeFile(String inputFilePath, String encryptedFilePath, String decryptedFilePath, String key) {
        try {
            // 1. 加密文件
            System.out.println("进行ECB文件加密...");
            EncryptFile.encryptFileECB(inputFilePath, encryptedFilePath, key);
            System.out.println("ECB加密完成，已保存到: " + encryptedFilePath);
            
            // 2. 解密文件
            System.out.println("进行ECB文件解密...");
            DecryptFile.decryptFileECB(encryptedFilePath, decryptedFilePath, key);
            System.out.println("ECB解密完成，已保存到: " + decryptedFilePath);
            
            // 3. 验证解密结果
            byte[] originalContent = Files.readAllBytes(Paths.get(inputFilePath));
            byte[] decryptedContent = Files.readAllBytes(Paths.get(decryptedFilePath));
            
            if (compareByteArrays(originalContent, decryptedContent)) {
                System.out.println("ECB模式验证成功：解密后的内容与原始内容一致。");
            } else {
                System.out.println("ECB模式验证失败：解密后的内容与原始内容不一致。");
                System.out.println("原始内容长度: " + originalContent.length + ", 解密后内容长度: " + decryptedContent.length);
            }
        } catch (Exception e) {
            System.out.println("ECB模式测试出错:");
            e.printStackTrace();
        }
    }
    
    private static void testCBCModeFile(String inputFilePath, String encryptedFilePath, String decryptedFilePath, String key) {
        try {
            // 1. 加密文件
            System.out.println("进行CBC文件加密...");
            EncryptFile.encryptFileCBC(inputFilePath, encryptedFilePath, key);
            System.out.println("CBC加密完成，已保存到: " + encryptedFilePath);
            
            // 2. 解密文件
            System.out.println("进行CBC文件解密...");
            DecryptFile.decryptFileCBC(encryptedFilePath, decryptedFilePath, key);
            System.out.println("CBC解密完成，已保存到: " + decryptedFilePath);
            
            // 3. 验证解密结果
            byte[] originalContent = Files.readAllBytes(Paths.get(inputFilePath));
            byte[] decryptedContent = Files.readAllBytes(Paths.get(decryptedFilePath));
            
            if (compareByteArrays(originalContent, decryptedContent)) {
                System.out.println("CBC模式验证成功：解密后的内容与原始内容一致。");
            } else {
                System.out.println("CBC模式验证失败：解密后的内容与原始内容不一致。");
                System.out.println("原始内容长度: " + originalContent.length + ", 解密后内容长度: " + decryptedContent.length);
            }
        } catch (Exception e) {
            System.out.println("CBC模式测试出错:");
            e.printStackTrace();
        }
    }
    
    private static boolean compareByteArrays(byte[] arr1, byte[] arr2) {
        if (arr1.length != arr2.length) {
            return false;
        }
        
        for (int i = 0; i < arr1.length; i++) {
            if (arr1[i] != arr2[i]) {
                return false;
            }
        }
        
        return true;
    }
}