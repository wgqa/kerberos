package Server;

        import javax.naming.Name;
        import javax.swing.*;
        import java.awt.*;
        import java.sql.Connection;
        import java.sql.DriverManager;
        import java.sql.PreparedStatement;
        import java.sql.ResultSet;
        import java.sql.SQLException;
        import java.sql.Statement;



/**
 * Java中使用JDBC连接数据库
 *  1） 加载驱动 2） 创建数据库连接
 *  3） 创建执行sql的语句 4） 执行语句 5） 处理执行结果 6） 释放资源
 * @author liu.hb
 *
 */
/*public class DBconnect {
    *
     * Statement 和 PreparedStatement之间的关系和区别.
     * 关系：PreparedStatement继承自Statement,都是接口
     * 区别：PreparedStatement可以使用占位符，是预编译的，批处理比Statement效率高
     *
    public static void conn() {
        String URL = "jdbc:mysql://127.0.0.1:3306/Supermarket?characterEncoding=utf-8";
        String USER = "root";
        String PASSWORD = "123";
        // 1.加载驱动程序
        try {
            Class.forName("com.mysql.jdbc.Driver");
            // 2.获得数据库链接
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            // 3.通过数据库的连接操作数据库，实现增删改查（使用Statement类）
            String name = "张三";
            //预编译
            String sql = "select * from userinfo where UserName=?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, name);
            ResultSet rs = statement.executeQuery();
//			String sql="select * from userinfo where UserName='"+name+"'";
//			Statement statement = conn.createStatement();
//			ResultSet rs = statement.executeQuery(sql);
            // 4.处理数据库的返回结果(使用ResultSet类)
            while (rs.next()) {
                System.out.println(rs.getString("UserName") + " " + rs.getString("Password"));
            }

            // 关闭资源【多谢指正】
            rs.close();
            statement.close();
            conn.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        conn();
    }
}*/
        import java.sql.*;

public class DBconnect {
    public static void main(String[] args)
            throws ClassNotFoundException, SQLException {
        final String URL = "jdbc:mysql://localhost:3306/server?useUnicode=true&characterEncoding=utf8&useSSL=true&serverTimezone=UTC";
        final String USER = "root";
        final String PASSWORD = "fxxxysh2001219";
         Class.forName("com.mysql.cj.jdbc.Driver");
        //2. 获得数据库连接
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        Statement st = conn.createStatement();

        try{
            //STEP 2: Register JDBC driver
         //   Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected database successfully...");

            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            st = conn.createStatement();

            String sql = "SELECT Nam, Author, Publisher, Total, Remain, Borrowtimes FROM books";
            ResultSet rs = st.executeQuery(sql);
            //STEP 5: Extract data from result set
            while(rs.next()){
                //Retrieve by column name
                String name  = rs.getString("Nam");
                String author = rs.getString("Author");
                String publisher = rs.getString("Publisher");
                int total = rs.getInt("Total");
                int remain = rs.getInt("Remain");
                int borrowtimes = rs.getInt("Borrowtimes");

                UI ui = new UI();
                ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                String str = "Name: " + name;
              //  ui.setText(str);

                //Display values
                System.out.print("Name: " + name);
                System.out.print(", Author: " + author);
                System.out.print(", Publisher: " + publisher);
                System.out.print(", Total: " + total);
                System.out.print(", Remain: " + remain);
                System.out.print(", Borrowtimes: " + borrowtimes + "\n");

            }
            rs.close();
        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }finally{
            //finally block used to close resources
            try{
                if(st!=null)
                    conn.close();
            }catch(SQLException se){
            }// do nothing
            try{
                if(conn!=null)
                    conn.close();
            }catch(SQLException se){
                se.printStackTrace();
            }//end finally try
        }//end try
      //  System.out.println("Goodbye!");
    }//end main

    public static Connection connect() {
        Statement stat = null;
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");     //加载MYSQL JDBC驱动程序
            System.out.println("Success loading Mysql Driver!");
        }
        catch (Exception e) {
            System.out.print("Error loading Mysql Driver!");
            e.printStackTrace();
        }

