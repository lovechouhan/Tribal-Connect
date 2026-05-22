import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class CheckDatabase {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/ecommerce?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String user = "root";
        String password = "LClc17@$";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("--- Connection Successful ---");
            dumpTable(conn, "seller");
            dumpTable(conn, "user");
            dumpTable(conn, "product");
            dumpTable(conn, "orders");
            dumpTable(conn, "order_item");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void dumpTable(Connection conn, String tableName) {
        System.out.println("\nDumping table: " + tableName);
        String query = "SELECT * FROM `" + tableName + "`";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            // Print columns
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnName(i) + " \t ");
            }
            System.out.println("\n--------------------------------------------------");
            
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i) + " \t ");
                }
                System.out.println();
                if (rowCount >= 10) {
                    System.out.println("... truncated after 10 rows ...");
                    break;
                }
            }
            if (rowCount == 0) {
                System.out.println("No rows found.");
            }
        } catch (Exception e) {
            System.out.println("Error reading table " + tableName + ": " + e.getMessage());
        }
    }
}
