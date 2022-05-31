package Client;


//client 在socket连接中也一直作为socket client存在，工作流程为
//先与as服务器建立socket连接，工作完成后断开和as的socket连接。再与
//TGS服务器建立socket连接。断开连接，再建立与应用服务器的socket连接。
//三次建立socket连接的过程中，仅有IP和端口号不同吧应该？其他工作由通讯接受的报文的不同来区别


import Client.UI.LIBRARY;
import DataStruct.Packet;
import Des.DES;
import RSA.RSA;

import java.io.*;

/*
 *  1. client与AS建立连接，client发送报文，等待一定时间，接收报文，结束。
 *
 *
 *
 * */
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Date;

import static Client.Client.*;
import static DataStruct.Packet.StringToTime;
import static DataStruct.Packet.createTimestamp;

public class Connection {
    public static  String messageSendUnencrypted;
    public static  String messageSendEncrypted;
    public static  String messageReceiveEncrypted;
    public static  String messageReceiveDecrypted;

    private final static String[] Kc = {"3096589494327972966542767555645488415857410521298179560751893624567975523927775168085739664949238616280271893353946263715523651672294362843822766996968340714023382235747900221065977","3889"}; //client公钥
    private final static String[] selfK ={"3096589494327972966542767555645488415857410521298179560751893624567975523927775168085739664949238616280271893353946263715523651672294362843822766996968340714023382235747900221065977" , "1152163794881094595676879571359995304125912323044089952277703799112846640042039256420690483427161040887459792478554485040196767218736825329254102072887596847184101841933983341452289"}; //client私钥



    public static void main(String args[]) throws Exception {
        Packet p = new Packet();

//        connectToAS();
//
//        connectToTGS(p);
//
//       connectToServer(p);
    }