        try {
            @SuppressWarnings("unused")
            //Connection  connect= DriverManager.getConnection( "jdbc:mysql:///email?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC","root","123456");
            //连接URL为   jdbc:mysql//服务器地址/数据库名  ，后面的2个参数分别是登陆用户名和密码

            //一开始必须填一个已经存在的数据库
        //    String url = "jdbc:mysql:///email?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC";
        //    conn = DriverManager.getConnection(url, "root", "123456");
        //       stat = conn.createStatement();

            final String URL = "jdbc:mysql://localhost:3306/server?useUnicode=true&characterEncoding=utf8&useSSL=true&serverTimezone=UTC";
            final String USER = "root";
            final String PASSWORD = "fxxxysh2001219";
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            stat = conn.createStatement();
            //创建表user
           /* stat.executeUpdate("create table if not exists `server`"
                    + "(`num` int NOT NULL AUTO_INCREMENT,"
                    + " `receiveid` char(4) NOT NULL, "
                    + " `sendid` char(4) NOT NULL,"
                    + " `len` char(16) NOT NULL, "
                    + "`content` text NULL, "
                    + "PRIMARY KEY (`num`))ENGINE=InnoDB DEFAULT CHARSET=utf8;");*/

            System.out.println("Success connect Mysql server!");

            //关闭数据库
            stat.close();
        }
        catch (Exception e) {
            System.out.print("get data error!");
            e.printStackTrace();
        }
        return conn;
    }

    public boolean log(Statement stat,String mID,String pw) throws SQLException{
        String password;
        String sql="SELECT name,password FROM user where name = \""+mID+"\"";
        try{
            ResultSet rs=stat.executeQuery(sql);//返回结果集
            while(rs.next()){//指针向后移动
                password = rs.getString("password");
                if(password.equals(pw)){
                    return true;
                }
            }
            rs.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public String selectByName(Statement stat,String bookName) throws SQLException{
        String result="";
        String sql="SELECT Nam, Author, Publisher, Total, Remain, Borrowtimes FROM books where Nam = \""+ bookName +"\"";
        ResultSet rs=stat.executeQuery(sql);//返回结果集
        while(rs.next()) {//指针向后移动
            String Nam = rs.getString("Nam");
            String Author = rs.getString("Author");
            String Publisher = rs.getString("Publisher");
            String Total = rs.getString("Total");
            String Remain =rs.getString("Remain");
            String Borrowtimes =rs.getString("Borrowtimes");
            result = result + "名称:"+Nam+" 作者:"+Author+" 出版商:"+Publisher+" 总数:"+Total+" 剩余数量:"+Remain+" 借阅次数:"+Borrowtimes+"\n";
            //result = result + rs.getString();
        }
        rs.close();
        return result;
    }

    public String selectByPublisher(Statement stat,String publisher) throws SQLException{
        String result="";
        String sql="SELECT Nam, Author, Publisher, Total, Remain, Borrowtimes FROM books where Publisher = \""+ publisher +"\"";
        ResultSet rs=stat.executeQuery(sql);//返回结果集
        while(rs.next()) {//指针向后移动
            String Nam = rs.getString("Nam");
            String Author = rs.getString("Author");
            String Publisher = rs.getString("Publisher");
            String Total = rs.getString("Total");
            String Remain =rs.getString("Remain");
            String Borrowtimes =rs.getString("Borrowtimes");
            result = result + "名称:"+Nam+" 作者:"+Author+" 出版商:"+Publisher+" 总数:"+Total+" 剩余数量:"+Remain+" 借阅次数:"+Borrowtimes+"\n";
            //result = result + rs.getString();
        }
        rs.close();
        return result;
    }

    public String selectByAuthor(Statement stat,String author) throws SQLException{
        String result="";
        String sql="SELECT Nam, Author, Publisher, Total, Remain, Borrowtimes FROM books where Author = \""+ author +"\"";
        ResultSet rs=stat.executeQuery(sql);//返回结果集
        while(rs.next()) {//指针向后移动
            String Nam = rs.getString("Nam");
            String Author = rs.getString("Author");
            String Publisher = rs.getString("Publisher");
            String Total = rs.getString("Total");
            String Remain =rs.getString("Remain");
            String Borrowtimes =rs.getString("Borrowtimes");
            result = result + "名称:"+Nam+" 作者:"+Author+" 出版商:"+Publisher+" 总数:"+Total+" 剩余数量:"+Remain+" 借阅次数:"+Borrowtimes+"\n";
            //result = result + rs.getString();
        }
        rs.close();
        return result;
    }

}




