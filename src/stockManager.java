import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class stockManager {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== STOCK MANAGEMENT =====");
            System.out.println("1. View All Products");
            System.out.println("2. Search Product by Barcode");
            System.out.println("3. Update Stock Quantity");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number.");
                continue;
            }

            if (choice == 1) {
                viewProducts();
            } else if (choice == 2) {
                System.out.print("Enter barcode: ");
                String barcode = sc.nextLine();
                searchProduct(barcode);
            } else if (choice == 3) {
                System.out.print("Enter barcode: ");
                String barcode = sc.nextLine();

                System.out.print("Enter new stock quantity: ");
                try {
                    int quantity = Integer.parseInt(sc.nextLine());

                    if (quantity < 0) {
                        System.out.println("Stock cannot be negative.");
                    } else {
                        updateStock(barcode, quantity);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Enter a valid quantity.");
                }
            } else if (choice == 4) {
                System.out.println("Stock Management closed.");
                break;
            } else {
                System.out.println("Invalid choice.");
            }
        }

        sc.close();
    }

    static void viewProducts() {
        String sql = "SELECT barcode, name, price, stock FROM products";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\nBarcode | Product | Price | Stock");
            while (rs.next()) {
                System.out.println(
                    rs.getString("barcode") + " | " +
                    rs.getString("name") + " | Rs. " +
                    rs.getDouble("price") + " | " +
                    rs.getInt("stock")
                );
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    static void searchProduct(String barcode) {
        String sql = "SELECT barcode, name, price, stock "
                   + "FROM products WHERE barcode = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, barcode);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Product: " + rs.getString("name"));
                    System.out.println("Price: Rs. " + rs.getDouble("price"));
                    System.out.println("Stock: " + rs.getInt("stock"));
                } else {
                    System.out.println("Product not found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    static void updateStock(String barcode, int quantity) {
        String sql = "UPDATE products SET stock = ? WHERE barcode = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setString(2, barcode);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Stock updated successfully!");
            } else {
                System.out.println("Product not found.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}