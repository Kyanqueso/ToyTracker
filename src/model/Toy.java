package model;

public class Toy {
    private int id;
    private String name;
    private String dateOrder;
    private String dateReceive;
    private String brandName;
    private String category;
    private String supplier;
    private double amount;
    private double downpayment;
    private double discount;
    private double balance;
    private String fullyPaid;
    private String barcode;
    private String imagePath;

    // Constructor, Getters, and Setters
    public Toy(int id, String name, String dateOrder, String dateReceive,
               String brandName, String category, String supplier, double amount,
               double downpayment, double discount, double balance, String fullyPaid, 
               String barcode, String imagePath) {
        this.id = id;
        this.name = name;
        this.dateOrder = dateOrder;
        this.dateReceive = dateReceive;
        this.brandName = brandName;
        this.category = category;
        this.supplier = supplier;
        this.amount = amount;
        this.downpayment = downpayment;
        this.discount = discount;
        this.balance = balance;
        this.fullyPaid = fullyPaid;
        this.barcode = barcode;
        this.imagePath = imagePath;
    }
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDateOrder() {
        return dateOrder;
    }
    public void setDateOrder(String dateOrder) {
        this.dateOrder = dateOrder;
    }
    public String getDateReceive() {
        return dateReceive;
    }
    public void setDateReceive(String dateReceive) {
        this.dateReceive = dateReceive;
    }
    public String getBrandName() {
        return brandName;
    }
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getSupplier() {
        return supplier;
    }
    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public double getDownpayment() {
        return downpayment;
    }
    public void setDownpayment(double downpayment) {
        this.downpayment = downpayment;
    }
    public double getDiscount() {
        return discount;
    }
    public void setDiscount(double discount) {
        this.discount = discount;
    }
    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }
    public String getFullyPaid() {
        return fullyPaid;
    }
    public void setFullyPaid(String fullyPaid) {
        this.fullyPaid = fullyPaid;
    }
    public String getBarcode() {
        return barcode;
    }
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}