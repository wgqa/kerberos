package Server;

import Client.UI.LIBRARY;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.interfaces.RSAPublicKey;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import static RSA.RSAUtils.encryptByPublicKey;
import static RSA.RSAUtils.pubKey0;
import static Server.StringToUnicode.gbEncoding;
import static Server.StringToUnicode.stringToUnicode;

public class Service {
    private static String pubKey="";
    public static void main(String[] args) throws Exception {
        boolean flag =true;
        while(flag){
            ServerSocket serverSocket = new ServerSocket(1231);

            Socket socket = null;
            //2.调用accept()等待客户端连接

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


            String messageFromClient =null;
            String messageToClient="";

            String[] messageFromClient1 =null;
            String[] messageFromClient2 =null;

            DBconnect db = new DBconnect();
            Statement stat = db.connect().createStatement();

            while((messageFromClient=br.readLine())!=null){//循环读取客户端的信息
                System.out.println("客户端发送过来的信息" + messageFromClient);

                 if(messageFromClient.charAt(0)=='1'){
                     messageFromClient=messageFromClient.substring(1,messageFromClient.length());
                     messageToClient=db.selectByName(stat,messageFromClient);
                 }

                if(messageFromClient.charAt(0)=='2'){
                    messageFromClient=messageFromClient.substring(1,messageFromClient.length());
                    messageToClient=db.selectByPublisher(stat,messageFromClient);
                }

                if(messageFromClient.charAt(0)=='3'){
                    messageFromClient=messageFromClient.substring(1,messageFromClient.length());
                    messageToClient=db.selectByAuthor(stat,messageFromClient);
                }

                if(messageFromClient.charAt(0)=='4'){
                    pubKey=messageFromClient.substring(1,messageFromClient.length());
                }

                if(messageToClient.equals("")){
                    System.out.println("没找符合要求的书");
                    messageToClient="没找符合要求的书";
                }
                System.out.println("发送的信息："+messageToClient);
                messageToClient=gbEncoding(messageToClient);
                //RSAPublicKey pubKey = LIBRARY
                messageToClient = encryptByPublicKey(messageToClient, pubKey0);
                OutputStream outputStream = socket.getOutputStream();
                socket.getOutputStream().write(messageToClient.getBytes("UTF-8"));
            }
//            if(messageFromClient.equals("exit")){
//                flag=false;
//            }
            //发送
            // 建立连接后获得输出流



            //messageFromClient=br.readLine();
            serverSocket.close();
            socket.shutdownInput();//关闭输入流
            socket.close();
        }
    }
}
