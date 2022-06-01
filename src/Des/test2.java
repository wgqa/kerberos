package Des;

public class test2 {
    //汉字转换成二进制字符串
    public static String strToBinStr(String str) {
        char[] chars=str.toCharArray();
        StringBuffer result = new StringBuffer();
        for(int i=0; i<chars.length; i++) {
            result.append(Integer.toBinaryString(chars[i]));
            result.append(" ");
        }
        return result.toString();
    }
    //二进制字符串转换成汉字
    public static String BinStrTostr(String binary) {
        String[] tempStr=binary.split(" ");
        char[] tempChar=new char[tempStr.length];
        for(int i=0;i<tempStr.length;i++) {
            tempChar[i]=BinstrToChar(tempStr[i]);
        }
        return String.valueOf(tempChar);
    }

    //将二进制字符串转换成int数组
    public static int[] BinstrToIntArray(String binStr) {
        char[] temp=binStr.toCharArray();
        int[] result=new int[temp.length];
        for(int i=0;i<temp.length;i++) {
            result[i]=temp[i]-48;
        }
        return result;
    }
    //将二进制转换成字符
    public static char BinstrToChar(String binStr) {
        int[] temp = BinstrToIntArray(binStr);
        int sum = 0;
        for (int i = 0; i < temp.length; i++) {
            sum += temp[temp.length - 1 - i] << i;
        }
        return (char) sum;
    }



    public static void main(String[] args) {
        String sKey = "0011000100110010001100110011010000110101001101100011011100111000";
        String chinese="书";
        String temp = strToBinStr(chinese);

        System.out.println(temp);
        System.out.println(BinStrTostr(temp));

        temp= Des.DES.encrypt(temp,sKey);
        System.out.println(temp);
        temp= Des.DES.decrypt(temp,sKey);
        System.out.println(temp);
    }

}
