package com.ando.aes;

import com.ando.aes.ConNum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Ando
 * @version 1.0
 * @date 2022/6/3
 */

@Component
public class Utils {


//    @Autowired
//    private ConNum conNum;

    /**
     * 打印4 * 4矩阵
     *
     * @param arr
     */
    public static void print(String[][] arr) {
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                System.out.print(arr[row][col] + " ");
            }
            System.out.println();
        }
        System.out.println("==============");
    }

    /**
     * 明文
     * 1. 将明文的普通字符串转化成对应的多个字节数组
     * 2. 每个字节数组都不能超过16字节
     * 3. 将字节数组按列排成4 * 4矩阵
     * 4. 字节不足时用00充当
     */
    public static String[][][] divide4PlainText(String plainText) {
        byte[] bytes = plainText.getBytes();
        int len = (bytes.length & 15) == 0 ? (bytes.length >>> 4) : (bytes.length >>> 4) + 1;
        String[][][] ret = new String[len][4][4];
        for (int k = 0; k < len; k++) {
            for (int i = 0; i < 4; i++) { //此为列
                for (int j = 0; j < 4; j++) { //此为行
                    int index = (i << 2) + j + (k << 4);
                    if (index < bytes.length) {
                        String hex = Integer.toHexString(bytes[index]);
                        if (hex.length() == 1) {
                            hex = "0" + hex;
                        }
                        ret[k][j][i] = hex;
                    } else {
                        ret[k][j][i] = "00";
                    }
                }
            }
        }
        return ret;
    }

    /**
     * 密钥分组
     * 1. 密钥的普通字符串转化成对应的字节数组
     * 2. 不能超过16字节
     * 3. 将字节数组按列排成4 * 4矩阵
     * 4. 字节不足时用00充当
     */
    public static String genEncodeRule() {
        StringBuilder chars = new StringBuilder("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            int num = Integer.parseInt(String.valueOf(Math.round(Math.floor(Math.random() * chars.length()))));
            result.append(chars.charAt(num));
        }
        return result.toString();
    }

    public static String[][] divide4Secret(String secret) {
//		secret = genEncodeRule();
        String[][] ret = new String[4][4];
        byte[] bytes = secret.getBytes();

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int index = (i << 2) + j;
                String hex = Integer.toHexString(bytes[index]);
                if (hex.length() == 1) {
                    hex = "0" + hex;
                }
                ret[j][i] = hex;

            }
        }

        return ret;
    }


    /*
     *
     * 将十六进制字符串分成多组4 * 4
     * 如: 0123456789abcdeffedcba9876543210  (16字节)
     * 转成:
             01 89 fe 76
            23 ab dc 54
            45 cd ba 32
            67 ef 98 10
     * 转入的是密文(长度一定是32的倍数)
     */
    public static String[][][] divide4Hex(String hexString) {
        int len = hexString.length() >>> 5;
        String[][][] ret = new String[len][4][4];
        int index = 0;
        for (int k = 0; k < len; k++) {
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    ret[k][j][i] = hexString.substring(index, index + 2);
                    index += 2;
                }
            }
        }
        return ret;
    }

    /**
     * 密钥扩展
     */
    public static String[][][] extendSecret(String[][] initSecret) {

        String[][] w = new String[44][4];
        // 1. w0-w3等于初始密钥
        for (int i = 0; i < initSecret.length; i++) {
            for (int j = 0; j < 4; j++) {
                w[i][j] = initSecret[j][i];
            }
        }
        // 2.计算w4-w43
        for (int i = 4; i < 44; i++) {
            String[] temp = w[i - 1];
            if ((i & 3) == 0) { // 如果wi下标i可以被4整除, 则w[i] = w[i - 4] xor z[i >>> 2]
                temp = g(w[i - 1], (i >>> 2) - 1);
            } // 否则, w[i] = w[i - 1] xor w[i - 4]
            for (int j = 0; j < 4; j++) {
                w[i][j] = xor4Hex(w[i - 4][j], temp[j]);
            }
        }

        // 3.计算构造密钥组返回
        String[][][] secretGroup = new String[11][4][4];
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    secretGroup[i][j][k] = w[(i << 2) + k][j];
                }
            }
        }
        return secretGroup;
    }


    /**
     * 辅助函数g
     *
     * @param w     当前轮最后一列
     * @param round 当前轮数
     * @return
     */
    public static String[] g(String[] w, int round) {
        String[] z = new String[4];
        for (int i = 0; i < 4; i++) {
            // 1.字循环
            // 2.字节代换
            z[i] = searchS(w[i + 1 & 3], ConNum.S);
            // 3.xor轮常数
            if (i == 0) {
                z[i] = xor4Hex(z[i], ConNum.RC[round]);  //只需异或第一个字节即可
            }
        }
        return z;
    }

    /**
     * 字节代换
     */
    public static void replaceByte(String[][] arr, String[][] s) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                arr[i][j] = searchS(arr[i][j], s);
            }
        }
    }

    /**
     * 行移位
     */
    public static String[][] moveRow(String[][] arr) {
        String[][] newArr = new String[4][4];
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                newArr[row][col] = arr[row][(col + row) & 3];
            }
        }
        return newArr;
    }

    /**
     * 逆行移位
     */
    public static String[][] moveRowInverse(String[][] arr) {
        String[][] newArr = new String[4][4];
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                newArr[row][col] = arr[row][(4 - row + col) & 3];
            }
        }
        return newArr;
    }

    /**
     * 列混淆
     */
    public static String[][] fixColumn(String[][] source, String[][] fix) {
        String[][] result = new String[4][4];
        for (int i = 0; i < 4; i++) {    // 控制行
            for (int j = 0; j < 4; j++) {    // 控制行使用次数
                String ret = "00000000";
                for (int k = 0; k < 4; k++) {    // 控制列
                    String binMultiply = binMultiply(fix[i][k], source[k][j]);
                    ret = xor(ret, binMultiply);
                }
                // 将结果转为16进制
                String hex = binToHex(ret);
                result[i][j] = hex;
            }
        }
        return result;
    }

    /**
     * S盒替换
     *
     * @return
     */
    public static String searchS(String hex, String[][] s) {
        /*
         * 1. 将传入的2位16进制字符串前后位分开, 转成10进制
         * 2. 第一位表示行, 第2位表示列, 去查询S盒, 返回2位16进制字符串
         */
        int row = Integer.valueOf(hex.substring(0, 1), 16);
        int col = Integer.valueOf(hex.substring(1, 2), 16);
        return s[row][col];
    }

    /**
     * 实现两个4 * 4矩阵对应元素的xor操作
     *
     * @return
     */
    public static String[][] xor4HexArr(String[][] h1, String[][] h2) {
        String[][] ret = new String[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                ret[i][j] = xor4Hex(h1[i][j], h2[i][j]);
            }
        }
        return ret;
    }

    /**
     * 将两个16进制字符串转成2进制字符串后进行乘法运算, 结果符合GF(2^8)
     */
    public static String binMultiply(String hexStr1, String hexStr2) {
        // 1. 将16进制字串符转成二进制字符串, 不足8位则在前面补0
        String binStr1 = hexTobin(hexStr1);
        String binStr2 = hexTobin(hexStr2);

        // 2. 定义返回结果result, 遍历binStr1中字符, 遇到字符'1'时, 记录其索引index,
        // 调用binMultiply(binStr2, 7 - index), 得到ret, 再与result进行xor运算
        String result = "00000000";
        for (int i = 0; i < binStr1.length(); i++) {
            if (binStr1.charAt(i) == '1') {
                String ret = binMultiply(binStr2, 7 - i);
                result = xor(result, ret);
            }
        }
        return result;
    }

    /**
     * 计算x的n次方 * f(x)
     *
     * @param binStr f(x)二进制字符串
     * @param num    x的指数n
     */
    public static String binMultiply(String binStr, int num) {
		/*
		 	思路:
		 		(1)循环执行num次以下的操作
		 		(2)是否flag判断最左边位是否为1
		 		(3)将binStr中二进制数左移一位, 右边补0
		 		(4)如果flag为true, 将(3)所得结果与00011011进行xor操作
		 */
        String ret = new String(binStr);
        for (int i = 0; i < num; i++) {
            boolean flag = ret.charAt(0) == '1';
            ret = ret.substring(1, ret.length()) + "0";
            if (flag) {
                ret = xor(ret, ConNum.XOR_STR);
            }
        }
        return ret;
    }

    /**
     * 两个十六进制字符串求异或
     */
    public static String xor4Hex(String h1, String h2) {
        return binToHex(xor(hexTobin(h1), hexTobin(h2)));
    }

    /**
     * 两个二进制字符串求异或
     */
    public static String xor(String s1, String s2) {
        if (s1 == null || s2 == null || s1.length() != s2.length())
            throw new RuntimeException("字符串长度不相等");
        StringBuilder sb = new StringBuilder(s1.length());
        for (int i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) == s2.charAt(i)) {
                sb.append("0");
            } else {
                sb.append("1");
            }
        }
        return sb.toString();
    }

    /**
     * 8位二进制转2位十六进制
     *
     * @return
     */
    public static String binToHex(String bin) {
        String hex = Integer.toHexString(Integer.valueOf(bin, 2));
        if (hex.length() == 1) {
            hex = "0" + hex;
        }
        return hex;
    }

    /**
     * 2位十六进制转8位二进制
     *
     * @param hex
     * @return
     */
    public static String hexTobin(String hex) {
        // 特殊处理hex长度超过2的情况，只保留最后两位
        if (hex.length() > 2) {
            hex = hex.substring(hex.length() - 2);
        }
        
        // 使用Long替代Integer以处理更大的数值
        String bin = "";
        try {
            Long value = Long.valueOf(hex, 16);
            bin = Long.toBinaryString(value);
        } catch (NumberFormatException e) {
            // 如果解析失败，尝试一个字符一个字符地处理
            StringBuilder binBuilder = new StringBuilder();
            for (char c : hex.toCharArray()) {
                String digitBin = Integer.toBinaryString(Character.digit(c, 16));
                // 确保每个十六进制字符产生4位二进制
                while (digitBin.length() < 4) {
                    digitBin = "0" + digitBin;
                }
                binBuilder.append(digitBin);
            }
            bin = binBuilder.toString();
        }
        
        // 确保结果是8位
        int len1 = 8 - bin.length();
        if (len1 > 0) {
            bin = ConNum.ZEROS.substring(0, len1) + bin;
        } else if (len1 < 0) {
            // 如果超过8位，只保留最后8位
            bin = bin.substring(bin.length() - 8);
        }
        return bin;
    }
}
