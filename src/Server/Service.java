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
import static RSA.getKey.pubkey3;
import static Server.StringToUnicode.gbEncoding;
import static Server.StringToUnicode.stringToUnicode;

public class Service {
    private static String pubKey="";
    public static void main(String[] args) throws Exception {
        try {
            //1.创建一个服务器端Socket，即ServerSocket，指定绑定的端口，并监听此端口
            ServerSocket serverSocket = new ServerSocket(1231);
            InetAddress address = InetAddress.getLocalHost();
            String ip = address.getHostAddress();
            Socket socket = null;
            //2.调用accept()等待客户端连接
            System.out.println("~~~Server服务端已就绪，等待客户端接入~，服务端ip地址: " + "192.198.43.233");
            while(true){
                //调用accept()方法开始监听，等待客户端的连接
                socket=serverSocket.accept();
                //创建一个新的线程
                ServiceThread serviceThread=new ServiceThread(socket);
                //启动线程
                serviceThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
