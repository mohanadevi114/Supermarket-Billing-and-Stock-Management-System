
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main extends JFrame {

    JTextField barcodeField, productBarcodeField;
    JTextField nameField, priceField, stockField, restockQtyField;

    JTable cartTable, stockTable;
    DefaultTableModel cartModel, stockModel;

    JTextArea billArea;
    JLabel totalLabel;

    HashMap<String, Product> productMap = new HashMap<>();
    ArrayList<Product> activeBill = new ArrayList<>();

    double total = 0;

    public Main() {
        setTitle("Supermarket Billing and Stock Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setMinimumSize(new Dimension(850, 600));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));

        // PRODUCT ENTRY: GRIDLAYOUT
        JPanel productPanel = new JPanel(
            new GridLayout(2, 5, 5, 5));
        productPanel.setBorder(
            BorderFactory.createTitledBorder("Product Management"));

        productBarcodeField = new JTextField();
        nameField = new JTextField();
        priceField = new JTextField();
        stockField = new JTextField();
        restockQtyField = new JTextField();

        productPanel.add(new JLabel("Product Barcode:"));
        productPanel.add(new JLabel("Product Name:"));
        productPanel.add(new JLabel("Price:"));
        productPanel.add(new JLabel("Stock / New Stock:"));
        productPanel.add(new JLabel("Restock Quantity:"));

        productPanel.add(productBarcodeField);
        productPanel.add(nameField);
        productPanel.add(priceField);
        productPanel.add(stockField);
        productPanel.add(restockQtyField);

        // BARCODE SCANNER
        JPanel barcodePanel = new JPanel(
            new FlowLayout(FlowLayout.LEFT));
        barcodePanel.setBorder(
            BorderFactory.createTitledBorder("Checkout Counter"));

        barcodeField = new JTextField(18);
        JButton addItemButton = new JButton("Add Item");

        barcodePanel.add(new JLabel("Scan Barcode:"));
        barcodePanel.add(barcodeField);
        barcodePanel.add(addItemButton);

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.add(productPanel, BorderLayout.CENTER);
        topPanel.add(barcodePanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // CART TABLE
        cartModel = new DefaultTableModel(
            new String[]{"Barcode", "Product", "Price",
                         "Quantity", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        cartTable = new JTable(cartModel);

        // STOCK TABLE
        stockModel = new DefaultTableModel(
            new String[]{"Barcode", "Product", "Price", "Stock"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        stockTable = new JTable(stockModel);

        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBorder(
            BorderFactory.createTitledBorder("Current Bill"));
        cartPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JPanel stockPanel = new JPanel(new BorderLayout());
        stockPanel.setBorder(
            BorderFactory.createTitledBorder("Stock Details"));
        stockPanel.add(new JScrollPane(stockTable), BorderLayout.CENTER);

        JSplitPane tableSplit = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT, cartPanel, stockPanel);
        tableSplit.setResizeWeight(0.5);

        // RECEIPT TEXT AREA
        billArea = new JTextArea();
        billArea.setEditable(false);
        billArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        billArea.setText("SUPERMARKET BILL\nAdd products to begin.\n");

        JPanel receiptPanel = new JPanel(new BorderLayout());
        receiptPanel.setBorder(
            BorderFactory.createTitledBorder("Bill Receipt"));
        receiptPanel.add(new JScrollPane(billArea), BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        centerPanel.add(tableSplit);
        centerPanel.add(receiptPanel);
        add(centerPanel, BorderLayout.CENTER);

        // BUTTONS: FLOWLAYOUT IN TWO ROWS
        JPanel firstButtonRow = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 5, 3));
        JPanel secondButtonRow = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 5, 3));

        JButton addProductButton = new JButton("Add Product");
        JButton updateButton = new JButton("Update Product");
        JButton deleteButton = new JButton("Delete Product");
        JButton restockButton = new JButton("Restock");
        JButton refreshButton = new JButton("Refresh Stock");

        JButton billButton = new JButton("Generate Bill");
        JButton clearButton = new JButton("Clear Cart");
        JButton stockReportButton = new JButton("Export Stock Report");
        JButton salesReportButton = new JButton("Export Sales Report");

        // Make Generate Bill easy to find
        billButton.setFont(new Font("Arial", Font.BOLD, 14));
        billButton.setPreferredSize(new Dimension(145, 35));

        firstButtonRow.add(addProductButton);
        firstButtonRow.add(updateButton);
        firstButtonRow.add(deleteButton);
        firstButtonRow.add(restockButton);
        firstButtonRow.add(refreshButton);

        secondButtonRow.add(billButton);
        secondButtonRow.add(clearButton);
        secondButtonRow.add(stockReportButton);
        secondButtonRow.add(salesReportButton);

        totalLabel = new JLabel("Total: Rs. 0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 15));

        JPanel buttonRows = new JPanel(new GridLayout(2, 1, 0, 0));
        buttonRows.add(firstButtonRow);
        buttonRows.add(secondButtonRow);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonRows, BorderLayout.CENTER);
        bottomPanel.add(totalLabel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        // BUTTON ACTION LISTENERS
        addItemButton.addActionListener(e -> addItem());
        billButton.addActionListener(e -> generateBill());
        clearButton.addActionListener(e -> clearCart());

        addProductButton.addActionListener(e -> addProduct());
        updateButton.addActionListener(e -> updateProduct());
        deleteButton.addActionListener(e -> deleteProduct());
        restockButton.addActionListener(e -> restockProduct());
        refreshButton.addActionListener(e -> refreshProducts());

        stockReportButton.addActionListener(e -> {
            ReportExporter.exportStockReport();
            JOptionPane.showMessageDialog(this,
                "Stock report export attempted. Check the terminal "
                + "for success or error messages.");
        });

        salesReportButton.addActionListener(e -> {
            ReportExporter.exportSalesReport();
            JOptionPane.showMessageDialog(this,
                "Sales report export attempted. Check the terminal "
                + "for success or error messages.");
        });

        // KEYLISTENER: ENTER ADDS BARCODE ITEM
        barcodeField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    addItem();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    barcodeField.setText("");
                }
            }
        });

        // SELECT STOCK ROW TO LOAD PRODUCT DETAILS
        stockTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()
                    && stockTable.getSelectedRow() >= 0) {

                int row = stockTable.getSelectedRow();

                productBarcodeField.setText(
                    stockModel.getValueAt(row, 0).toString());
                nameField.setText(
                    stockModel.getValueAt(row, 1).toString());
                priceField.setText(
                    stockModel.getValueAt(row, 2).toString());
                stockField.setText(
                    stockModel.getValueAt(row, 3).toString());
                restockQtyField.setText("");
            }
        });

        refreshProducts();
        setVisible(true);
        barcodeField.requestFocusInWindow();
    }

    // LOAD PRODUCTS INTO HASHMAP AND STOCK TABLE
    void refreshProducts() {
        productMap.clear();
        stockModel.setRowCount(0);

        String sql =
            "SELECT barcode, name, price, stock "
            + "FROM products ORDER BY name";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Product p = new Product(
                    rs.getString("barcode"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("stock"));

                productMap.put(p.getBarcode(), p);

                stockModel.addRow(new Object[]{
                    p.getBarcode(), p.getName(),
                    p.getPrice(), p.getStock()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Unable to load products: " + ex.getMessage());
        }
    }

    // ADD ITEM USING HASHMAP
    void addItem() {
        String barcode = barcodeField.getText().trim();

        if (barcode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a barcode!");
            return;
        }

        Product p = productMap.get(barcode);

        if (p == null) {
            JOptionPane.showMessageDialog(this,
                "Product not found. Click Refresh Stock or add the product.");
            return;
        }

        int index = -1;

        for (int i = 0; i < activeBill.size(); i++) {
            if (activeBill.get(i).getBarcode().equals(barcode)) {
                index = i;
                break;
            }
        }

        int quantity = index >= 0
            ? activeBill.get(index).getQuantity() : 0;

        if (quantity >= p.getStock()) {
            JOptionPane.showMessageDialog(this, "Not enough stock!");
            return;
        }

        if (index >= 0) {
            activeBill.get(index).setQuantity(quantity + 1);
        } else {
            Product item = new Product(
                p.getBarcode(), p.getName(), p.getPrice(), p.getStock());
            item.setQuantity(1);
            activeBill.add(item);
        }

        refreshCart();
        barcodeField.setText("");
        barcodeField.requestFocusInWindow();
    }

    // UPDATE CART TABLE AND TOTAL
    void refreshCart() {
        cartModel.setRowCount(0);
        total = 0;

        for (Product p : activeBill) {
            double subtotal = p.getSubtotal();
            total += subtotal;

            cartModel.addRow(new Object[]{
                p.getBarcode(), p.getName(),
                String.format("%.2f", p.getPrice()),
                p.getQuantity(),
                String.format("%.2f", subtotal)
            });
        }

        totalLabel.setText(String.format("Total: Rs. %.2f", total));
    }

    void clearCart() {
        activeBill.clear();
        refreshCart();
        barcodeField.setText("");
        barcodeField.requestFocusInWindow();
    }

    // CREATE PRODUCT
    void addProduct() {
        try {
            String barcode = productBarcodeField.getText().trim();
            String name = nameField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int stock = Integer.parseInt(stockField.getText().trim());

            if (barcode.isEmpty() || name.isEmpty()
                    || price < 0 || stock < 0
                    || !Double.isFinite(price)) {
                throw new IllegalArgumentException(
                    "Enter valid product details.");
            }

            String sql = "INSERT INTO products "
                + "(barcode, name, price, stock) VALUES (?, ?, ?, ?)";

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, barcode);
                ps.setString(2, name);
                ps.setDouble(3, price);
                ps.setInt(4, stock);
                ps.executeUpdate();
            }

            refreshProducts();
            JOptionPane.showMessageDialog(this, "Product added successfully!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Price must be a number and stock must be a whole number.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Add product failed: " + ex.getMessage());
        }
    }

    // UPDATE PRODUCT
    void updateProduct() {
        try {
            String barcode = productBarcodeField.getText().trim();
            String name = nameField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int stock = Integer.parseInt(stockField.getText().trim());

            if (barcode.isEmpty() || name.isEmpty()
                    || price < 0 || stock < 0
                    || !Double.isFinite(price)) {
                throw new IllegalArgumentException(
                    "Enter valid product details.");
            }

            String sql = "UPDATE products SET name=?, price=?, stock=? "
                + "WHERE barcode=?";

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setDouble(2, price);
                ps.setInt(3, stock);
                ps.setString(4, barcode);

                if (ps.executeUpdate() == 0) {
                    JOptionPane.showMessageDialog(this, "Product not found.");
                    return;
                }
            }

            refreshProducts();
            JOptionPane.showMessageDialog(this, "Product updated successfully!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Enter valid price and stock numbers.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Update failed: " + ex.getMessage());
        }
    }

    // DELETE PRODUCT
    void deleteProduct() {
        String barcode = productBarcodeField.getText().trim();

        if (barcode.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Select a stock row first.");
            return;
        }

        for (Product item : activeBill) {
            if (item.getBarcode().equals(barcode)) {
                JOptionPane.showMessageDialog(this,
                    "Remove this product from the current cart first.");
                return;
            }
        }

        int answer = JOptionPane.showConfirmDialog(
            this, "Delete product " + barcode + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "DELETE FROM products WHERE barcode=?")) {

            ps.setString(1, barcode);

            if (ps.executeUpdate() == 0) {
                JOptionPane.showMessageDialog(this, "Product not found.");
                return;
            }

            refreshProducts();
            JOptionPane.showMessageDialog(this, "Product deleted.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Delete failed: " + ex.getMessage());
        }
    }

    // RESTOCK USING A SEPARATE QUANTITY FIELD
    void restockProduct() {
        try {
            String barcode = productBarcodeField.getText().trim();
            int quantity = Integer.parseInt(
                restockQtyField.getText().trim());

            if (barcode.isEmpty() || quantity <= 0) {
                JOptionPane.showMessageDialog(this,
                    "Enter a barcode and a positive restock quantity.");
                return;
            }

            String sql =
                "UPDATE products SET stock = stock + ? WHERE barcode = ?";

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, quantity);
                ps.setString(2, barcode);

                if (ps.executeUpdate() == 0) {
                    JOptionPane.showMessageDialog(this, "Product not found.");
                    return;
                }
            }

            refreshProducts();
            restockQtyField.setText("");
            JOptionPane.showMessageDialog(this,
                "Stock restocked successfully!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Enter a whole-number restock quantity.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Restock failed: " + ex.getMessage());
        }
    }

    // GENERATE BILL WITH DATABASE TRANSACTION
    void generateBill() {
        if (activeBill.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty!");
            return;
        }

        String billNo = "BILL-" + System.currentTimeMillis();
        StringBuilder receipt = new StringBuilder();
        double billTotal = 0;

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                // Lock and validate stock; calculate current DB prices
                for (Product item : activeBill) {
                    try (PreparedStatement ps = con.prepareStatement(
                        "SELECT name, price, stock FROM products "
                        + "WHERE barcode=? FOR UPDATE")) {

                        ps.setString(1, item.getBarcode());

                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next()) {
                                throw new SQLException(
                                    "Product not found: " + item.getBarcode());
                            }

                            if (rs.getInt("stock") < item.getQuantity()) {
                                throw new SQLException(
                                    "Insufficient stock for " + rs.getString("name"));
                            }

                            billTotal += rs.getDouble("price")
                                * item.getQuantity();
                        }
                    }
                }

                // Save sales header FIRST for the foreign key
                try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO sales (bill_no, sale_date, total_amount) "
                    + "VALUES (?, NOW(), ?)")) {
                    ps.setString(1, billNo);
                    ps.setDouble(2, billTotal);
                    ps.executeUpdate();
                }

                receipt.append("       SUPERMARKET BILL\n");
                receipt.append("Bill No: ").append(billNo).append("\n");
                receipt.append("----------------------------------------\n");

                for (Product item : activeBill) {
                    String barcode = item.getBarcode();
                    int quantity = item.getQuantity();

                    try (PreparedStatement ps = con.prepareStatement(
                        "SELECT name, price FROM products WHERE barcode=?")) {

                        ps.setString(1, barcode);

                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next()) {
                                throw new SQLException(
                                    "Product not found: " + barcode);
                            }

                            String name = rs.getString("name");
                            double price = rs.getDouble("price");
                            double subtotal = price * quantity;

                            try (PreparedStatement update = con.prepareStatement(
                                "UPDATE products SET stock=stock-? "
                                + "WHERE barcode=? AND stock>=?")) {
                                update.setInt(1, quantity);
                                update.setString(2, barcode);
                                update.setInt(3, quantity);

                                if (update.executeUpdate() != 1) {
                                    throw new SQLException(
                                        "Stock update failed for " + name);
                                }
                            }

                            try (PreparedStatement psItem = con.prepareStatement(
                                "INSERT INTO sale_items "
                                + "(bill_no, barcode, product_name, price, "
                                + "quantity, subtotal) VALUES (?, ?, ?, ?, ?, ?)")) {
                                psItem.setString(1, billNo);
                                psItem.setString(2, barcode);
                                psItem.setString(3, name);
                                psItem.setDouble(4, price);
                                psItem.setInt(5, quantity);
                                psItem.setDouble(6, subtotal);
                                psItem.executeUpdate();
                            }

                            receipt.append(String.format(
                                "%-20s x %d  Rs. %.2f%n",
                                name, quantity, subtotal));
                        }
                    }
                }

                con.commit();

                receipt.append("----------------------------------------\n");
                receipt.append(String.format(
                    "TOTAL: Rs. %.2f%n", billTotal));
                receipt.append("Thank you! Visit Again.");

                billArea.setText(receipt.toString());
                activeBill.clear();
                refreshCart();
                refreshProducts();

                JOptionPane.showMessageDialog(this,
                    "Bill generated successfully!\nBill No: " + billNo);

                barcodeField.requestFocusInWindow();

            } catch (Exception ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(true);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Bill failed: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
}
