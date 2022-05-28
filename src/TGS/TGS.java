package TGS;

//import TGS.UI.AP;

import DataStruct.Authenticator;
import DataStruct.Head;
import DataStruct.Packet;
import DataStruct.Ticket;
import Des.DES;
import RSA.RSA;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.ParseException;

import static DataStruct.Packet.StringToTime;
import static DataStruct.Packet.createTimestamp;

public class TGS {


   // private static int Number = -1; //包的初始编号
    //private final int MAXNUMBER = 0; //包的最大编号
    private final static String TGSID = "1110";
    private final static String[] SELFKEY = {"865703290362069039664574527025207637385565054952203601297173618790874525723", "259555164968416688158625327465001826979021157149123374785762616336430795281"}; //TGS私钥
    private final static String[] KEYV = {"1197109859790913087686830134943274236719072137378604521650533072095054080831", "3889"}; //V公钥


    /**
     * 分析包
     * 根据头部，将接受到的包拆解开，各个字段存到 Package 对象中
     *
     * @param message
     */

    public DataStruct.Packet packAnalyse(String message) throws ParseException {
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


        //验证消息验证码
      //  if (DataStruct.Head.MD5(pack).equals(p.getHead().getSecurityCode())) {

            //分离出包内容和Ticket和Auth
            String messageT = "", messageA = "";
            int tic = Integer.parseInt(p.getHead().getExpend(),2);//加密后tic的长度
        System.out.println("tic的大小"+tic+"packet剩余部分的长度"+pack.length());
            for (int i = headLength; i < message.length(); i++) {
                if (i < headLength + 4)
                    p.setRequestID(p.getRequestID() + M[i]);
                else if (i < headLength + 4 + tic) {
                    messageT = messageT + M[i];
                } else {
                    messageA = messageA + M[i];
                }

            }
        System.out.println("message a"+ messageA);


            //Ticket解密 用tgs私钥
            RSA rsa = new RSA();
            System.out.println("密文:" + messageT);
            messageT = rsa.decrypt(messageT, SELFKEY);
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
//                else {
//                    System.err.println("999分析发现TGS收到的package长度有误，请检查！！");
//                    System.exit(0);
//                }
            }
        String time1 = BinaryToString(ticket.getTimeStamp());//时间戳
        String time2 = BinaryToString(ticket.getLifeTime());//生命周期
        long time11 = StringToTime(time1);
        long time22 = StringToTime(time2);
        String timeNow = createTimestamp();
        long time33 = StringToTime(timeNow);
        if(time11<time22 && time22 >time33){
            System.out.println("时间戳合法，继续认证");
        }
        else{
            System.out.println("时间戳非法，停止认证");
        }
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
//                else {
//                    System.err.println("888分析发现TGS收到的package长度有误，请检查！！");
//                    System.exit(0);
//                }
            }
//            ticket.setTimeStamp(BinaryToString(ticket.getTimeStamp()));
//            ticket.setLifeTime(BinaryToString(ticket.getLifeTime()));
//            auth.setTimeStamp(BinaryToString(auth.getTimeStamp()));
            p.setTicket(ticket);
            p.setAuth(auth);
        System.out.println(auth.getClientID());
        System.out.println(ticket.getID());
     //   System.out.println(auth.getClientIP());
      //  System.out.println(ticket.getIP());
            if(auth.getClientID().equals(ticket.getID()) ){
                System.out.println("用户身份信息正确，继续认证");
            }
            else{
                System.out.println("用户信息错误，停止认证！");
            }

            System.out.println("分析的包：" + p);
                return p;
        } // else {
          //  System.err.println("TGS收到的包有误");
          //       return null;
      //  }
  //  }


    /**
     * 二进制转十进制
     *
     * @param string
     * @return
     */
    public static String BinaryToString(String string) {
        int length = string.length();
        char C[] = string.toCharArray();
        String M[] = new String[length / 4];
        for (int i = 0; i < M.length; i++) {
            M[i] = "";
        }
        //System.out.println(""+M[0]+M[1]+M[2]+M[3]+"-"+M[4]+M[5]+"-"+M[6]+M[7]+" "+M[8]+M[9]+":"+M[10]+M[11]+":"+M[12]+M[13]);
        int M1[] = new int[length / 4];

        String s = "";  //进行二进制的累加
        for (int i1 = 0; i1 < length; i1++) {
            M[i1 / 4] = M[i1 / 4] + C[i1];
            if (i1 % 4 == 3) {
                M1[i1 / 4] = Integer.parseInt(M[i1 / 4], 2);

                s = s + M1[i1 / 4]; //加入string中
            }
        }
        return s;
    }

    /*
     * 将string字符串按位转二进制编码的string字符串(数字转)
     */
    public static String StringToBinary(String string)
    {
        char M[] = string.toCharArray();
        //System.out.println(""+M[0]+M[1]+M[2]+M[3]+"-"+M[4]+M[5]+"-"+M[6]+M[7]+" "+M[8]+M[9]+":"+M[10]+M[11]+":"+M[12]+M[13]);
        int M1[] = new int[M.length];
        String tmp = new String();

        String s ="";  //进行二进制的累加
        for(int i=0;i<M.length;i++)
        {
            if (Character.isDigit(M[i])){  // 判断是否是数字
                M1[i] = Integer.parseInt(String.valueOf(M[i]));
            }
            else {
                System.err.println("String转Binary出错，并不是数字");
            }

            tmp = supplement(4, Integer.toBinaryString(M1[i]));
            //每一位都转成了二进制
            s = s + tmp; //加入string中
        }
        return s;
    }


    /**
     * 把str补齐到n位，高位写0
     *
     * @param n
     * @param str 要补齐的字符串
     * @return
     */
    public static String supplement(int n, String str) {
        if (n > str.length()) {
            int sl = str.length();//string原长度
            for (int i = 0; i < (n - sl); i++) {
                str = "0" + str;
            }
        }
        return str;
    }

    /**
     * 将 Package 类型转化为二进制流数据
     * 依次判断每一属性，进行拼接
     * @param p 数据包
     * @return 二进制字符串
     */
