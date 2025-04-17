////package com.ando.aes;
////
////import org.junit.jupiter.api.Test;
////import org.springframework.boot.test.context.SpringBootTest;
////
////import java.io.FileInputStream;
////import java.io.FileOutputStream;
////import java.io.IOException;
////
////@SpringBootTest
////class AesApplicationTests {
////
////    @Test
////    void contextLoads() {
////    }
////   @Test
////    void encryptFile(){
////        String srcFilePath = "d:\\hello.text";
////        String destFilePath = "d:\\hello1.txt";
////        //字节数组
////       byte[] buf = new byte[8];
////       int readLen = 0;
////       FileInputStream fileInputStream = null;
////       FileOutputStream fileOutputStream = null;
//////       StringBuffer cipher = new StringBuffer();
////String plainText = "";
////       try {
////           //创建FileInputStream对象，用于读取文件
////           fileInputStream = new FileInputStream(srcFilePath);
////           fileOutputStream = new FileOutputStream(destFilePath);
////
////           while ((readLen = fileInputStream.read(buf)) != -1){
////
////              plainText =  plainText + new String(buf, 0, readLen);
////
//////               fileOutputStream.write(buf, 0, readLen);
////
////           }
////           System.out.println(plainText);
////
////           String cipher = Encrypt.encrypt(plainText, "feilZigbpEUihBoe");
////           System.out.println(cipher);
////          fileOutputStream.write(cipher.getBytes());
////
////       } catch (IOException e) {
////           e.printStackTrace();
////       }finally {
////
////           try {
////               //关闭输入刘和输出流，释放资源
////               if (fileInputStream != null){
////                   fileInputStream.close();
////               }
////               if (fileOutputStream != null){
////                   fileOutputStream.close();
////               }
////           } catch (IOException e) {
////               e.printStackTrace();
////           }
////
////       }
////
////   }
////
////    @Test
////    void decryptFile(){
////        String srcFilePath = "d:\\hello1.txt";
////        String destFilePath = "d:\\hello2.txt";
////        //字节数组
////        byte[] buf = new byte[1024];
////        int readLen = 0;
////        FileInputStream fileInputStream = null;
////        FileOutputStream fileOutputStream = null;
//////       StringBuffer cipher = new StringBuffer();
////        String cipher = "";
////        try {
////            //创建FileInputStream对象，用于读取文件
////            fileInputStream = new FileInputStream(srcFilePath);
////            fileOutputStream = new FileOutputStream(destFilePath);
////
////            while ((readLen = fileInputStream.read(buf)) != -1){
////
////                cipher =  cipher + new String(buf, 0, readLen);
////
//////               fileOutputStream.write(buf, 0, readLen);
////
////            }
////            System.out.println(cipher);
////
////            String plainText = Decrypt.decrypt(cipher, "feilZigbpEUihBoe");
////            System.out.println(plainText);
////            fileOutputStream.write(plainText.getBytes());
////
////        } catch (IOException e) {
////            e.printStackTrace();
////        }finally {
////
////            try {
////                //关闭输入刘和输出流，释放资源
////                if (fileInputStream != null){
////                    fileInputStream.close();
////                }
////                if (fileOutputStream != null){
////                    fileOutputStream.close();
////                }
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////
////        }
////
////    }
////}
//package com.zhongzhi.utils;
//import javax.crypto.Cipher;
//import javax.crypto.spec.IvParameterSpec;
//import javax.crypto.spec.SecretKeySpec;
//import org.apache.commons.codec.binary.Base64;
///**
// * @Classname ZzSecurityHelper
// * @Description TODO
// * @Date 2019/6/24 16:50
// * @Created by whd
// */
//public class ZzSecurityHelper {
//    /*
//     * 加密用的Key 可以用26个字母和数字组成 使用AES-128-CBC加密模式，key需要为16位。
//     */
//    private static final String key="hj7x89H$yuBI0456";
//    private static final String iv ="NIfb&95GUY86Gfgh";
//    /**
//     * @author miracle.qu
//     * @Description AES算法加密明文
//     * @param data 明文
//     * @param key 密钥，长度16
//     * @param iv 偏移量，长度16
//     * @return 密文
//     */
//    public static String encryptAES(String data) throws Exception {
//        try {
//            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
//            int blockSize = cipher.getBlockSize();
//            byte[] dataBytes = data.getBytes();
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
//            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());  // CBC模式，需要一个向量iv，可增加加密算法的强度
//
//            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
//            byte[] encrypted = cipher.doFinal(plaintext);
//
//            return ZzSecurityHelper.encode(encrypted).trim(); // BASE64做转码。
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    /**
//     * @author miracle.qu
//     * @Description AES算法解密密文
//     * @param data 密文
//     * @param key 密钥，长度16
//     * @param iv 偏移量，长度16
//     * @return 明文
//     */
//    public static String decryptAES(String data) throws Exception {
//        try
//        {
//            byte[] encrypted1 = ZzSecurityHelper.decode(data);//先用base64解密
//
//            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
//            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
//            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
//
//            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
//
//            byte[] original = cipher.doFinal(encrypted1);
//            String originalString = new String(original);
//            return originalString.trim();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//    /**
//     * 编码
//     * @param byteArray
//     * @return
//     */
//    public static String encode(byte[] byteArray) {
//        return new String(new Base64().encode(byteArray));
//    }
//
//    /**
//     * 解码
//     * @param base64EncodedString
//     * @return
//     */
//    public static byte[] decode(String base64EncodedString) {
//        return new Base64().decode(base64EncodedString);
//    }
//}
