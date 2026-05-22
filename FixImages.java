import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FixImages {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/ecommerce?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String user = "root";
        String password = "LClc17@$";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            updateImage(conn, "Unique Beaded Necklace", "/images/beaded_necklace.png");
            updateImage(conn, "Unique Wood Carving", "/images/wood_carving.png");
            updateImage(conn, "Unique Warli Painting", "/images/warli_painting.png");
            updateImage(conn, "Indigenous Toda Embroidery", "/images/toda_embroidery.png");
            
            System.out.println("Finished updating images!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateImage(Connection conn, String title, String imageUrl) throws SQLException {
        String query = "UPDATE product SET image_url = ? WHERE title = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, imageUrl);
            pstmt.setString(2, title);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Updated " + rowsAffected + " rows for title: " + title);
        }
    }
}
