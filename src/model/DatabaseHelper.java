package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    // SQLite connection URL
    private static final String DB_URL = "jdbc:sqlite:toynado.db";  // Path to your database file

    // Method to establish connection to the SQLite database
    public static Connection connect() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
            return null;
        }
    }
    
    // Method to initialize the database and create the toys table
    public static void initializeDatabase() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS toys (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                date_order TEXT,
                date_receive TEXT,
                brand_name TEXT,
                category TEXT,
                supplier TEXT NOT NULL,
                amount REAL,
                downpayment REAL,
                discount REAL DEFAULT 0.05,
                balance REAL,
                fully_paid TEXT CHECK(fully_paid IN ('YES','NO')) NOT NULL,
                barcode TEXT,
                image_path TEXT
            );
        """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.out.println("Database initialization failed: " + e.getMessage());
        }
    }
    public static boolean addToy(Toy toy) {
        String sql = """
            INSERT INTO toys(name, date_order, date_receive, brand_name, category, supplier, 
                             amount, downpayment, discount, balance, fully_paid, barcode, image_path)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
        """;

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, toy.getName());
            pstmt.setString(2, toy.getDateOrder());
            pstmt.setString(3, toy.getDateReceive());
            pstmt.setString(4, toy.getBrandName());
            pstmt.setString(5, toy.getCategory());
            pstmt.setString(6, toy.getSupplier());
            pstmt.setDouble(7, toy.getAmount());
            pstmt.setDouble(8, toy.getDownpayment());
            pstmt.setDouble(9, toy.getDiscount());
            pstmt.setDouble(10, toy.getBalance());
            pstmt.setString(11, toy.getFullyPaid());
            pstmt.setString(12, toy.getBarcode());
            pstmt.setString(13, toy.getImagePath()); // ✅ new line
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Add failed: " + e.getMessage());
            return false;
        }
    }
    public static boolean updateToy(Toy toy) {
        String sql = """
            UPDATE toys
            SET name=?, date_order=?, date_receive=?, brand_name=?, category=?, supplier=?,
                amount=?, downpayment=?, discount=?, balance=?, fully_paid=?, barcode=?, image_path=?
            WHERE id=?;
        """;

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, toy.getName());
            pstmt.setString(2, toy.getDateOrder());
            pstmt.setString(3, toy.getDateReceive());
            pstmt.setString(4, toy.getBrandName());
            pstmt.setString(5, toy.getCategory());
            pstmt.setString(6, toy.getSupplier());
            pstmt.setDouble(7, toy.getAmount());
            pstmt.setDouble(8, toy.getDownpayment());
            pstmt.setDouble(9, toy.getDiscount());
            pstmt.setDouble(10, toy.getBalance());
            pstmt.setString(11, toy.getFullyPaid());
            pstmt.setString(12, toy.getBarcode());
            pstmt.setString(13, toy.getImagePath());
            pstmt.setInt(14, toy.getId());          
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean deleteToy(int id) {
        String sql = "DELETE FROM toys WHERE id = ?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Delete failed: " + e.getMessage());
            return false;
        }
    }
    // for testing purposes
    public static void resetAutoIncrement() {
        String sql = "DELETE FROM sqlite_sequence WHERE name='toys'";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("Auto-increment reset.");
        } catch (SQLException e) {
            System.out.println("Failed to reset auto-increment: " + e.getMessage());
        }
    }
    public static Toy getToyById(int id) {
        String sql = "SELECT * FROM toys WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Toy(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("date_order"),
                    rs.getString("date_receive"),
                    rs.getString("brand_name"),
                    rs.getString("category"),
                    rs.getString("supplier"),
                    rs.getDouble("amount"),
                    rs.getDouble("downpayment"),
                    rs.getDouble("discount"),
                    rs.getDouble("balance"),
                    rs.getString("fully_paid"),
                    rs.getString("barcode"),
                    rs.getString("image_path")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error fetching toy: " + e.getMessage());
        }

        return null;
    }
    public static List<Toy> getAllToys() {
        List<Toy> toys = new ArrayList<>();
        String sql = "SELECT * FROM toys";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Toy toy = new Toy(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("date_order"),
                    rs.getString("date_receive"),
                    rs.getString("brand_name"),
                    rs.getString("category"),
                    rs.getString("supplier"),
                    rs.getDouble("amount"),
                    rs.getDouble("downpayment"),
                    rs.getDouble("discount"),
                    rs.getDouble("balance"),
                    rs.getString("fully_paid"),
                    rs.getString("barcode"),
                    rs.getString("image_path")
                );
                toys.add(toy);
            }

        } catch (SQLException e) {
            System.out.println("Error loading toys: " + e.getMessage());
        }
        return toys;
    }
    public static Toy getLastInsertedToy() {
        String sql = "SELECT * FROM toys ORDER BY id DESC LIMIT 1";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return new Toy(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("date_order"),
                    rs.getString("date_receive"),
                    rs.getString("brand_name"),
                    rs.getString("category"),
                    rs.getString("supplier"),
                    rs.getDouble("amount"),
                    rs.getDouble("downpayment"),
                    rs.getDouble("discount"),
                    rs.getDouble("balance"),
                    rs.getString("fully_paid"),
                    rs.getString("barcode"),
                    rs.getString("image_path")
                );
            }
        } catch (SQLException e) {
            System.out.println("Fetch latest toy failed: " + e.getMessage());
        }
        return null;
    }
    /*private void insertToyIntoDatabase(Toy toy) {
        String sql = "INSERT INTO toys (id, name, dateOrder, dateReceive, brandName, category, supplier, amount, downpayment, discount, balance, fullyPaid, barcode) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, toy.getId());
            pstmt.setString(2, toy.getName());
            pstmt.setString(3, toy.getDateOrder());
            pstmt.setString(4, toy.getDateReceive());
            pstmt.setString(5, toy.getBrandName());
            pstmt.setString(6, toy.getCategory());
            pstmt.setString(7, toy.getSupplier());
            pstmt.setDouble(8, toy.getAmount());
            pstmt.setDouble(9, toy.getDownpayment());
            pstmt.setDouble(10, toy.getDiscount());
            pstmt.setDouble(11, toy.getBalance());
            pstmt.setString(12, toy.getFullyPaid());
            pstmt.setString(13, toy.getBarcode());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/
    /*public static void ensureImagePathColumnExists() {
        String checkColumnSQL = "PRAGMA table_info(toys)";
        String alterTableSQL = "ALTER TABLE toys ADD COLUMN image_path TEXT";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(checkColumnSQL)) {

            boolean columnExists = false;

            while (rs.next()) {
                if ("image_path".equalsIgnoreCase(rs.getString("name"))) {
                    columnExists = true;
                    break;
                }
            }

            if (!columnExists) {
                stmt.execute(alterTableSQL);
                System.out.println("✅ 'image_path' column added.");
            } else {
                System.out.println("✅ 'image_path' column already exists.");
            }

        } catch (SQLException e) {
            System.out.println("❌ Failed to check/add column: " + e.getMessage());
        }
    }*/
    //
    public static boolean deleteAll(){
        String sql = "DELETE from toys";
        
        try (Connection conn = connect(); PreparedStatement pst = conn.prepareStatement(sql)){
            pst.executeUpdate();
            return true;
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
            return false;
        }
    }
}