package entity;

public class Electronics extends Product {
    private String brand;
    private int warrantyPeriod;

    public Electronics(int productId, String productName, String description, double price, int quantityInStock, String brand, int warrantyPeriod) {

        super(productId, productName, description, price, quantityInStock, "Electronics");
        this.brand = brand;
        this.warrantyPeriod = warrantyPeriod;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getWarrantyPeriod() {
        return warrantyPeriod;
    }

    public void setWarrantyPeriod(int warrantyPeriod) {
        this.warrantyPeriod = warrantyPeriod;
    }
}
