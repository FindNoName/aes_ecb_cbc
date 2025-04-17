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
 * @author Ando
 * @version 1.0
 * @date 2022/6/3
 */

@Component
public class Decrypt {


    /**
     * 解密
     *
     * @param cipher 密文16进制字符串
     * @param key    密钥字符串
     */
    public static String decryptECB(String cipher, String key) {
        // 0. 密文/密钥分组
        String[][][] cipherMatrix = Utils.divide4Hex(cipher);
        String[][] initSecret = Utils.divide4Secret(key);
        List<Byte> list = new ArrayList<>(cipherMatrix.length << 4 + 16);
        // 1. 构造密钥组
        String[][][] extendSecret = Utils.extendSecret(initSecret);
        for (int i = 0; i < cipherMatrix.length; i++) {
            // 2. 轮密钥加
            cipherMatrix[i] = Utils.xor4HexArr(cipherMatrix[i], extendSecret[10]);
            // 3.10轮重复加密操作
            for (int round = 9; round >= 0; round--) {
                // 逆行移位
                cipherMatrix[i] = Utils.moveRowInverse(cipherMatrix[i]);
                // 逆字节代替
                Utils.replaceByte(cipherMatrix[i], ConNum.S_INVERSE);
                // 轮密钥加
                cipherMatrix[i] = Utils.xor4HexArr(cipherMatrix[i], extendSecret[round]);
                // 逆列混淆(第十轮不进行)
                if (round != 0) {
                    cipherMatrix[i] = Utils.fixColumn(cipherMatrix[i], ConNum.FIX_COLUMN_INVERSE);
                }
            }
            // 4.还原明文
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
        
        byte[] bytes = new byte[lastIndex + 1];
        for (int i = 0; i <= lastIndex; i++) {
            bytes[i] = list.get(i);
        }
        
        return new String(bytes);
    }

    public static String decryptCBC(String base64Content, String key) throws Exception {
        /**
         * content: 解密内容(base64编码格式)
         * slatKey: 加密时使用的盐，16位字符串
         * vectorKey: 加密时使用的向量，16位字符串
         */
//        public String decrypt(String base64Con, String slatKey, String vectorKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKey secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        IvParameterSpec iv = new IvParameterSpec(ConNum.IV.getBytes("UTF-8"));
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        byte[] content = Base64.decodeBase64(base64Content);
        byte[] encrypted = cipher.doFinal(content);
        return new String(encrypted, "UTF-8");
    }

}

