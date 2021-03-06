package Client.UI;

import Client.Client;
import RSA.RSAUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;

import static Client.Client.ServerIP;
import static Client.UnicodeToString.decodeUnicode;
import static Client.UnicodeToString.unicodeToString;
import static RSA.RSAUtils.decryptByPrivateKey;
import static RSA.RSAUtils.priKey0;
import static RSA.getKey.prikey3;

public class LIBRARY extends JFrame {
    private JPanel contentPane;  //私有成员
    private JTextArea bookInfo;  //显示查询到的信息
    private JTextArea message1;//报文信息
    private JTextField searchKey;// 查询的关键词
    private JButton search; //查询按钮
    private JButton clear; //清空bookinfo
    //写在类中的是成员，其他地方能用，写在构造函数中的是一次性的
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    LIBRARY frame = new LIBRARY( );
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public LIBRARY() throws IOException {

        //组件位置不对是因为没有 setlayout=null
        setTitle("图书馆藏查询系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//设置默认的退出方式
        setBounds(25, 120, 1400, 700);//设置frame大小
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        //显示图书信息
        //JScrollPane scrollPane = new JScrollPane();
        //scrollPane.setBounds(437, 48, 366, 358);
//        bookInfo = new JTextArea();
//        bookInfo.setLineWrap(true);//自动换行
//        bookInfo.setEditable(false);
//        bookInfo.setForeground(Color.BLACK);
//        bookInfo.setFont(new Font("仿宋", Font.PLAIN, 18));
//        bookInfo.append("图书信息在这里显示\n");
//        bookInfo.setBounds(50,100,800,500);
//        bookInfo.setBackground(Color.gray);

//        JScrollPane scrollPane = new JScrollPane(bookInfo);
//        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//        scrollPane.setVisible(true);
        //contentPane.add(scrollPane);
//        contentPane.setVisible(true);
        /**************************************/
        JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setBounds(50,100,800,500);
        contentPane.add(scrollPane2);

        bookInfo = new JTextArea();
        bookInfo.setLineWrap(true);
        bookInfo.setEditable(false);
        bookInfo.setForeground(Color.GRAY);
        bookInfo.setFont(new Font("仿宋", Font.PLAIN, 14));
        bookInfo.setColumns(10);
        bookInfo.setForeground(Color.BLACK);
        bookInfo.setBackground(Color.gray);
        scrollPane2.setViewportView(bookInfo);

        /*******************************************/


        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(860, 100, 500, 500);
        contentPane.add(scrollPane);

        message1 = new JTextArea();
        message1.setLineWrap(true);
        message1.setEditable(false);
        message1.setForeground(Color.GRAY);
        message1.setFont(new Font("仿宋", Font.PLAIN, 14));
        message1.setColumns(10);

        scrollPane.setViewportView(message1);
        /******************************************/


        //下拉选项
        JComboBox comboBox = new JComboBox();
        comboBox.setBounds(10,30,120,30);
        comboBox.addItem("按书名查询");
        comboBox.addItem("按出版社查询");
        comboBox.addItem("按作者查询");
        contentPane.add(comboBox);



        //private JTextField searchKey;// 查询的关键词
        //    private JButton search; //查询按钮
        //    private JButton clear; //清空bookinfo
        searchKey = new JTextField();
        searchKey.setFont(new Font("仿宋", Font.PLAIN, 18));
        searchKey.setBounds(150,30,700,30);

        //查询按钮
        search =new JButton("查询");
        search.setBounds(900, 30, 100, 30);
        search.setFont(new Font("仿宋", Font.PLAIN, 18));
        System.out.println("?");

        //要用equals方法判断，不能直接用 ==
        //先清空显示区域
        HashMap<String, Object> map = null;
        try {
            map = RSAUtils.getKeys();
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            noSuchAlgorithmException.printStackTrace();
        }




        search.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                bookInfo.setText("");
                message1.setText("");
                String messageFromSever="";
                String message="";//de unicode
                String requestToServer="";//发送的查询请求
                String sort=(String)comboBox.getSelectedItem();//获取请求种类
                System.out.println("?");



                if(searchKey.getText() .trim().equals("")){ ;
                    System.out.println("查询内容不能为空！");
                    bookInfo.append("查询内容不能为空！");
                }
                else {
                    //正常应该发送
                    if(sort == "按书名查询"){
                        requestToServer = "1"+searchKey.getText()+"\n";
                    }
                    else if(sort == "按出版社查询"){
                        requestToServer = "2"+searchKey.getText()+"\n";
                    }
                    else if(sort =="按作者查询"){
                        requestToServer = "3"+searchKey.getText()+"\n";
                    }
                    Socket socket;
                    try {
                        socket = new Socket(ServerIP,1231);
                        //在ui里面写读写始终失败
                        if(Client.send(requestToServer,socket)){
                            System.out.println("成功发送给service"+requestToServer+"\n");
                            messageFromSever = Client.receive(socket);
                            //substring 包括头 不包括尾
                            String head = messageFromSever.substring(0,6);
                            System.out.println("head长度为"+head);
                            String signMessage = messageFromSever.substring(6, 6+Integer.parseInt(head));//加密的签名
                            String trueMessage =messageFromSever.substring(6+Integer.parseInt(head));//加密的报文
                            String deSignMessage=decryptByPrivateKey(signMessage, prikey3);//签名解密
                            message1.append("加密的签名："+signMessage+"\n\n\n");
                            message1.append("解密的签名："+deSignMessage+"\n\n\n");

                            if(deSignMessage.equals(trueMessage)){
                                System.out.println("签名认证成功！"+"\n\n\n");
                                message1.append("签名认证成功！"+"\n\n\n");
                            }
                            else{
                                System.out.println("签名认真失败！"+"\n\n\n");
                                message1.append("签名认证失败！"+"\n\n\n");
                            }

                            message1.append("加密的报文信息为："+trueMessage+"\n\n\n");


                            message = decryptByPrivateKey(trueMessage, prikey3);
                            //message=decodeUnicode(messageFromSever);
                            System.out.println("从service收到的消息"+message);
                            socket.close();
                        };

                    } catch (UnknownHostException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    bookInfo.append(message);
                }

            }
        });

        //清空按钮
        clear = new JButton("清空");
        clear.setBounds(1000,30,100,30);
        clear.setFont(new Font("仿宋", Font.PLAIN, 18));
        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bookInfo.setText("");
                message1.setText("");

            }
        });



        //contentPane.add(bookInfo);
        contentPane.add(searchKey);
        contentPane.add(search);
        contentPane.add(clear);

        //contentPane.add(scrollPane);

    }
}
