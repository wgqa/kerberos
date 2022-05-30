//package Client;
//
//import java.io.UnsupportedEncodingException;
//import java.math.BigInteger;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//
//public class hashtest {
//
//
//        public static String getMD5Str(String str) {
//            byte[] digest = null;
//            try {
//                MessageDigest md5 = MessageDigest.getInstance("md5");
//                digest  = md5.digest(str.getBytes("utf-8"));
//            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//            //16是表示转换为16进制数
//            String md5Str = new BigInteger(1, digest).toString(16);
//            return md5Str;
//        }
//
//    public static void main(String[] args) {
//        String sjz= "sjz193191";
//        System.out.println("sjzmd5 "+getMD5Str(sjz));
//        String wgq = "wgq12345";
//        System.out.println("wgqmd5 "+getMD5Str(wgq));
//        String hjf = "hjf193191";
//        System.out.println(getMD5Str(hjf));
//    }
//}
