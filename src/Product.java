
public class Product {
    private String barcode;
    private String name;
    private double price;
    private int stock;
    private int quantity;

    public Product(String barcode, String name,
                   double price, int stock) {
        this.barcode = barcode;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.quantity = 1;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSubtotal() {
        return price * quantity;
    }
}
