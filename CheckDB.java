import java.sql.*;

public class CheckDB {
    public static void main(String[] args) throws Exception {
        System.out.println("Connecting to database...");
        try (Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/codethics?useSSL=false&allowPublicKeyRetrieval=true", "root", "TnSf2024@.@")) {
            Statement s = c.createStatement();
            ResultSet rs = s.executeQuery("SHOW CREATE TABLE users");
            if (rs.next()) {
                System.out.println(rs.getString(2));
            } else {
                System.out.println("Table 'projects' not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
