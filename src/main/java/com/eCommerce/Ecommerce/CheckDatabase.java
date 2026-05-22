package com.eCommerce.Ecommerce;

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

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            System.out.println("--- Connection Successful ---");
            
            dumpQuery(conn, "SELECT id, seller_name, email, role, enabled FROM sellers");
            dumpQuery(conn, "SELECT id, name, email, role, enabled FROM user");
            dumpQuery(conn, "SELECT COUNT(*) AS total_products, seller_id FROM product GROUP BY seller_id");
            dumpQuery(conn, "SELECT id, name, category, selling_price, seller_id FROM product LIMIT 10");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void dumpQuery(Connection conn, String query) {
        System.out.println("\nExecuting query: " + query);
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            // Print columns
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnLabel(i) + " \t ");
            }
            System.out.println("\n--------------------------------------------------");
            
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i) + " \t ");
                }
                System.out.println();
            }
            if (rowCount == 0) {
                System.out.println("No rows found.");
            }
        } catch (Exception e) {
            System.out.println("Error executing query: " + e.getMessage());
        }
    }
}
