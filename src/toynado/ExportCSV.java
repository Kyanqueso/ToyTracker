package toynado;
import java.io.*;
import java.util.List;
import model.DatabaseHelper;
import model.Toy;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import com.opencsv.CSVWriter;

public class ExportCSV {
    public void exportToCSV(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export to");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
                 String[] header = {
                    "ID", "Name", "Order Date", "Receive Date", "Brand", "Category", "Supplier",
                    "Amount", "Downpayment", "Discount", "Balance", "Fully Paid", "Barcode", "Image Path"
                };
                writer.writeNext(header);

                // Fetch all toys from the database
                List<Toy> toys = DatabaseHelper.getAllToys();

                // Write each toy's data
                for (Toy toy : toys) {
                    String[] data = {
                        String.valueOf(toy.getId()),
                        toy.getName(),
                        toy.getDateOrder(),
                        toy.getDateReceive(),
                        toy.getBrandName(),
                        toy.getCategory(),
                        toy.getSupplier(),
                        String.valueOf(toy.getAmount()),
                        String.valueOf(toy.getDownpayment()),
                        String.valueOf(toy.getDiscount()),
                        String.valueOf(toy.getBalance()),
                        toy.getFullyPaid(),
                        toy.getBarcode(),
                        toy.getImagePath()
                    };
                    writer.writeNext(data);
                }
                //Debug purposes
                System.out.println("Exported toys to CSV successfully.");
            } catch (IOException e) {
                System.out.println("Failed to export toys to CSV: " + e.getMessage());
            }
        }
    }
}