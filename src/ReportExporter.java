
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ReportExporter {

    public static void exportStockReport() {
        String fileName = "Stock_Report_"
                + LocalDate.now() + ".txt";

        String sql = "SELECT barcode, name, price, stock "
                   + "FROM products ORDER BY name";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery();
             FileWriter writer = new FileWriter(fileName)) {

            writer.write("SUPERMARKET STOCK REPORT\n");
            writer.write("Date: " + LocalDate.now() + "\n");
            writer.write("----------------------------------------\n");
            writer.write("Barcode | Product | Price | Stock\n");
            writer.write("----------------------------------------\n");

            while (rs.next()) {
                writer.write(
                    rs.getString("barcode") + " | "
                    + rs.getString("name") + " | Rs. "
                    + rs.getDouble("price") + " | "
                    + rs.getInt("stock") + "\n"
                );
            }

            writer.write("----------------------------------------\n");
            writer.write("End of Stock Report\n");

            System.out.println("Stock report saved: " + fileName);

        } catch (SQLException | IOException ex) {
            System.out.println(
                "Stock report error: " + ex.getMessage());
        }
    }

    public static void exportSalesReport() {
        String fileName = "Sales_Report_"
                + LocalDate.now() + ".txt";

        String sql = "SELECT bill_no, sale_date, total_amount "
                   + "FROM sales "
                   + "WHERE sale_date >= CURDATE() "
                   + "AND sale_date < CURDATE() + INTERVAL 1 DAY "
                   + "ORDER BY sale_date";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery();
             FileWriter writer = new FileWriter(fileName)) {

            writer.write("END-OF-DAY SALES REPORT\n");
            writer.write("Date: " + LocalDate.now() + "\n");
            writer.write("----------------------------------------\n");

            double totalSales = 0;
            int billCount = 0;

            while (rs.next()) {
                writer.write(
                    "Bill No: " + rs.getString("bill_no")
                    + " | Date: " + rs.getTimestamp("sale_date")
                    + " | Amount: Rs. "
                    + String.format("%.2f",
                        rs.getDouble("total_amount"))
                    + "\n"
                );

                totalSales += rs.getDouble("total_amount");
                billCount++;
            }

            writer.write("----------------------------------------\n");
            writer.write("Number of Bills: " + billCount + "\n");
            writer.write(String.format(
                "Total Sales: Rs. %.2f%n", totalSales));

            System.out.println("Sales report saved: " + fileName);

        } catch (SQLException | IOException ex) {
            System.out.println(
                "Sales report error: " + ex.getMessage());
        }
    }
}
