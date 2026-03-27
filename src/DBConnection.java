import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    static Connection conn;

    static Connection getConnection() {
        try{

            conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/C:\\Users\\Ivo\\test","sa","12345");


        } catch (SQLException e){
            e.printStackTrace();
        }

       return conn;
    }
}
