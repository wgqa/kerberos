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
        try {
            //1.创建一个服务器端Socket，即ServerSocket，指定绑定的端口，并监听此端口
            ServerSocket serverSocket = new ServerSocket(1234);
            InetAddress address = InetAddress.getLocalHost();
            String ip = address.getHostAddress();
            Socket socket = null;
            //2.调用accept()等待客户端连接
            System.out.println("~~~TGS服务端已就绪，等待客户端接入~，服务端ip地址: " + "192.198.43.233");

            while(true){
                //调用accept()方法开始监听，等待客户端的连接
                socket=serverSocket.accept();
                //创建一个新的线程
                TGSThread tgsThread=new TGSThread(socket);
                //启动线程
                tgsThread.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