//    String packageToBiarny(DataStruct.Packet p , String k)
//    {
//        RSA.rsa rsa = new RSA.rsa();
//        DataStruct.Ticket ticket = p.getTicket();
//        String s = new String();
//        String send = p.toString();
//        String lt = p.getLifeTime();
//        String ts = p.getTimeStamp();
//
//        System.out.println("打包："+p);
//
//        if(p.getHead().getExistTS() == "1") {
//            s = StringToBinary(p.getTimeStamp());
//            p.setTimeStamp(s);
//        }
//
//        if(p.getHead().getExistLifeTime() == "1") {
//            s = StringToBinary(p.getLifeTime());
//            p.setLifeTime(s);
//        }
//        //把t转为二进制
//        if(p.getHead().getExistTicket().equals("1")) {
//            DataStruct.Ticket t = p.getTicket();
//            t.setTimeStamp(StringToBinary(p.getTicket().getTimeStamp()));
//            t.setLifeTime(StringToBinary(p.getTicket().getLifeTime()));
//            //给Ticket加密
//            String cipher = rsa.encrypt(t.ticketOutput(), KEYV);
//            t = new Ticket("", "", "", "", "", "");
//
//            char M[] = cipher.toCharArray();
//
//            //加密后放入t中
//            for(int i = 0;i<cipher.length();i++)
//            {
//                if(i<64) {
//                    t.setSessionKey(t.getSessionKey()+M[i]);
//                }
//                else if(i<68) {
//                    t.setID(t.getID()+M[i]);
//                }
//                else if(i<68+32){
//                    t.setIP(t.getIP()+M[i]);
//                }
//                else if(i<68+36) {
//                    t.setRequestID(t.getRequestID()+M[i]);
//                }
//                else if(i<68+36+56) {
//                    t.setTimeStamp(t.getTimeStamp()+M[i]);
//                }
//                else {
//                    t.setLifeTime(t.getLifeTime()+M[i]);
//                }
//            }
//            p.setTicket(t);
//        }
//        String tic = Integer.toString((p.getTicket().ticketOutput().length())); //tickettgs加密后的长度
//        tic = supplement(16, StringToBinary(tic));
//        p.getHead().setExpend(tic);
//
//        //加密 用Kc,tgs
//        String c = DES.encrypt(p.packageOutput(), k);//加密后的包
//
//        //生成消息认证码
//        String sc = DataStruct.Head.MD5(c);
//        p.getHead().setSecurityCode(sc);
//
//        send = p.getHead().headOutput()+c;
//
//        p.setTimeStamp(ts);
//        p.setLifeTime(lt);
//        p.setTicket(ticket);
//        return send;
//    }



    /**
     * 打包数据
     * 形成 Kc,v|| IDV|| TS4|| Ticketv 包
     * 形成的包用  KeyCtgs加密
     * 加密的密文以字符串形式返回
     * @param IDv v的ID
     * @param Ticketv 加密后的v票据
     * @return
     */
    public DataStruct.Packet packData(String clientID,String IDv,Ticket Ticketv){
        DataStruct.Packet p = new DataStruct.Packet();;

        p.setSessionKey(Ticketv.getSessionKey());
        p.setTimeStamp(StringToBinary(createTimestamp()));
        //p.setLifeTime(Packet.createLifeTime(2));
        p.setTicket(Ticketv);

       /* if(Number > 16)
        {
            Number = -1;
        }
        Number++;
        String number = supplement(4, Integer.toBinaryString(Number));*/
        String securityCode = DataStruct.Head.zero(128);
        //加密后的ticket的长度
        RSA rsa = new RSA();
        String ticketOutPutEncrypted = rsa.encrypt(Ticketv.ticketOutput(),KEYV);
        int ticketLength=ticketOutPutEncrypted.length();

        System.out.println(ticketLength);
        String ticketLengthBinary = Integer.toBinaryString(ticketLength);
        System.out.println("发送给client的ticket v的长度"+ticketLengthBinary+"   "+ticketLength);
        String ticketLengthSuppliment = supplement(16,ticketLengthBinary);
        System.out.println(ticketLengthSuppliment);
        //
        DataStruct.Head h= new DataStruct.Head(clientID,TGSID,"0","1","0","0","1","0","1","0",securityCode,ticketLengthSuppliment);
        p.setHead(h);

        return p;
    }

    /**
     * 生成会话密钥 Key(c,v)
     * 随机生成会话密钥 Kcv
     * @return 字符串形式密钥
     */
    public String generateKeyCV(){
        String skey = new String();
        for(int i= 0 ;i < 64;i++)
        {
            int x = (int)(Math.random()*2);
            if(x == 2) {
                x = (int)(Math.random()*2);
            }
            skey = skey + Integer.toBinaryString(x);
        }
        return skey;
    }

    /**
     * 生成Ticket
     * 按 Kc,v||IDC||ADC|| IDv||TS4||Lifetime4 格式封装 Ticket
     * 用 Kv 加密
     * 以字符串形式返回
     * @param p 解析的包中的内容
     * @return 返回加密后的Ticket
     */
    public DataStruct.Ticket generateTicketV(DataStruct.Packet p, InetAddress inetAddress){
        System.out.println("-----正在生成Ticket-----");
        String lifeTime = StringToBinary(DataStruct.Packet.createLifeTime(2));
        String timeStamp = StringToBinary(createTimestamp());
        String clientIP = ipToBinary(inetAddress);
        DataStruct.Ticket t = new Ticket(generateKeyCV(), p.getHead().getSourceID(), clientIP,
                p.getRequestID(),timeStamp , lifeTime);
        System.out.println("生成的Ticket："+t);

        return t;
    }


    /**
     * ip转binary
     * @param ip
     * @return
     */
    public static String ipToBinary(InetAddress ip) {
        byte[] b = ip.getAddress();
        long l = b[0] << 24L & 0xff000000L |
                b[1] << 16L & 0xff0000L |
                b[2] << 8L & 0xff00L |
                b[3] << 0L & 0xffL;
        return Integer.toBinaryString((int)l);
    }


    /**
     * 发送消息
     * 用 socket 输出流进行发送
     * @param socket 套接字，对应的 socket 对象
     * @param message 要发送的信息
     * @return 发送成功返回 true
     * @throws IOException
     */
    public static boolean send(Socket socket, String message) throws IOException{
        OutputStream os=null;
        try {
            os = socket.getOutputStream();
            os.write(message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            os.flush();
            socket.shutdownOutput();
        }
        return true;
    }

    /*
     * 接收消息
     * @param socket 传入对应的 socket 对象,
     * @throws IOException
     */

    public static String receive(Socket s) throws IOException{
        String ssss="";
        InputStream is=null;
        try {

            is = s.getInputStream();
            //4.对获取的输入流进行的操作
            byte [] b = new byte[100];
            int len;
            while((len = is.read(b))!=-1){
                String str = new String(b,0,len);
                ssss+=str;
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            s.shutdownInput();
        }
        System.out.println("收到的包："+ssss);
        return ssss;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("-------TGS打开----------");
   //     AP ui = new AP();
   //     ui.setVisible(true);
        int port=1234;
    //    TGSReceiver r = new TGSReceiver();
    //    new Thread(r.listener(port,ui)).start();
    }


}



