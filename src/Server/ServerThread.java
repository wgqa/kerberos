package Server;

import Des.DES;
import TGS.TGS;

import java.io.*;
import java.net.Socket;
import java.text.ParseException;

public class ServerThread extends Thread{
    // 和本线程相关的Socket
    Socket socket = null;

    public ServerThread(Socket socket) {
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

            DataStruct.Packet p = Server.packAnalyse(messageFromClient);
            System.out.println("server解析出来的包为:"+p.toString());
            //根据解析出来的包判断 是否合法，合法的话生成一个expandcode用来给client与应用服务做认证。
            String expandCode="qwertyuiop";
            //message   0011111100000011000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000011111000001000000010001000000101001010000001100100011000010100111110000001101000011001111110110111100101110110010010100001011110101111001111001010111100011100101100010110110100010011000111011100011100001101101010001011000111011011000101111001000111100010101111011010100110100010111101101111111111011101110110111111011000111010000111000000011100111011100110110111111000000101000001001010001101111111111110001100111001011011001010010111001000
            //          0011111100000011000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000011111000001000000010001000000101001010000001100100011000010100111110000001101000011001111110110111100101110110010010100001011110101111001111001010111100011100101100010110110100010011000111011100011100001101101010001011000111011011000101111001000111100010101111011010100110100010111101101111111111011101110110111111011000111010000111000000011100111011100110110111111000000101000001001010001101111111111110001100111001011011001010010111001000
            // 发送
            // 建立连接后获得输出流
            OutputStream outputStream = socket.getOutputStream();
            DataStruct.Packet verifyToClient = Server.packetToClient(expandCode);
            System.out.println("server packet to client:"+verifyToClient.toString());
            String messageToClient = verifyToClient.getHead().headOutput()+verifyToClient.packageOutput();
            System.out.println("server message to client:"+messageToClient);
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
