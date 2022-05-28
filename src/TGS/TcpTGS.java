package TGS;

import DataStruct.Packet;
import Des.DES;
import jdk.swing.interop.SwingInterOpUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.util.Arrays;

import static Client.Client.TGSID;


public class TcpTGS {

    public static String BinaryToString(String string)
        {
            int length = string.length();
           char C[] = string.toCharArray();
           String M[] = new String[length/4];
           for(int i=0;i<M.length;i++){
               M[i] = "";
            }
            //System.out.println(""+M[0]+M[1]+M[2]+M[3]+"-"+M[4]+M[5]+"-"+M[6]+M[7]+" "+M[8]+M[9]+":"+M[10]+M[11]+":"+M[12]+M[13]);
            int M1[] = new int[length/4];

            String s ="";  //进行二进制的累加
            for(int i1=0;i1<length;i1++)
            {
                M[i1/4] = M[i1/4]+C[i1];
                if(i1%4 == 3) {
                    M1[i1/4] = Integer.parseInt(M[i1/4],2);

                    s = s + M1[i1/4]; //加入string中
                }
            }
            return s;
        }

    public static DataStruct.Packet packAnalyse(String message) {

        DataStruct.Packet p = new DataStruct.Packet();

        int headLength = p.getHead().headOutput().length();
        char M[] = message.toCharArray();
        System.out.println(Arrays.toString(M)+"\n");

        String s[] = new String[headLength];
        for (int i = 0; i < headLength; i++) {
            s[i] = String.valueOf(M[i]);
        }
        System.out.println(Arrays.toString(s)+"\n");
      /*  p.setHead(new DataStruct.Head(s[0] + s[1] + s[2] + s[3], s[4] + s[5] + s[6] + s[7], s[8], s[9], s[10],
                s[11], s[12], s[13], s[14], s[15]));*/
        p.setHead(new DataStruct.Head(s[0] + s[1] + s[2] + s[3], s[4] + s[5] + s[6] + s[7], s[8], s[9], s[10],
                s[11], s[12], s[13], s[14], s[15],/* s[16]+s[17]+s[18]+s[19] ,*/  "",
                s[148]+s[149]+s[150]+s[151]+s[152]+s[153]+s[154]+s[155]+s[156]+s[157]+s[158]+s[159]+s[160]+s[161]+s[162]+s[163]));

       /* DataStruct.Packet p2 = new DataStruct.Packet();
        p2.setHead(new DataStruct.Head("0000", "1111", "2", "3","3",
                "4", "5", "6","7", "8"));
        //System.out.println(p.getHead());
        System.out.println(p2);*/
        String pack = message.replaceFirst(p.getHead().headOutput(), "");

        System.out.println("分析的包："+ p.toString());
        String messageT = "" , messageA = "";
        int tic = Integer.parseInt(BinaryToString(p.getHead().getExpend()));//加密后tic的长度
        for(int i = headLength;i<message.length();i++)
        {
            if(i<headLength+4)
                p.setRequestID(p.getRequestID()+M[i]);
            else if(i<headLength+4+tic){
                messageT = messageT + M[i];
            }
            else {
                messageA = messageA + M[i];
            }

        }

        return p;
    }

    public static void main(String[] args) throws IOException, ParseException {
        //1.创建一个服务器端Socket，即ServerSocket，指定绑定的端口，并监听此端口
        ServerSocket serverSocket = new ServerSocket(1234);
        InetAddress address = InetAddress.getLocalHost();
        String ip = address.getHostAddress();
        Socket socket = null;
        //2.调用accept()等待客户端连接
        System.out.println("~~~TGS服务端已就绪，等待客户端接入~，服务端ip地址: " + "192.198.43.233");
        socket = serverSocket.accept();

        Socket s = socket;

        //3.连接后获取输入流，读取客户端信息
        InputStream is=null;
        InputStreamReader isr=null;
        BufferedReader br=null;
        OutputStream os=null;
        PrintWriter pw=null;
        is = socket.getInputStream();     //获取输入流
        isr = new InputStreamReader(is,"UTF-8");
        br = new BufferedReader(isr);

        String verify =null;//判断客户端的信息是否存在
        String messageFromClient = null;

        while((verify=br.readLine())!=null){//循环读取客户端的信息
            System.out.println("客户端发送过来的信息" + verify);
            messageFromClient=verify;
        }


        System.out.println("messageFromClient"+messageFromClient);
        TGS tgs = new TGS();
        DataStruct.Packet p = tgs.packAnalyse(messageFromClient);

        //发送
        // 建立连接后获得输出流
        OutputStream outputStream = socket.getOutputStream();
        DataStruct.Ticket TicketV = tgs.generateTicketV(p,s.getInetAddress());
        DataStruct.Packet packetToClient = tgs.packData(p.getHead().getSourceID(),p.getRequestID(),TicketV);
        System.out.println("发送给client的packet是:"+packetToClient.toString());
        String messageToClient2 = packetToClient.packageOutput4();//ticket使用rsa加密过，其他的没变
        System.out.println("使用rsa加密了ticket v 其他部分没变"+messageToClient2);
        String messageToClient =packetToClient.getHead().headOutput()+ DES.encrypt(messageToClient2, p.getTicket().getSessionKey());//使用从client获取的ticket tgs 的sessionkey加密
        System.out.println("TGS message to client:"+messageToClient);
        socket.getOutputStream().write(messageToClient.getBytes("UTF-8"));


        //messageFromClient=br.readLine();
        socket.shutdownInput();//关闭输入流
        socket.close();

    }
}
