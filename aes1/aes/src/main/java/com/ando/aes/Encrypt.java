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
 * @author Ando
 * @version 1.0
 * @date 2022/6/3
 */

@Component
public class Encrypt {

//    public static void main(String[] args) {
//        System.out.println(encryptCBC("hello", "feilZigbpEUihBoe"));
//    }

    /**
     * 加密
     *
     * @param plainText 明文
     * @param key       密钥
     * @return 密文
     */
    public static String encryptECB(String plainText, String key) {
        // 0. 明文/密钥分组
        String[][][] plainMatrix = Utils.divide4PlainText(plainText);
        String[][] initSecret = Utils.divide4Secret(key);
        StringBuilder sb = new StringBuilder(plainMatrix.length << 4); // 用于拼接密文
        // 1. 构造密钥组
        String[][][] extendSecret = Utils.extendSecret(initSecret);
        for (int i = 0; i < plainMatrix.length; i++) {
            // 2. 轮密钥加
            plainMatrix[i] = Utils.xor4HexArr(plainMatrix[i], extendSecret[0]);
            // 3.10轮重复加密操作
            for (int round = 1; round <= 10; round++) {
                // 字节代换
                Utils.replaceByte(plainMatrix[i], ConNum.S);
                // 行移位
                plainMatrix[i] = Utils.moveRow(plainMatrix[i]);
                // 列混淆(第十轮不进行)
                if (round != 10) {
                    plainMatrix[i] = Utils.fixColumn(plainMatrix[i], ConNum.FIX_COLUMN);
                }
                // 轮密钥加
                plainMatrix[i] = Utils.xor4HexArr(plainMatrix[i], extendSecret[round]);
            }
            // 4.拼接密文
            for (int k = 0; k < 4; k++) {
                for (int j = 0; j < 4; j++) {
                    sb.append(plainMatrix[i][j][k]);
                }
            }
        }
        return sb.toString();
    }


    public static String encryptCBC(String plainText, String key) throws Exception {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKey secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
        IvParameterSpec iv = new IvParameterSpec(ConNum.IV.getBytes("UTF-8"));
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
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
