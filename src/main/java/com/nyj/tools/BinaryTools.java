package com.nyj.tools;

/**
 * @author nyj
 * @version 1.0
 * @date 2023/4/20 10:19
 */
public class BinaryTools {
    /*
    * 将整型数组转化成十六进制的字符串
    *
    * */
    //
    public static String IntegersArrayToHexString(Integer[] bytes){
        String hexStr = "0123456789ABCDEF";
        String result = "";
        String hex = "";
        for (int b: bytes) {
            hex = String.valueOf(hexStr.charAt((b & 0xF0) >> 4));  //高四位对应的十六进制数
            hex += String.valueOf(hexStr.charAt(b & 0x0F)); //低四位对应的十六进制数
            result += hex + " ";  //高四位与低四位拼接，得到一个两位的十六进制数
        }
        return result;
    }
    /*
    * 16进制字符串转化为字节数组
    *
    * @param hexString 16进制字符串
    * @return byte[] 字节数组
    * */
    public static byte[] hexStringToByteArray(String hexString){
        hexString = hexString.replaceAll(" ","");
        int len = hexString.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0;i < len;i+=2){
            bytes[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) +
                    Character.digit(hexString.charAt(i+1), 16));

        }
        return bytes;
    }
}
