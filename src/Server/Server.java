package Server;

import DataStruct.Authenticator;
import DataStruct.Packet;
import DataStruct.Ticket;
import Des.DES;
import RSA.RSA;

import java.text.ParseException;

import static DataStruct.Packet.StringToTime;
import static DataStruct.Packet.createTimestamp;
import static TGS.TGS.BinaryToString;
import static TGS.TGS.StringToBinary;

public class Server {

    private final static String[] SELFKEYV = {"1197109859790913087686830134943274236719072137378604521650533072095054080831" , "1119539357176536616075341013316710825069096752350867012830658571383977123009"};//V私钥


    public static String supplement(int n, String str) {
        if (n > str.length()) {
            int sl = str.length();//string原长度
            for (int i = 0; i < (n - sl); i++) {
                str = "0" + str;
            }
        }
        return str;
    }

    public static Packet packAnalyse(String message) throws ParseException {
        System.out.println("-----开始解析包-----");
        DataStruct.Packet p = new DataStruct.Packet();
        DataStruct.Ticket ticket = new Ticket();
        DataStruct.Authenticator auth = new Authenticator();

        //分离出头部
        int headLength = p.getHead().headOutput().length();
        char M[] = message.toCharArray();
        String s[] = new String[headLength+4];//头部和requestID
        System.out.println(s);
        for (int i = 0; i < headLength; i++) {
            s[i] = String.valueOf(M[i]);
        }
        p.setHead(new DataStruct.Head(s[0] + s[1] + s[2] + s[3], s[4] + s[5] + s[6] + s[7], s[8], s[9], s[10],
                s[11], s[12], s[13], s[14], s[15],/* s[16]+s[17]+s[18]+s[19] ,*/  "",
                s[144]+s[145]+s[146]+s[147]+s[148] + s[149] + s[150] + s[151] + s[152] + s[153] + s[154] + s[155] + s[156] + s[157] + s[158] + s[159]));
        for (int n = 16; n < 144; n++) {
            p.getHead().setSecurityCode(p.getHead().getSecurityCode() + s[n]);
        }
        System.out.println(p.getHead());
        String pack = message.replaceFirst(p.getHead().headOutput(), "");
        System.out.println("packet剩余部分"+pack);
        //packet剩余部分应该是 左半部分的ticket和requestID， 加上右半部分的auth
        //剩余部分001000000010001000000101001010000001100100011000010100111110000001101000011001111110110111100101110110010010100001011110101111001111001010111100011100101100010110110100010011000111011100011100001101101010001011000111011011000101111001000111100010101111011010100110100010111101101111111111011101110110111111011000111010000111000000011100111011100110110111111000000101000001001010001101111111111110001100111001011011001010010111001000
        //剩余部分001000000010001000000101001010000001100100011000010100111110000001101000011001111110110111100101110110010010100001011110101111001111001010111100011100101100010110110100010011000111011100011100001101101010001011000111011011000101111001000111100010101111011010100110100010111101101111111111011101110110111111011000111010000111000000011100111011100110110111111000000101000001001010001101111111111110001100111001011011001010010111001000
        //验证消息验证码
        //  if (DataStruct.Head.MD5(pack).equals(p.getHead().getSecurityCode())) {

        //分离出包内容和Ticket和Auth
        String messageT = "", messageA = "";
        int tic = Integer.parseInt(p.getHead().getExpend(),2);//加密后tic的长度
        System.out.println("*****ticket v长度"+tic);
        System.out.println("tic的大小"+tic+"packet剩余部分的长度"+pack.length());
        for (int i = headLength; i < message.length(); i++) {
            if (i < headLength + 56)
                p.setTimeStamp(p.getTimeStamp() + M[i]);
            else if (i < headLength + 56 + tic) {
                messageT = messageT + M[i];
            } else {
                messageA = messageA + M[i];
            }

        }
        System.out.println("未解密的ticket v是"+messageT);
        //未解密的110100111010011001111100011010000101111000010011100111101001001100111011001111001100011100001010000100010101001110010000000000101111011000101000111111000101010110001111100110101110110001111111000111111001110001100000101100110000111000001111101001  10110100010000010110100011001010101111000000101001110100111101101100000011011101110100110110000001101010111011110000111010011000
        //加密过的110100111010011001111100011010000101111000010011100111101001001100111011001111001100011100001010000100010101001110010000000000101111011000101000111111000101010110001111100110101110110001111111000111111001110001100000101100110000111000001111101001
        //Ticket解密 用tgs私钥
        RSA rsa = new RSA();
        System.out.println("密文:" + messageT);
        messageT = rsa.decrypt(messageT, SELFKEYV);
        System.out.println("明文:" + messageT);
        System.out.println("messageT="+messageT);
        char T[] = messageT.toCharArray();
        //解Ticket
        for (int i = 0; i < messageT.length(); i++) {
            if (i < 64)
                ticket.setSessionKey(ticket.getSessionKey() + T[i]);
            else if (i < 64+4)
                ticket.setID(ticket.getID() + T[i]);
            else if (i < 64+4+32)
                ticket.setIP(ticket.getIP() + T[i]);
            else if (i < 64+4+32+4)
                ticket.setRequestID(ticket.getRequestID() + T[i]);
            else if (i < 64+4+32+4 + 56)
                ticket.setTimeStamp(ticket.getTimeStamp() + T[i]);
            else if (i < 64+4+32+4 + 56 + 56)
                ticket.setLifeTime(ticket.getLifeTime() + T[i]);
//            else {
//                System.err.println("分析发现Server收到的package qwe长度有误，请检查！！");
//                System.exit(0);
//            }
        }
        //解密完ticket之后通过timestamp判断
        /***********************判断client 发来的包的时间戳**************************************/
        String time1 = BinaryToString(ticket.getTimeStamp());//时间戳
//            String time2 = BinaryToString(packetFromAS.getLifeTime());//生命周期
        long time11 = StringToTime(time1);
        long time22 = time11+2*60000;
        String timeNow = createTimestamp();
        long time33 = StringToTime(timeNow);
        if(time11<time33 && time22 >time33){
            System.out.println("时间戳合法，继续认证");
        }
        else{
            System.out.println("时间戳非法，停止认证");
        }

        System.out.println("message A是？"+messageA);
        System.out.println("message a length"+messageA.length());
        //Auth解密，用Ticket中的sessionkey解密
        messageA = DES.decrypt(messageA, ticket.getSessionKey());
        char A[] = messageA.toCharArray();
        for (int i = 0; i < messageA.length(); i++) {
            if (i < 4)
                auth.setClientID(auth.getClientID() + A[i]);
            else if (i < 36)
                auth.setClientIP(auth.getClientIP() + A[i]);
            else if (i < 36 + 56)
                auth.setTimeStamp(auth.getTimeStamp() + A[i]);
//            else {
//                System.err.println("分析发现Server收到 asdf的package长度有误，请检查！！");
//                System.exit(0);
//            }
        }
//            ticket.setTimeStamp(BinaryToString(ticket.getTimeStamp()));
//            ticket.setLifeTime(BinaryToString(ticket.getLifeTime()));
//            auth.setTimeStamp(BinaryToString(auth.getTimeStamp()));

        if(auth.getClientID().equals(ticket.getID()) ){
            System.out.println("用户身份信息正确，继续认证");
        }
        else{
            System.out.println("用户信息错误，停止认证！");
        }
        p.setTicket(ticket);
        p.setAuth(auth);

        System.out.println("分析的包：" + p);
        return p;
    } // else {
    //从client获得到    Ticket=Ticket{sessionKey='1110011001011100100101100110011001000011111101101101011000111100', ID='0101', IP='00111100011101011000000000000101', requestID='1011', timeStamp='11001111001111110111000110010011101101100110101100110010', lifeTime='00000011001011000110000100110010011001001100000111000100'}
    //tgs发送给client的 Ticket=Ticket{sessionKey='0011100001101110110010111000111101100110100111011110010111001110', ID='1111', IP='11000000101010000010101110101110', requestID='0011', timeStamp='00100000001000100000010100101000000101100100100100000111', lifeTime='00100000001000100000010100101000000101100100100100000111'}
    public static Packet packetToClient(String expandCode){
        DataStruct.Packet p = new DataStruct.Packet();;


        p.setTimeStamp(StringToBinary(createTimestamp()));
        //p.setLifeTime(Packet.createLifeTime(2));

       /* if(Number > 16)
        {
            Number = -1;
        }
        Number++;
        String number = supplement(4, Integer.toBinaryString(Number));*/
        String securityCode = DataStruct.Head.zero(128);
        //

        expandCode = supplement(16,expandCode);
        System.out.println(expandCode);
        //
        DataStruct.Head h= new DataStruct.Head("ssss","ssss","0","0","0","0","1","0","0","0",securityCode,expandCode);
        p.setHead(h);

        return p;
    }
}