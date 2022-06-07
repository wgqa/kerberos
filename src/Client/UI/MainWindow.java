package Client.UI;

import DataStruct.Packet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import Client.Client;
import Client.MD5;

import static Client.Client.ServerIP;
import static Client.Connection.*;

public class MainWindow extends JFrame {

    private JPanel contentPane;  //私有成员
    private JTextField username2;
    private JPasswordField password2;//加2是防重名
    private JTextArea messageOnScreen;
    private JTextArea message1;
    private String sjzMD5 = "c2c9a44fe1541d5af3b26484b38da669";
    private String wgqMD5 = "562ab9820edb273392fbc538887e07ad";
    private String hjfMD5 = "a7bd6cdd619ba0202d8c751e96beb091";
    //写在类中的是成员，其他地方能用，写在构造函数中的是一次性的
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    MainWindow frame = new MainWindow();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //构造函数，在这里写主窗口的参数设置
    public MainWindow(){
        setTitle("登陆页面");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//设置默认的退出方式
        setBounds(400, 250, 855, 475);//设置frame大小

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(null);
        setContentPane(contentPane);
        //用户名标签
        JLabel username = new JLabel("用户名\uFF1A");
        username.setBounds(43, 167, 72, 18);
        username.setFont(new Font("仿宋", Font.PLAIN, 18));
        //密码标签
        JLabel password = new JLabel("\u5BC6\u7801\uFF1A");
        password.setBounds(43, 267, 72, 18);
        password.setFont(new Font("仿宋", Font.PLAIN, 18));

        //JTextField写入文本框 JTextarea 显示文本框
        username2 =new JTextField();
        username2.setBounds(129, 164, 176, 24);
        username2.setFont(new Font("仿宋", Font.PLAIN, 18));
        username2.setColumns(10);
        password2 = new JPasswordField();
        password2.setEchoChar('*');
        password2.setBounds(129, 264, 176, 24);
        password2.setFont(new Font("仿宋", Font.PLAIN, 18));
        password2.setColumns(10);

        //显示报文
        //JScrollPane scrollPane = new JScrollPane();
        //scrollPane.setBounds(437, 48, 366, 358);
        //contentPane.add(scrollPane);

        //添加滚动条
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(437, 48, 366, 358);
        contentPane.add(scrollPane);

        messageOnScreen = new JTextArea();
        messageOnScreen.setLineWrap(true);
        messageOnScreen.setEditable(false);
        messageOnScreen.setForeground(Color.GRAY);
        messageOnScreen.setFont(new Font("仿宋", Font.PLAIN, 18));
        messageOnScreen.setColumns(10);
        messageOnScreen.append("");
        scrollPane.setViewportView(messageOnScreen);


//        //实现注册和登录按钮
//        JButton signup =new JButton("注册");
//        signup.setBounds(296, 98, 72, 27);
//        signup.setFont(new Font("仿宋", Font.PLAIN, 18));
//        signup.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                dispose();
//                SIGNUP frame1=new SIGNUP();
//                frame1.setVisible(true);
//            }
//        });
        //注册会关闭主窗口怎么解决

        JButton login = new JButton("登录");
        login.setBounds(296, 367, 72, 27);
        login.setFont(new Font("仿宋", Font.PLAIN, 18));
        login.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                Packet packetFromAS = new Packet();
                Packet packetFromTGS = new Packet();
                Packet packetFromServer = new Packet();
                //p = Client.signin2(username2.getText(),password2.getText());
                //
                String message="";//未加密的发送报文
                //String messageSendUnencrypted = "";
//                String messageSendEncrypted = "";
//                String messageReceiveEncrypted = "";
//                String messageReceiveDecrypted = "";
                if(username2.getText().trim().equals("")||password2.getText().trim().equals("")){
                    System.out.println("用户名或密码为空");
                    JOptionPane.showMessageDialog(contentPane,"用户名或密码为空");
                    return;
                }
                else {
                    String userID = "";
                    if(username2.getText().equals("sjz")){//用户名正确，判断密码
                        if(MD5.getMD5Str(password2.getText()).equals(sjzMD5)){
                            userID = "1111";
                        }
                        else{
                            System.out.println("用户名或密码错误！");
                            JOptionPane.showMessageDialog(contentPane,"用户名或密码错误！");
                            return;
                        }
                    }
                    else if(username2.getText().equals("wgq")){
                        if(MD5.getMD5Str(password2.getText()).equals(wgqMD5)){
                            userID = "1110";
                        }
                        else{
                            System.out.println("用户名或密码错误！");
                            JOptionPane.showMessageDialog(contentPane,"用户名或密码错误！");
                            return;
                        }
                    }
                    else if(username2.getText().equals("hjf")){
                        if(MD5.getMD5Str(password2.getText()).equals(hjfMD5)){
                            userID = "1101";
                        }
                        else{
                            System.out.println("用户名或密码错误！");
                            JOptionPane.showMessageDialog(contentPane,"用户名或密码错误！");
                            return;
                        }
                    }
                    else{
                        System.out.println("用户不存在在");
                        JOptionPane.showMessageDialog(contentPane,"用户不存在！");
                        userID = username2.getText();
                        return;

                    }
                    /*  把
                    *
                    * */
                    packetFromAS =connectToAS(userID);
                    messageOnScreen.append("\n\nclient成功连接到AS");
                    messageOnScreen.append("\n\nclient发送给AS的包（未加密）"+messageSendUnencrypted);
                    messageOnScreen.append("\n\nclient从AS获取的包（未解密）"+messageReceiveEncrypted);
                    messageOnScreen.append("\n\nclient从AS获取的包（解密后）"+messageReceiveDecrypted);
                    System.out.println("在ui里 packetFromAS为"+packetFromAS.toString());
                    packetFromTGS =connectToTGS(packetFromAS);
                    messageOnScreen.append("\n\nclient成功连接到TGS");
                    messageOnScreen.append("\n\nclient发送给TGS的包（未加密）"+messageSendUnencrypted);
                    messageOnScreen.append("\n\nclient发送给TGS的包（加密后）"+messageSendEncrypted);
                    messageOnScreen.append("\n\nclient从TGS获取的包（未解密）"+messageReceiveEncrypted);
                    messageOnScreen.append("\n\nclient从TGS获取的包（解密后）"+messageReceiveDecrypted);
                    System.out.println("在ui里，packetFromTGS为"+packetFromTGS.toString());
                    packetFromServer = connectToServer(packetFromTGS,packetFromAS);
                    messageOnScreen.append("\n\nclient成功连接到Server");
                    messageOnScreen.append("\n\nclient发送给Server的包（未加密）"+messageSendUnencrypted);
                    messageOnScreen.append("\n\nclient发送给Server的包（加密后）"+messageSendEncrypted);

                    try {
                        LIBRARY library = new LIBRARY();
                        library.setVisible(true);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }


                    //connectToService(verifyCode,port);



                    /**********************************************************************************/
                }
                //这里的逻辑应该如何组织
                //登录的逻辑  Kerberos的整体流程是做登录的 使用用户名连接AS
                //注册的逻辑
//                if(connectToAS(username2.getText(),packetFromAS, messageSendUnencrypted,messageSendEncrypted,messageReceiveEncrypted,messageReceiveDecrypted)){
//
//                    messageOnScreen.append("\n成功连接到AS");
//                    messageOnScreen.append("\n未加密的发送包"+messageSendUnencrypted);
//                    messageOnScreen.append("\n加密的发送包"+messageSendEncrypted);
//                    messageOnScreen.append("\n未解密的接收包"+messageReceiveEncrypted);
//                    messageOnScreen.append("\n解密后的接收包"+messageReceiveDecrypted);
//                    if(connectToTGS(packetFromAS, packetFromTGS,messageSendUnencrypted,messageSendEncrypted,messageReceiveEncrypted,messageReceiveDecrypted)){
//                        messageOnScreen.append("\n成功连接到TGS");
//                        messageOnScreen.append("\n未加密的发送包"+messageSendUnencrypted);
//                        messageOnScreen.append("\n加密的发送包"+messageSendEncrypted);
//                        messageOnScreen.append("\n未解密的接收包"+messageReceiveEncrypted);
//                        messageOnScreen.append("\n解密后的接收包"+messageReceiveDecrypted);
//                        if(connectToServer(packetFromTGS,messageSendUnencrypted,messageSendEncrypted,messageReceiveDecrypted)){
//                            messageOnScreen.append("\n成功连接到Server");
//                            messageOnScreen.append("\n未加密的发送包"+messageSendUnencrypted);
//                            messageOnScreen.append("\n加密的发送包"+messageSendEncrypted);
//                            if(messageReceiveDecrypted=="1"){
//                                messageOnScreen.append("\nserver认证成功！");
//                                LIBRARY library = new LIBRARY();
//                                library.setVisible(true);
//                                //跳转到服务界面
//                            }
//                            else if(messageReceiveDecrypted=="0"){
//                                messageOnScreen.append("\nserver认证失败！");
//                            }
//                            else {
//                                messageOnScreen.append("\nserver认证出错！");
//                            }
//                        }
//                        else{
//                            messageOnScreen.append("\n连接Server失败！");
//                        }
//                    }
//                    else{
//                        messageOnScreen.append("\n连接TGS失败！");
//                    }
//                }
//                else {
//                    messageOnScreen.append("\n连接AS失败！");
//                }


            }
        });

        //不显示是因为没有把这些组件加到contentPane中
        contentPane.add(username);
        contentPane.add(password);
        contentPane.add(username2);
        contentPane.add(password2);
        //contentPane.add(signup);
        contentPane.add(login);
        //contentPane.add(messageOnScreen); //

    }
}
