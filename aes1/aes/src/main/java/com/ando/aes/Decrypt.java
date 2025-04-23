package com.ando.aes;

import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
//import java.util.Base64;
import java.util.List;

import org.apache.commons.codec.binary.Base64;


/**
 * 解密类，实现AES解密算法的ECB和CBC两种模式
 * ECB模式: 电子密码本模式，每个密文块独立解密，不使用初始化向量
 * CBC模式: 密码块链接模式，使用初始化向量，每个明文块依赖于前一个密文块
 */

@Component
public class Decrypt {


    /**
     * AES-ECB模式解密
     *
     * @param cipher 16进制格式的密文字符串
     * @param key    16字节的密钥字符串
     * @return 解密后的明文字符串
     * 
     * 解密步骤：
     * 1. 将密文和密钥分组成4x4的矩阵
     * 2. 进行初始轮密钥加（使用最后一轮的子密钥）
     * 3. 进行10轮解密操作，每轮包括逆行移位、逆字节代换、轮密钥加和逆列混淆(第一轮除外)
     * 4. 将解密后的字节转换为字符串
     */
    public static String decryptECB(String cipher, String key) {
        // 0. 密文/密钥分组
        // 将16进制密文字符串分组成多个4x4的矩阵
        String[][][] cipherMatrix = Utils.divide4Hex(cipher);
        // 将密钥分组成一个4x4的矩阵
        String[][] initSecret = Utils.divide4Secret(key);
        // 用于存储解密后的明文字节
        List<Byte> list = new ArrayList<>(cipherMatrix.length << 4 + 16);
        
        // 1. 构造密钥组（密钥扩展）
        // 从初始密钥生成11组子密钥，用于10轮解密和初始轮密钥加
        String[][][] extendSecret = Utils.extendSecret(initSecret);
        
        // 对每一组密文矩阵进行解密
        for (int i = 0; i < cipherMatrix.length; i++) {
            // 2. 轮密钥加（使用最后一轮的子密钥）
            // 将当前密文矩阵与最后一组子密钥（第10轮）进行异或操作
            cipherMatrix[i] = Utils.xor4HexArr(cipherMatrix[i], extendSecret[10]);
            
            // 3. 进行10轮解密操作（逆向进行）
            for (int round = 9; round >= 0; round--) {
                // 逆行移位：将行移位操作逆向执行
                cipherMatrix[i] = Utils.moveRowInverse(cipherMatrix[i]);
                
                // 逆字节代替：使用逆S盒替换每个字节
                Utils.replaceByte(cipherMatrix[i], ConNum.S_INVERSE);
                
                // 轮密钥加：与对应轮的子密钥进行异或
                cipherMatrix[i] = Utils.xor4HexArr(cipherMatrix[i], extendSecret[round]);
                
                // 逆列混淆：将列混淆操作逆向执行（第一轮不进行）
                if (round != 0) {
                    cipherMatrix[i] = Utils.fixColumn(cipherMatrix[i], ConNum.FIX_COLUMN_INVERSE);
                }
            }
            
            // 4. 还原明文
            // 按列优先顺序将矩阵中的16个十六进制字符串转换为字节
            for (int k = 0; k < 4; k++) {
                for (int j = 0; j < 4; j++) {
                    // 使用Integer.parseInt代替Byte.valueOf，避免溢出
                    int intVal = Integer.parseInt(cipherMatrix[i][j][k], 16);
                    // 转换为字节，只保留低8位
                    byte byteVal = (byte) intVal;
                    // 只添加非零字节到结果中（解决填充问题）
                    list.add(byteVal);
                }
            }
        }
        
        // 移除末尾的 \0 字节（PKCS7/PKCS5 填充的移除）
        int lastIndex = list.size() - 1;
        while (lastIndex >= 0 && list.get(lastIndex) == 0) {
            lastIndex--;
        }
        
        // 创建字节数组并转换为字符串
        byte[] bytes = new byte[lastIndex + 1];
        for (int i = 0; i <= lastIndex; i++) {
            bytes[i] = list.get(i);
        }
        
        // 返回解密后的明文字符串
        return new String(bytes);
    }

    /**
     * AES-CBC模式解密
     *
     * @param base64Content Base64编码的密文字符串
     * @param key           16字节的密钥字符串
     * @return 解密后的明文字符串
     * @throws Exception 解密过程中可能出现的异常
     * 
     * CBC模式使用Java自带的解密库实现，需要与加密时使用相同的密钥和IV
     */
    public static String decryptCBC(String base64Content, String key) throws Exception {
        /**
         * content: 解密内容(base64编码格式)
         * slatKey: 加密时使用的盐，16位字符串
         * vectorKey: 加密时使用的向量，16位字符串
         */
//        public String decrypt(String base64Con, String slatKey, String vectorKey) throws Exception {
        // 创建AES/CBC/PKCS5Padding模式的Cipher对象
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        
        // 根据密钥创建SecretKey对象
        SecretKey secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        
        // 设置初始化向量IV（必须与加密时使用的IV相同）
        IvParameterSpec iv = new IvParameterSpec(ConNum.IV.getBytes("UTF-8"));
        
        // 初始化Cipher对象为解密模式
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        
        // 将Base64编码的密文解码为字节数组
        byte[] content = Base64.decodeBase64(base64Content);
        
        // 执行解密操作
        byte[] encrypted = cipher.doFinal(content);
        
        // 返回解密后的明文字符串
        return new String(encrypted, "UTF-8");
    }

}

