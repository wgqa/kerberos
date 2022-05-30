package TGS;

import Des.DES;

import java.io.*;
import java.net.Socket;
import java.text.ParseException;

public class TGSThread extends Thread{
    // 和本线程相关的Socket
    Socket socket = null;

    public TGSThread(Socket socket) {
        this.socket = socket;
    }

    public void run(){
        //3.连接后获取输入流，读取客户端信息
        InputStream is=null;
        InputStreamReader isr=null;
        BufferedReader br=null;
        OutputStream os=null;
        PrintWriter pw=null;
        try {
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
            DataStruct.Ticket TicketV = tgs.generateTicketV(p,socket.getInetAddress());
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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


}
