package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Statement;

import static RSA.RSAUtils.encryptByPublicKey;
import static RSA.getKey.pubkey3;
import static Server.Service.supplement;

public class ServiceThread extends Thread{

    // 和本线程相关的Socket
    Socket socket = null;

    public ServiceThread(Socket socket) {
        this.socket = socket;
    }

    public void run(){
        InputStream is=null;
        InputStreamReader isr=null;
        BufferedReader br=null;
        OutputStream os=null;
        PrintWriter pw=null;
        try {
            is = socket.getInputStream();     //获取输入流
            isr = new InputStreamReader(is,"UTF-8");
            br = new BufferedReader(isr);


            String messageFromClient =null;
            String messageToClientSign="";
            String messageToClientTrue="";
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



                if(messageToClient.equals("")){
                    System.out.println("没找符合要求的书");
                    messageToClient="没找符合要求的书";
                }
                System.out.println("发送的信息："+messageToClient);
                //messageToClient=gbEncoding(messageToClient);
                //RSAPublicKey pubKey = LIBRARY
                //签名
                messageToClientTrue = encryptByPublicKey(messageToClient, pubkey3);
                messageToClientSign=encryptByPublicKey(messageToClientTrue,pubkey3);
                messageToClient=supplement(6,String.valueOf(messageToClientSign.length()))+messageToClientSign+messageToClientTrue;
                System.out.println("sign的长度为"+messageToClientSign.length());
                System.out.println("sign的补齐为"+supplement(6,String.valueOf(messageToClientSign.length())));
                OutputStream outputStream = socket.getOutputStream();
                socket.getOutputStream().write(messageToClient.getBytes("UTF-8"));
            }
//            if(messageFromClient.equals("exit")){
//                flag=false;
//            }
            //发送
            // 建立连接后获得输出流


            //messageFromClient=br.readLine();
            socket.shutdownInput();//关闭输入流
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
