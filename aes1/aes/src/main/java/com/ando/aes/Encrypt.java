package com.ando.aes;


import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
//import java.util.Base64;
import org.apache.commons.codec.binary.Base64;

import java.security.NoSuchAlgorithmException;

/**
 * 加密类，实现AES加密算法的ECB和CBC两种模式
 * ECB模式: 电子密码本模式，每个明文块独立加密，不使用初始化向量
 * CBC模式: 密码块链接模式，使用初始化向量，每个密文块依赖于前一个密文块
 */

@Component
public class Encrypt {

//    public static void main(String[] args) {
//        System.out.println(encryptCBC("hello", "feilZigbpEUihBoe"));
//    }

    /**
     * AES-ECB模式加密
     *
     * @param plainText 待加密的明文字符串
     * @param key       16字节的密钥字符串
     * @return 加密后的16进制密文字符串
     * 
     * 加密步骤：
     * 1. 将明文和密钥分组成4x4的矩阵
     * 2. 进行初始轮密钥加
     * 3. 进行10轮AES加密操作，每轮包括字节代换、行移位、列混淆(最后一轮除外)和轮密钥加
     * 4. 拼接最终密文结果
     */
    public static String encryptECB(String plainText, String key) {
        // 0. 明文/密钥分组
        // 将明文分组成多个4x4的矩阵，每个矩阵16字节
        String[][][] plainMatrix = Utils.divide4PlainText(plainText);
        // 将密钥分组成一个4x4的矩阵
        String[][] initSecret = Utils.divide4Secret(key);
        // 用于拼接最终的密文结果
        StringBuilder sb = new StringBuilder(plainMatrix.length << 4); // 用于拼接密文
        
        // 1. 构造密钥组（密钥扩展）
        // 从初始密钥生成11组子密钥，用于10轮加密和初始轮密钥加
        String[][][] extendSecret = Utils.extendSecret(initSecret);
        
        // 对每一组明文矩阵进行加密
        for (int i = 0; i < plainMatrix.length; i++) {
            // 2. 轮密钥加（初始轮）
            // 将当前明文矩阵与第一组子密钥进行异或操作
            plainMatrix[i] = Utils.xor4HexArr(plainMatrix[i], extendSecret[0]);
            
            // 3. 进行10轮加密操作
            for (int round = 1; round <= 10; round++) {
                // 字节代换：通过S盒替换每个字节
                Utils.replaceByte(plainMatrix[i], ConNum.S);
                
                // 行移位：通过循环左移操作混淆数据
                plainMatrix[i] = Utils.moveRow(plainMatrix[i]);
                
                // 列混淆：通过矩阵乘法增加扩散性（最后一轮不进行）
                if (round != 10) {
                    plainMatrix[i] = Utils.fixColumn(plainMatrix[i], ConNum.FIX_COLUMN);
                }
                
                // 轮密钥加：与当前轮的子密钥进行异或
                plainMatrix[i] = Utils.xor4HexArr(plainMatrix[i], extendSecret[round]);
            }
            
            // 4. 拼接加密后的密文结果
            // 按列优先顺序将矩阵中的16个字节转为16进制字符串拼接
            for (int k = 0; k < 4; k++) {
                for (int j = 0; j < 4; j++) {
                    sb.append(plainMatrix[i][j][k]);
                }
            }
        }
        return sb.toString();
    }

    /**
     * AES-CBC模式加密
     * 
     * @param plainText 待加密的明文字符串
     * @param key       16字节的密钥字符串
     * @return Base64编码的密文字符串
     * @throws Exception 加密过程中可能出现的异常
     * 
     * CBC模式使用Java自带的加密库实现，相比ECB模式更安全
     * 因为CBC模式使用初始化向量，相同的明文每次加密结果都不同
     */
    public static String encryptCBC(String plainText, String key) throws Exception {
        // 创建AES/CBC/PKCS5Padding模式的Cipher对象
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        
        // 根据密钥创建SecretKey对象
        SecretKey secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        
        // 设置初始化向量IV
        IvParameterSpec iv = new IvParameterSpec(ConNum.IV.getBytes("UTF-8"));
        
        // 初始化Cipher对象为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        
        // 执行加密操作
        byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
        
        // 返回Base64编码后的密文字符串
        return Base64.encodeBase64String(encrypted);
    }
}

//        try {
//
//            byte[] bytes = encodeRule.getBytes("UTF-8");
//            SecretKeySpec keySpec = new SecretKeySpec(bytes, "AES");
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
//            byte[] bytes1 = cipher.doFinal(content.getBytes("UTF-8"));
//            String result = Base64.getEncoder().encodeToString(bytes1);
//            return result;
//
//        } catch (Exception e) {
//           e.printStackTrace();
//        }
//        return null;
//    }

//        try {
//            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
//            int blockSize = cipher.getBlockSize();
//            byte[] dataBytes = plainText.getBytes();
//            int plaintextLength = dataBytes.length;
//
//            if (plaintextLength % blockSize != 0) {
//                plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
//            }
//
//            byte[] plaintext = new byte[plaintextLength];
//            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
//
//            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
//            IvParameterSpec ivspec = new IvParameterSpec(ConNum.IV.getBytes());  // CBC模式，需要一个向量iv，可增加加密算法的强度
//
//            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
//            byte[] encrypted = cipher.doFinal(plaintext);
////            byte[] encode = new Base64().encode(encrypted);
////            String s = new Base64().encode(encrypted).toString();
//String s = encrypted.toString();
//            return s;
//
//            //return encryptCBC.encode(encrypted).trim(); // BASE64做转码。
////            System.out.println(encrypted.toString());
//
////            return encrypted.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }


//}
