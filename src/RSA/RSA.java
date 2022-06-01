package RSA;
import java.math.BigInteger;

public class RSA {

	public static String StringToBinary(String string)
	{
		char M[] = string.toCharArray();
		//System.out.println(""+M[0]+M[1]+M[2]+M[3]+"-"+M[4]+M[5]+"-"+M[6]+M[7]+" "+M[8]+M[9]+":"+M[10]+M[11]+":"+M[12]+M[13]);
		int M1[] = new int[M.length];
		String tmp = new String();

		String s ="";  
		for(int i=0;i<M.length;i++)
		{
			if (Character.isDigit(M[i])){
				M1[i] = Integer.parseInt(String.valueOf(M[i]));
			}
			else {
				System.err.println("String含有非数字类型");
			}

			tmp = supplement(4, Integer.toBinaryString(M1[i]));

			s = s + tmp;
		}
		return s;
	}


	public static String supplement(int n,String str){
		if(n>str.length()) {
			int sl=str.length();//string&#x052D;&#xFFFD;&#xFFFD;&#xFFFD;&#xFFFD;
			for(int i=0;i<(n-sl);i++) {
				str="0"+str;
			}
		}
		return str;
	}
	public static String StringToBinary2(String string)
	{
		char M[] = string.toCharArray();
		//System.out.println(""+M[0]+M[1]+M[2]+M[3]+"-"+M[4]+M[5]+"-"+M[6]+M[7]+" "+M[8]+M[9]+":"+M[10]+M[11]+":"+M[12]+M[13]);
		int M1[] = new int[M.length];
		String tmp = new String();

		String s ="";
		for(int i=0;i<M.length;i++)
		{
			if (Character.isDigit(M[i])){
				M1[i] = Integer.parseInt(String.valueOf(M[i]));
			}
			else {
				System.err.println("String里含有非数字的类型");
			}

			tmp = supplement(3, Integer.toBinaryString(M1[i]));

			s = s + tmp;
		}
		return s;
	}
	public static String BinaryToString2(String string)
	{
		int length = string.length();
		String str = string;
		if(length%3 != 0) {
			str = supplement((length+3-length%3), string);
			length = str.length();
		}
		char C[] = str.toCharArray();

		String M[] = new String[length/3];
		for(int i=0;i<M.length;i++){
			M[i] = "";
		}
		//System.out.println(""+M[0]+M[1]+M[2]+M[3]+"-"+M[4]+M[5]+"-"+M[6]+M[7]+" "+M[8]+M[9]+":"+M[10]+M[11]+":"+M[12]+M[13]);
		int M1[] = new int[length/3];

		String s ="";  //&#xFFFD;&#xFFFD;&#xFFFD;&#x0436;&#xFFFD;&#xFFFD;&#xFFFD;&#xFFFD;&#x01B5;&#xFFFD;&#xFFFD;&#x06FC;&#xFFFD;
		for(int i1=0;i1 < length;i1++)
		{
			M[i1/3] = M[i1/3]+C[i1];
			if(i1%3 == 2) {
				M1[i1/3] = Integer.parseInt(M[i1/3],2);
				s = s + M1[i1/3]; //&#xFFFD;&#xFFFD;&#xFFFD;&#xFFFD;string&#xFFFD;&#xFFFD;
			}
		}
		return s;
	}
	public static String BinaryToString(String string)
	{
		int length = string.length();
		String str = string;
		if(length%4 != 0) {
			str = supplement((length+4-length%4), string);
			length = str.length();
		}
		char C[] = str.toCharArray();

		String M[] = new String[length/4];
		for(int i=0;i<M.length;i++){
			M[i] = "";
		}
		//System.out.println(""+M[0]+M[1]+M[2]+M[3]+"-"+M[4]+M[5]+"-"+M[6]+M[7]+" "+M[8]+M[9]+":"+M[10]+M[11]+":"+M[12]+M[13]);
		int M1[] = new int[length/4];

		String s ="";  //&#xFFFD;&#xFFFD;&#xFFFD;&#x0436;&#xFFFD;&#xFFFD;&#xFFFD;&#xFFFD;&#x01B5;&#xFFFD;&#xFFFD;&#x06FC;&#xFFFD;
		for(int i1=0;i1 < length;i1++)
		{
			M[i1/4] = M[i1/4]+C[i1];
			if(i1%4 == 3) {
				M1[i1/4] = Integer.parseInt(M[i1/4],2);

				s = s + M1[i1/4]; //&#xFFFD;&#xFFFD;&#xFFFD;&#xFFFD;string&#xFFFD;&#xFFFD;
			}
		}
		return s;
	}



	public String encrypt(String plainText, String[] pubkey){
		System.out.println("-----RSA开始加密-----");
		BigInteger n = new BigInteger(pubkey[0]) ;
		BigInteger e = new BigInteger(pubkey[1]) ;
//	plainText = StringToBinary2(plainText);//&#xFFFD;&#x023D;&#xFFFD;&#xFFFD;&#xFFFD;&#x022B;&#xFFFD;&#xFFFD;&#x05EA;&#xFFFD;&#xFFFD;&#xFFFD;&#x0276;&#xFFFD;&#xFFFD;&#xFFFD;&#xFFFD;&#xFFFD;
//	plainText = BinaryToString2("1"+plainText);
		//System.out.println(BinaryToString2(plainText));
		plainText = "1"+plainText;
		//把二进制转换成十进制
		plainText = new BigInteger(plainText,2).toString(10);

		BigInteger m = new BigInteger(plainText);
		BigInteger c = m.modPow(e, n);
		//String	C=StringToBinary(c.toString());
		String	C=c.toString(2) ;
		return C.toString() ;
	}


	public String decrypt(String cipherText, String[] selfkey){
		System.out.println("-----RSA开始解密;-----");
		BigInteger n = new BigInteger(selfkey[0]) ;
		BigInteger d = new BigInteger(selfkey[1]) ;
		String c1 = new BigInteger(cipherText,2).toString(10);
		BigInteger c = new BigInteger(c1);
		BigInteger m = c.modPow(d, n);

		String M = m.toString(2);
		String M2 = M.substring(1,M.length());

		return M2.toString() ;
	}







}
