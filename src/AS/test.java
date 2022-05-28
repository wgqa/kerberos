package AS;

import java.sql.SQLException;
import java.sql.Statement;

public class test {
    public static void main(String[] args) throws SQLException {
        DBconncet db = new DBconncet();
        Statement stat = db.connect().createStatement();
        String id = "1111";
        boolean exist = db.selectID(stat,id);
        System.out.println(exist);

    }
}