    public static Packet connectToAS(String clientID ) {
        Packet  packetFromAS = new Packet();
        try {
            // 要连接的服务端IP地址和端口
            String host = ASIP;
            int port = 1233;
            // 与服务端建立连接
            Socket socket = new Socket("192.168.43.233", port);
            // Socket socket = new Socket(host, port);
            // 建立连接后获得输出流
            OutputStream outputStream = socket.getOutputStream();


            Packet packetToAS = clientToAS(clientID, TGSID);
            //String message = packetToAS.toString()+"\n"+Client.packetToBinary(packetToAS);
            System.out.println("发送的给AS的packet为"+packetToAS.toString());
            //message =Client.packetToBinary(packetToAS);
            //messageSendUnencrypted = packetToAS.getHead().headOutput() + packetToAS.packageOutput();
            messageSendUnencrypted = packetToAS.toString();
            System.out.println("发送给AS的报文为"+messageSendUnencrypted);
            socket.getOutputStream().write(messageSendUnencrypted.getBytes("UTF-8"));

            //通过shutdownOutput高速服务器已经发送完数据，后续只能接受数据
            socket.shutdownOutput();

            /*  在此期间AS服务器对收到的报文进行验证，如果验证成功就会发送回数据包 */
            /*  设置一个最长等待时间，如果超出时间仍没有收到回复，则当作认证失败，报错 */

            InputStream inputStream = socket.getInputStream();
            byte[] bytes = new byte[1024];
            int len;
            StringBuilder stringBuilder = new StringBuilder();
            while ((len = inputStream.read(bytes)) != -1) {
                //注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
                stringBuilder.append(new String(bytes, 0, len, "UTF-8"));
            }
            System.out.println("从AS收到的报文"+stringBuilder.toString());
            if(stringBuilder.toString().equals("")){
                System.out.println("收到的包为空！");
            }
            messageReceiveEncrypted = stringBuilder.toString();
            packetFromAS = packetAnalyse(stringBuilder.toString(),"");
            /*************验证从AS收到的包是否合法**************************/
            String time1 = BinaryToString(packetFromAS.getTimeStamp());//时间戳
            String time2 = BinaryToString(packetFromAS.getLifeTime());//生命周期
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
            /*************验证从AS收到的包是否合法**************************/
            messageReceiveDecrypted = packetFromAS.getHead().headOutput()+packetFromAS.packageOutput();
            System.out.println("从AS收到的包"+packetFromAS.toString());
            inputStream.close();
            outputStream.close();
            socket.close();

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return packetFromAS;
    }


    public static Packet connectToTGS(Packet packetFromAS ) {
        Packet packetFromTGS = new Packet();
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
            System.out.println("本地ip为"+address.toString());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String clientIP = address.getHostAddress();
        try {
            // 要连接的服务端IP地址和端口
            String host = TGSIP;
            int port = 1234;
            // 与服务端建立连接
            Socket socket = new Socket(host, port);
            // 建立连接后获得输出流
            OutputStream outputStream = socket.getOutputStream();

            System.out.println("从AS中获取的ticket为"+packetFromAS.getTicket().toString());
            Packet packetToTGS = clientToTGS(packetFromAS.getclientID(), "SERV", packetFromAS.getTicket(), Client.generateAuth(packetFromAS.getclientID(), ipToBinary(address), packetFromAS.getSessionKey()));

            //messageSendUnencrypted = packetToTGS.getHead().headOutput() + packetToTGS.packageOutput();
            messageSendUnencrypted = packetToTGS.toString();
            //加密  加密auth部分 auth用as生成的sessionkey加密 client获得sessionkey的地方是中间段，但是发给tgs的不包含此部分了，tgs能解密ticket从ticket中获取
            messageSendEncrypted = packetToTGS.getHead().headOutput() +packetToTGS.packageOutput2()+ DES.encrypt(packetToTGS.getAuth().AuthOutput(),packetFromAS.getSessionKey()); ;
            System.out.println("发送给TGS的报文内容："+messageSendEncrypted);
            System.out.println("发送给TGS的包"+packetToTGS.toString());
            socket.getOutputStream().write(messageSendEncrypted.getBytes("UTF-8"));


            socket.shutdownOutput();

            InputStream inputStream = socket.getInputStream();
            byte[] bytes = new byte[1024];
            int len;
            StringBuilder stringBuilder = new StringBuilder();
            while ((len = inputStream.read(bytes)) != -1) {
                //注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
                stringBuilder.append(new String(bytes, 0, len, "UTF-8"));
            }
            System.out.println("从TGS收到的内容为: " + stringBuilder);
            messageReceiveEncrypted = stringBuilder.toString();
            packetFromTGS = packetAnalyse(stringBuilder.toString(),packetFromAS.getSessionKey());
            /***********************判断tgs 发来的包的时间戳**************************************/
            String time1 = BinaryToString(packetFromAS.getTimeStamp());//时间戳
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
            //messageReceiveDecrypted = packetFromTGS.getHead().headOutput()+packetFromTGS.packageOutput();
            messageReceiveDecrypted = packetFromTGS.toString();
            inputStream.close();
            outputStream.close();
            socket.close();

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return packetFromTGS;
    }

    public  static Packet connectToServer(Packet packetFromTGS,Packet packetFromAS){
        Packet packetFromServer = new Packet();
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String clientIP = address.getHostAddress();
        try {
            // 要连接的服务端IP地址和端口
            String host = ServerIP;
            int port = 1235;
            // 与服务端建立连接
            Socket socket = new Socket("192.168.43.140", port);
            // Socket socket = new Socket(host, port);
            // 建立连接后获得输出流
            OutputStream outputStream = socket.getOutputStream();
            //因为从TGS中获取的包里没有clietID
            //String clientID,DataStruct.Ticket ticketServer, DataStruct.Authenticator authServer
            Packet packetToServer = clientToServer(packetFromAS.getclientID(), packetFromTGS.getTicket(),Client.generateAuth(packetFromAS.getclientID(), ipToBinary(address), packetFromTGS.getSessionKey()));
            //String message = packetToAS.toString()+"\n"+Client.packetToBinary(packetToAS);
            //messageSendUnencrypted =packetToServer.getHead().headOutput()+packetToServer.packageOutput();
            messageSendUnencrypted =packetToServer.toString();
            messageSendEncrypted = packetToServer.getHead().headOutput()+packetToServer.packageOutput2()+ Des.DES.encrypt(packetToServer.getAuth().AuthOutput(),packetFromTGS.getSessionKey());//使用tgs发来的ticket v 的sessionkey加密
            socket.getOutputStream().write(messageSendEncrypted.getBytes("UTF-8"));
            System.out.println("向sever发的包和message是："+packetToServer.toString()+"message"+messageSendEncrypted);
            //通过shutdownOutput高速服务器已经发送完数据，后续只能接受数据
            socket.shutdownOutput();

            /*  在此期间AS服务器对收到的报文进行验证，如果验证成功就会发送回数据包 */
            /*  设置一个最长等待时间，如果超出时间仍没有收到回复，则当作认证失败，报错 */

            InputStream inputStream = socket.getInputStream();
            byte[] bytes = new byte[1024];
            int len;
            StringBuilder sb = new StringBuilder();
            while ((len = inputStream.read(bytes)) != -1) {
                //注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
                sb.append(new String(bytes, 0, len,"UTF-8"));
            }
            System.out.println("从server中获取的包为: " + sb);  // 这里后期要改成显示在客户端上

            messageReceiveDecrypted = sb.toString();
            packetFromServer = packetAnalyse(messageReceiveDecrypted,packetFromTGS.getSessionKey());
            //packetFromServer = packetAnalyse(messageReceiveDecrypted);
            // tostring方法用来展示在屏幕上
            //发送的包是 packetToBinary

            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return packetFromServer;
    }

    public static boolean connectToService(String verifyCode,int port){
        try {
            // 要连接的服务端IP地址和端口
            String host = ServerIP;
            // 与服务端建立连接
            Socket socket = new Socket(host, port);
            // 建立连接后获得输出流
            OutputStream outputStream = socket.getOutputStream();
            socket.getOutputStream().write(verifyCode.getBytes("UTF-8"));


            socket.shutdownOutput();

            InputStream inputStream = socket.getInputStream();
            byte[] bytes = new byte[1024];
            int len;
            StringBuilder stringBuilder = new StringBuilder();
            while ((len = inputStream.read(bytes)) != -1) {
                //注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
                stringBuilder.append(new String(bytes, 0, len, "UTF-8"));
            }
            System.out.println("从Service收到的内容为: " + stringBuilder);

            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}

/* log
* 首先进行了与server的测试，普通的socket通讯可以正常实现
* 出现unhandled exception错误 使用 try catch （ctrl+alt+t）
* */

  /*  Packet{
        head=Head{
            destID='0001',
            sourceID='1111',
            existLogin='0',
            existSessionKey='0',
            existID='1',
            existRequstID='1',
            existTS='1',
            existLifeTime='0',
            existTicket='0',
            existAuthenticator='0' },
        sessionKey='',
        clientID='1111',
        requestID='0010',
        imeStamp='',
        lifeTime='',
        Ticket=Ticket{
            sessionKey='',
            ID='',
            IP='',
            requestID='',
            timeStamp='',
            lifeTime=''},
        Auth=Authenticator{
            clientID='',
            clientIP='',
            timeStamp=''}
    }*/