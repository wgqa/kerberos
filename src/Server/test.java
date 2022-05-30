package Server;

import Server.DBconnect;

import java.sql.SQLException;
import java.sql.Statement;

public class test {
    public static void main(String[] args) throws SQLException {
        DBconnect db = new DBconnect();
        Statement stat = db.connect().createStatement();
        String name="西游记";
        String result=db.selectByName(stat,name);
        System.out.println(result);
    }
}
