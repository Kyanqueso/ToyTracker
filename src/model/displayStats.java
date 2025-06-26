package model;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class displayStats {
    //basic summary
    public static String getTotalToys(){
        String query = "select COUNT(*) from toys";
        try (Connection conn = DatabaseHelper.connect()){
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt(1); 
                return String.valueOf(count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }
    public static String getTotalAmount(){
        String query = "select SUM(amount) from toys";
        try (Connection conn = DatabaseHelper.connect()) {
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            
            if(rs.next()){
                int sum = rs.getInt(1);
                return String.valueOf(sum);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }
    public static String getMostExpensive(){
        String query = "select name, amount from toys where amount = (SELECT MAX(amount) FROM toys)";
        try (Connection conn = DatabaseHelper.connect()){
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            
            if(rs.next()){
                String name = rs.getString("name");
                int amount = rs.getInt("amount");
                return name + " (PHP" + amount + ")";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String getTopSupplier(){
        String query = "SELECT supplier, COUNT(*) AS total_supplied\n" +
                        "FROM toys\n" +
                        "GROUP BY supplier\n" +
                        "ORDER BY total_supplied DESC\n" +
                        "LIMIT 1";
        try(Connection conn = DatabaseHelper.connect()){
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            
            if(rs.next()){
                String topSupplier = rs.getString("supplier");
                return topSupplier;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
    public static Map<String, Double> getGrossSpending(){
        Map <String, Double> grossSpending = new HashMap<>();
        
        String query = "SELECT date_order, amount FROM toys";
        
        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMM yyyy");
            
            while(rs.next()){
                String dateOrderStr = rs.getString("date_order");
                double amount = rs.getDouble("amount");
                
                if (dateOrderStr != null) {
                    LocalDate dateOrder = LocalDate.parse(dateOrderStr, inputFormatter);
                    String monthYear = dateOrder.format(outputFormatter); // e.g., "Jan 2025"

                    grossSpending.put(monthYear, grossSpending.getOrDefault(monthYear, 0.0) + amount);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return grossSpending;
    }
    //line chart
    public static Map<String, Double> getMonthlySpending() {
        Map<String, Double> monthlySpending = new HashMap<>();

        String query = "SELECT date_order, balance FROM toys"; 

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // match DB format
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMM yyyy");  // e.g., Jan 2025

            while (rs.next()) {
                String dateOrderStr = rs.getString("date_order");
                double amount = rs.getDouble("balance");

                if (dateOrderStr != null) {
                    LocalDate dateOrder = LocalDate.parse(dateOrderStr, inputFormatter);
                    String monthYear = dateOrder.format(outputFormatter); // e.g., "Jan 2025"

                    monthlySpending.put(monthYear, monthlySpending.getOrDefault(monthYear, 0.0) + amount);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return monthlySpending;
    }
    //bar chart
    public static Map<String, Integer> getNumOrders(){
        Map<String,Integer> orderPerMonth = new HashMap<>();
        
        String query = "select date_order from toys";
        
        try (Connection conn = DatabaseHelper.connect()){
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); 
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMM yyyy");
            
            while(rs.next()){
                String dateOrderStr = rs.getString("date_order");
                
                if (dateOrderStr != null) {
                    LocalDate dateOrder = LocalDate.parse(dateOrderStr, inputFormatter);
                    String monthYear = dateOrder.format(outputFormatter); // e.g., "Jan 2025"
                    
                    orderPerMonth.put(monthYear, orderPerMonth.getOrDefault(monthYear, 0) + 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderPerMonth;
    }
    //pie chart
    public static Map<String, Integer> getPaymentStatus() {
        Map<String, Integer> statusCounts = new HashMap<>();

        String query = "SELECT fully_paid, COUNT(*) AS count FROM toys GROUP BY fully_paid";

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                String status = rs.getString("fully_paid");
                int count = rs.getInt("count");
                statusCounts.put(status, count);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return statusCounts;
    }
}