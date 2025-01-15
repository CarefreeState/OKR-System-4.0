package cn.lbcmmszdntnt.common.util.convert;


import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.exception.GlobalServiceException;

public class ShortCodeUtil {

    private static final String CHARSET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_";
    public static final int FETCH_RADIX = 16;
    public static final int MODULES = CHARSET.length();
    public static final int FETCH_SIZE = 4;

    // length 的范围是 [1, 29]
    public static String subCodeByString(String str, int length) {
        str = EncryptUtil.md5(str);
        int strLength = str.length();
        int gap = strLength / length;//取值间隔
        if(gap < 1 || (length - 1) * gap + FETCH_SIZE > strLength) {
            String message = String.format("哈希字符串 %s，无法取出 %d 个 %d 位 %d 进制数", str, length, FETCH_SIZE, FETCH_RADIX);
            throw new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_NOT_VALID);
        }
        StringBuilder subCode = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = Integer.parseInt(str.substring(i * gap, i * gap + FETCH_SIZE), FETCH_RADIX);//提取十六进制数
            subCode.append(CHARSET.charAt(index % MODULES));//对应到Base64字典的某个Base64字符
        }
        return subCode.toString();
    }

    public static void main(String[] args) {
        long teamId = 10;
        System.out.println("\"" + subCodeByString("teamId=" + teamId + "macaku", 6) +"\"");
        System.out.println("\"" + subCodeByString("WX_JWT" + "macaku", 6) +"\""); // r6Vsr0
        System.out.println("\"" + subCodeByString("EMAIL_JWT" + "macaku", 6) +"\""); // Rl0p0r
        System.out.println("\"" + subCodeByString("ACK" + "macaku", 6) +"\""); // Z-1_rf
    }

}
