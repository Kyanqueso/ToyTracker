package toynado;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.TableView;
import javafx.collections.FXCollections;
import model.DatabaseHelper;
import model.Toy;
import com.opencsv.*;
import com.opencsv.exceptions.CsvException;
import java.util.List;

public class ImportCSV {
    public void importFromCSV(Stage stage, TableView<Toy> tableView) throws CsvException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try (CSVReader reader = new CSVReader(new FileReader(file))) {
                List<String[]> rows = reader.readAll();
                boolean isFirstLine = true;

                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter altFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                StringBuilder errorMessages = new StringBuilder();
                int lineNumber = 1;

                // First phase: scan for errors
                for (String[] values : rows) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        lineNumber++;
                        continue;
                    }

                    if (values.length < 14) {
                        errorMessages.append("Line ").append(lineNumber).append(": Expected 14 columns but got ").append(values.length).append("\n");
                        lineNumber++;
                        continue;
                    }

                    try {
                        if (values[0] == null || values[0].trim().isEmpty()) {
                            throw new IllegalArgumentException("'Toy ID' (column 1) is required and cannot be empty.");
                        }
                        try {
                            Integer.parseInt(values[0].trim());
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("'Toy ID' must be a valid integer.");
                        }

                        if (values[1] == null || values[1].trim().isEmpty()) {
                            throw new IllegalArgumentException("'Name' (column 2) is required and cannot be empty.");
                        }

                        if (values[6] == null || values[6].trim().isEmpty()) {
                            throw new IllegalArgumentException("'Supplier' (column 7) is required and cannot be empty.");
                        }

                        String orderDateStr = values[2].trim();
                        String receiveDateStr = values[3].trim();

                        if (!orderDateStr.isEmpty()) {
                            parseFlexibleDate(orderDateStr, isoFormatter, altFormatter);
                        }
                        if (!receiveDateStr.isEmpty()) {
                            parseFlexibleDate(receiveDateStr, isoFormatter, altFormatter);
                        }

                        parseDoubleOrThrow(values[7], "Amount", lineNumber);
                        parseDoubleOrThrow(values[8], "Paid", lineNumber);
                        parseDoubleOrThrow(values[9], "Discount", lineNumber);
                        parseDoubleOrThrow(values[10], "Other Cost", lineNumber);

                        if (values[11] == null || values[11].trim().isEmpty()) {
                            throw new IllegalArgumentException("'Status' (column 12) is required and must be 'YES' or 'NO'.");
                        }
                        
                        String status = values[11].trim().toUpperCase();
                        if (!status.equals("YES") && !status.equals("NO")) {
                            throw new IllegalArgumentException("Status must be 'YES' or 'NO' (case-insensitive). Found: '" + values[11] + "'");
                        }
                        
                    } catch (Exception e) {
                        errorMessages.append("Line ").append(lineNumber).append(": ").append(e.getMessage()).append("\n");
                    }

                    lineNumber++;
                }
                // If any errors, show all and abort import
                if (errorMessages.length() > 0) {
                    showAlert(Alert.AlertType.ERROR, "Import Error", errorMessages.toString());
                    return;
                }

                // Second phase: import rows since no errors found
                isFirstLine = true;
                lineNumber = 1;
                for (String[] values : rows) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        lineNumber++;
                        continue;
                    }

                    String orderDateStr = values[2].trim();
                    String receiveDateStr = values[3].trim();

                    String orderDate = orderDateStr.isEmpty() ? null :
                        parseFlexibleDate(orderDateStr, isoFormatter, altFormatter).format(outputFormatter);
                    String receiveDate = receiveDateStr.isEmpty() ? null :
                        parseFlexibleDate(receiveDateStr, isoFormatter, altFormatter).format(outputFormatter);

                    Toy toy = new Toy(
                        Integer.parseInt(values[0].trim()),
                        values[1].trim(),
                        orderDate,
                        receiveDate,
                        nullIfEmpty(values[4]),
                        nullIfEmpty(values[5]),
                        values[6].trim(),
                        parseDoubleOrThrow(values[7], "Amount", lineNumber),
                        parseDoubleOrThrow(values[8], "Paid", lineNumber),
                        parseDoubleOrThrow(values[9], "Discount", lineNumber),
                        parseDoubleOrThrow(values[10], "Other Cost", lineNumber),
                        values[11].trim().toUpperCase(),
                        nullIfEmpty(values[12]),
                        nullIfEmpty(values[13])
                    );
                    DatabaseHelper.addToy(toy);
                    lineNumber++;
                }
                showAlert(Alert.AlertType.INFORMATION, "Import Success", "Toys imported successfully.");
                loadToysIntoTable(tableView);
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "File Error", "Could not read the file:\n" + e.getMessage());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Unexpected Error", "Something went wrong:\n" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private LocalDate parseFlexibleDate(String input, DateTimeFormatter... formatters) {
        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(input, formatter);
            } catch (Exception ignored) {}
        }
        throw new IllegalArgumentException("Unrecognized date format: " + input);
    }

    private double parseDoubleOrThrow(String value, String fieldName, int lineNumber) {
        try {
            return (value == null || value.trim().isEmpty()) ? 0.0 : Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            throw new NumberFormatException(fieldName + " must be a number: '" + value + "'");
        }
    }

    private String nullIfEmpty(String value) {
        return (value == null || value.trim().isEmpty()) ? null : value.trim();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadToysIntoTable(TableView<Toy> tableView) {
        tableView.setItems(FXCollections.observableArrayList(DatabaseHelper.getAllToys()));
    }
}
