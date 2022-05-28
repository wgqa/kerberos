package Server;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Service {
    public static void main(String[] args) throws IOException {
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
            while((messageFromClient=br.readLine())!=null){//循环读取客户端的信息
                System.out.println("客户端发送过来的信息" + messageFromClient);
                messageToClient=messageFromClient+"经过sql处理";//这里改成sql查询的结果
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
