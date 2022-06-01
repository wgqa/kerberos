package Des;

public class test {
    public static void main(String[] args) {
        String sKey = "0011000100110010001100110011010000110101001101100011011100111000";
        String s="101";
        s= Des.DES.encrypt(s,sKey);
        System.out.println(s);
        s= Des.DES.decrypt(s,sKey);
        System.out.println(s);
    }
}
