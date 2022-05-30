package AS;

import DataStruct.Packet;
import DataStruct.Ticket;
import RSA.RSA;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.SQLException;
import java.sql.Statement;

import static AS.AS.*;
import static Client.Client.TGSID;

public class ASThread extends Thread{
    private final static String[] Kc = {"3096589494327972966542767555645488415857410521298179560751893624567975523927775168085739664949238616280271893353946263715523651672294362843822766996968340714023382235747900221065977","3889"}; //client公钥

    // 和本线程相关的Socket
    Socket socket = null;

    public ASThread(Socket socket) {
        this.socket = socket;
    }

    //线程执行的操作，响应客户端的请求
    public void run(){
        try {
            String ip = socket.getRemoteSocketAddress().toString();
            InetAddress address = (((InetSocketAddress) socket.getRemoteSocketAddress()).getAddress());
            System.out.println("客户端ip为"+address.toString());
            //3.连接后获取输入流，读取客户端信息
            InputStream is=null;
            InputStreamReader isr=null;
            BufferedReader br=null;
            OutputStream os=null;
            PrintWriter pw=null;
            is = socket.getInputStream();     //获取输入流
            isr = new InputStreamReader(is,"UTF-8");
            br = new BufferedReader(isr);
            String info = null;
            String message2 =null;
            while((info=br.readLine())!=null){//循环读取客户端的信息
                System.out.println("客户端发送过来的信息" + info);
                message2=info;
                System.out.println("接收到的报文为"+message2);

            }
            info=br.readLine();
            socket.shutdownInput();//关闭输入流

            Packet packetFromClient=packAnalyse(message2);
            /*********************第一个判断，在这里，去数据库，找clientID，然后找到对应的key************************************************/
            System.out.println("packet from client"+packetFromClient.toString());
            DBconncet db = new DBconncet();
            Statement stat = db.connect().createStatement();
            String id = packetFromClient.getclientID();
            boolean exist = db.selectID(stat,id);
            if(!exist){
                System.out.println("不存在该用户！");
                //应该跳出该循环而不是报错
                socket.shutdownOutput();
                socket.close();
            }
            else{
                System.out.println("用户存在，继续认证");
            }


            //发送
            // 建立连接后获得输出流
            OutputStream outputStream = socket.getOutputStream();

            //生成ticket
            Ticket tgs = generateTicketTGS(packetFromClient,address);
            //System.out.println("tgs"+tgs);
            //System.out.println("?"+packetFromClient.getclientID());//duide
            Packet packetToClient = packData(packetFromClient.getclientID(),TGSID,tgs);
            /*******************加密************************/
            RSA rsa = new RSA();
            String undecrypted = packetToClient.packageOutput3();//加密过ticket的
            // System.out.println("packet to client"+packetToClient);
            //String message = packetToAS.toString()+"\n"+Client.packetToBinary(packetToAS);
            String messageToClient = packetToClient.getHead().headOutput()+ rsa.encrypt(undecrypted,Kc);
            /*******************加密************************/
            System.out.println("message to client"+messageToClient);
            socket.getOutputStream().write(messageToClient.getBytes("UTF-8"));

            //通过shutdownOutput高速服务器已经发送完数据，后续只能接受数据
            socket.shutdownOutput();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

}
