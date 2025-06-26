package toynado;

import com.opencsv.exceptions.CsvException;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.DatabaseHelper;
import model.Toy;
import model.displayStats;
import static model.DatabaseHelper.*;

public class Toynado extends Application {
    protected BorderPane rootLayout;
    protected TableView<Toy> tableView;
    @Override
    public void start(Stage primaryStage) {
        
        DatabaseHelper.initializeDatabase();
        rootLayout = new BorderPane();
        
        // Create menu bar
        HBox menu_bar = new HBox();
        menu_bar.setAlignment(Pos.CENTER);
        menu_bar.getStyleClass().add("menu-bar");
        menu_bar.prefHeightProperty().bind(primaryStage.heightProperty().multiply(0.08));

        // Create Labels for menu items
        Label home = new Label("Home");
        Label table = new Label("Table");
        Label statistics = new Label("Statistics");
        home.getStyleClass().add("menu-bar");
        table.getStyleClass().add("menu-bar");
        statistics.getStyleClass().add("menu-bar");
        
        // Add event handlers to switch content
        home.setOnMouseClicked(e -> showHomePage());
        table.setOnMouseClicked(e -> showTablePage());
        statistics.setOnMouseClicked(e -> showStatsPage());
        
        // Exit Button
        Button exit = new Button("Exit");
        exit.getStyleClass().add("exit-button");
        exit.setOnAction(e -> showExitConfirmation(primaryStage));
        
        //Reset auto increment button, will delete later!!!
        /**Button resetIdButton = new Button("Reset ID Counter");
        resetIdButton.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Reset");
            confirm.setHeaderText("Reset Auto-Increment ID?");
            confirm.setContentText("Only do this if the table is empty or for testing.");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    resetAutoIncrement();
                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setTitle("Reset Successful");
                    info.setHeaderText(null);
                    info.setContentText("ID counter reset.");
                    info.showAndWait();
                }
            });
        }); **/
        // until here to delete soon or just comment out
        
        // Spacer for alignment
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Add menu items to menu bar
        menu_bar.getChildren().addAll(home, table, statistics, spacer, exit); //resetIDButton add here if need
        rootLayout.setTop(menu_bar);
        showHomePage();

        // Add ScrollPane to enable scrolling when screen size is small
        ScrollPane scrollPane = new ScrollPane(rootLayout);
        scrollPane.setFitToWidth(true); // Ensures it stretches horizontally
        scrollPane.setFitToHeight(false); // Allow scrolling vertically
        scrollPane.setPannable(true); // Allow touch/mouse drag scrolling
        scrollPane.setStyle("-fx-background: #D6931C; -fx-background-color: #D6931C;");
        rootLayout.setStyle("-fx-background-color: #D6931C;"); // Ensure root background matches

        // Scene setup
        Scene scene = new Scene(scrollPane, 1000, 500);
        scene.getStylesheets().add(getClass().getResource("menu.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("home_page.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("table_page.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("statistics.css").toExternalForm());

        primaryStage.setTitle("Toynado");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    protected void showHomePage() {
        tableView = new TableView<>();
        
        Label header = new Label("Welcome to ToyNado!");
        header.setId("header-label");
        header.setAlignment(Pos.CENTER);

        // Buttons
        Button btn1 = new Button("+ Add a Toy");
        btn1.setId("addToyButton");
        btn1.setPrefSize(180, 50);
        btn1.setOnAction(e -> openAddForm(tableView));
        
        Button btn2 = new Button("[] View Table");
        btn2.setId("viewTableButton");
        btn2.setPrefSize(180, 50);
        btn2.setOnMouseClicked(e -> showTablePage());

        VBox.setMargin(btn1, new Insets(20, 0, 5, 0));
        VBox.setMargin(btn2, new Insets(5, 0, 20, 0));

        // VBox for buttons
        VBox buttonContainer = new VBox(20);
        buttonContainer.getChildren().addAll(btn1, btn2);
        buttonContainer.setId("button-container");
        buttonContainer.setMaxSize(450, 250);
        buttonContainer.setPadding(new Insets(20));

        // Labels for bottom section
        Toy lastToy = getLastInsertedToy();
        Label label1;
        if (lastToy != null) {
            label1 = new Label("Your last added toy: " + lastToy.getName());
        } else {
            label1 = new Label("Your last added toy: (Empty table)");
        }
        Label label2; // = new Label("Your date last order received: ");
        if(lastToy != null){
            label2 = new Label("Your date last order received: " + lastToy.getDateReceive());
        } else{
            label2 = new Label("Your date last order received: (Empty table)");
        }
        label1.setId("info-label");
        label2.setId("info-label");
        
        // VBox for additional info
        VBox infoContainer = new VBox(20);
        infoContainer.getChildren().addAll(label1, label2);
        infoContainer.setAlignment(Pos.CENTER);
        infoContainer.setPadding(new Insets(20));
        infoContainer.setMaxSize(450, 120);
        infoContainer.setId("info-container");

        VBox.setMargin(infoContainer, new Insets(20, 0, 0, 0));

        // Parent VBox to hold both buttonContainer and infoContainer
        VBox mainContainer = new VBox(30);
        mainContainer.getChildren().addAll(header, buttonContainer, infoContainer);
        mainContainer.setAlignment(Pos.CENTER);

        // StackPane to center everything
        StackPane centerPane = new StackPane(mainContainer);
        centerPane.setAlignment(Pos.CENTER);
        centerPane.setStyle("-fx-background-color: #D6931C;");
        
        // Set home page to center of BorderPane
        rootLayout.setCenter(centerPane);      
    }
    protected void showTablePage() {
        tableView = new TableView<>();
        
        VBox tablePage = new VBox(20);
        tablePage.setAlignment(Pos.CENTER);
        tablePage.setPadding(new Insets(20));

        Label tableLabel = new Label("Toy-Nado Table");

        // Create 'CRUD' buttons
        Button add = new Button("Add");
        add.setId("AddID");
        add.setPrefSize(180, 40);
        add.setOnAction(e -> openAddForm(tableView));

        Button edit = new Button("Edit");
        edit.setId("EditID");
        edit.setPrefSize(180, 40);
        edit.setOnAction(e -> openEditForm(tableView));

        Button delete = new Button("Delete");
        delete.setId("DeleteID");
        delete.setPrefSize(180, 40);
        delete.setOnAction(e -> openDeleteForm(this));

        // Place buttons in an HBox
        HBox buttonBox1 = new HBox(20); 
        buttonBox1.setAlignment(Pos.CENTER);
        buttonBox1.getChildren().addAll(add, edit, delete);
        
        //Search bar
        // First row of filters: search and supplier fields
        HBox topFilterRow = new HBox(15);
        topFilterRow.setAlignment(Pos.CENTER_LEFT);

        TextField searchField = new TextField();
        searchField.setPromptText("Search by name...");
        searchField.setPrefWidth(150);

        TextField supplierField = new TextField();
        supplierField.setPromptText("Supplier name...");
        supplierField.setPrefWidth(150);
        
        TextField brandField = new TextField();
        brandField.setPromptText("Brand name...");
        brandField.setPrefWidth(150);
        
        TextField categoryField = new TextField();
        categoryField.setPromptText("Category name...");
        categoryField.setPrefWidth(150);

        topFilterRow.getChildren().addAll(
            new Label("Search:"), searchField,
            new Label("Supplier:"), supplierField,
            new Label("Brand:"), brandField,
            new Label("Category:"), categoryField
        );

        // Second row of filters: date range, checkboxes, and filter button
        HBox bottomFilterRow = new HBox(15);
        bottomFilterRow.setAlignment(Pos.CENTER);

        DatePicker orderDateFrom = new DatePicker();
        orderDateFrom.setPromptText("Order Date From");
        orderDateFrom.setPrefWidth(100);

        DatePicker orderDateTo = new DatePicker();
        orderDateTo.setPromptText("Order Date To");
        orderDateTo.setPrefWidth(100);

        HBox dateFilterBox = new HBox(10);
        dateFilterBox.setAlignment(Pos.CENTER_LEFT);
        dateFilterBox.getChildren().addAll(
            new Label("Order Date Range:"), orderDateFrom, new Label("to"), orderDateTo
        );

        CheckBox paidCheck = new CheckBox("Fully Paid");
        CheckBox unpaidCheck = new CheckBox("Not Fully Paid");

        Button filterButton = new Button("Apply Filters");
        filterButton.setPrefHeight(30);
        filterButton.setOnAction(e -> {             //Filter function
            applyFilters(searchField.getText(), supplierField.getText(), brandField.getText(),
                         categoryField.getText(), orderDateFrom.getValue(), orderDateTo.getValue(),
                         paidCheck.isSelected(), unpaidCheck.isSelected());
        });

        bottomFilterRow.getChildren().addAll(dateFilterBox, paidCheck, unpaidCheck, filterButton);

        // Combine into vertical box
        VBox filterBar = new VBox(10);
        filterBar.setPadding(new Insets(10));
        filterBar.setAlignment(Pos.TOP_LEFT); // Keep internal elements left-aligned
        filterBar.getChildren().addAll(topFilterRow, bottomFilterRow);

        // Wrap filterBar in a centered HBox
        HBox filterBarWrapper = new HBox(filterBar);
        filterBarWrapper.setAlignment(Pos.CENTER); // Center the whole filter section
        
        // TableView
        tableView.getColumns().clear();
        TableColumn<Toy, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Toy, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Toy, String> orderDateCol = new TableColumn<>("Order Date");
        orderDateCol.setCellValueFactory(new PropertyValueFactory<>("dateOrder"));
        orderDateCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null); 
                } else if (item == null || item.trim().isEmpty()) {
                    setText("N/A");
                } else {
                    setText(item);
                }
            }
        });

        TableColumn<Toy, String> receiveDateCol = new TableColumn<>("Receive Date");
        receiveDateCol.setCellValueFactory(new PropertyValueFactory<>("dateReceive"));
        receiveDateCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null); 
                } else if (item == null || item.trim().isEmpty()) {
                    setText("N/A");
                } else {
                    setText(item);
                }
            }
        });

        TableColumn<Toy, String> brandCol = new TableColumn<>("Brand");
        brandCol.setCellValueFactory(new PropertyValueFactory<>("brandName"));

        TableColumn<Toy, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Toy, String> supplierCol = new TableColumn<>("Supplier");
        supplierCol.setCellValueFactory(new PropertyValueFactory<>("supplier"));

        TableColumn<Toy, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<Toy, Double> downpaymentCol = new TableColumn<>("Downpayment");
        downpaymentCol.setCellValueFactory(new PropertyValueFactory<>("downpayment"));

        TableColumn<Toy, Double> discountCol = new TableColumn<>("Discount");
        discountCol.setCellValueFactory(new PropertyValueFactory<>("discount"));

        TableColumn<Toy, Double> balanceCol = new TableColumn<>("Balance");
        balanceCol.setCellValueFactory(new PropertyValueFactory<>("balance"));

        TableColumn<Toy, String> paidCol = new TableColumn<>("Fully Paid");
        paidCol.setCellValueFactory(new PropertyValueFactory<>("fullyPaid"));

        TableColumn<Toy, String> barcodeCol = new TableColumn<>("Barcode");
        barcodeCol.setCellValueFactory(new PropertyValueFactory<>("barcode"));

        tableView.getColumns().addAll(
            idCol, nameCol, orderDateCol, receiveDateCol, brandCol, categoryCol,
            supplierCol, amountCol, downpaymentCol, discountCol, balanceCol, paidCol, barcodeCol
        );
        
        TableColumn<Toy, Void> deleteCol = new TableColumn<>("Delete");
        deleteCol.setCellFactory(col -> new TableCell<>() {
            protected final Button deleteBtn = new Button("🗑");

            {
                deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: red; -fx-font-size: 12px;");
                deleteBtn.setOnAction(e -> {
                    Toy toy = getTableView().getItems().get(getIndex());
                    int id = toy.getId();

                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Confirm Delete");
                    confirm.setHeaderText("Delete toy: " + toy.getName() + "?");
                    confirm.setContentText("Are you sure you want to delete this toy? This action cannot be undone.");

                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            boolean success = deleteToy(id);
                            if (success) {
                                getTableView().getItems().remove(toy);
                                showAlert(Alert.AlertType.INFORMATION, "Deleted", "Toy deleted successfully.");
                            } else {
                                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete toy.");
                            }
                        }
                    });
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteBtn);
                }
            }
        });
        tableView.getColumns().add(deleteCol);
        
        //Import Export Buttons
        ImportCSV importCSV = new ImportCSV();
        Button imp = new Button("Import from CSV");
        imp.setId("ImpID");
        imp.setPrefSize(180, 40);

        imp.setOnAction(e -> {
        try {
            importCSV.importFromCSV((Stage) imp.getScene().getWindow(), tableView);
        } catch (CsvException a) {
            a.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Import Error");
            alert.setContentText("There was an error reading the CSV file.");
            alert.showAndWait();
        }  
        });
        
        Button exp = new Button("Export to CSV");
        exp.setId("ExpID");
        exp.setPrefSize(180, 40);
        
        exp.setOnAction((ActionEvent event) -> {
            ExportCSV exportCSV = new ExportCSV();
            exportCSV.exportToCSV((Stage) exp.getScene().getWindow());  
        });
        
        HBox buttonBox2 = new HBox(20); 
        buttonBox2.setAlignment(Pos.CENTER);
        buttonBox2.getChildren().addAll(imp, exp);
        
        // Add components to the main VBox
        tablePage.getChildren().addAll(tableLabel, buttonBox1, filterBarWrapper, tableView, buttonBox2);

        rootLayout.setCenter(tablePage);
        loadToysIntoTable(tableView);
    }
    protected void showStatsPage(){
        Label header = new Label("Statistics");
        header.setId("stat-header");
        
        //Summary Statistics
        Label subHeader = new Label("Basic Summary");
        subHeader.setId("summaryHead");
        subHeader.setAlignment(Pos.CENTER);           // Aligns content inside the label
        subHeader.setMaxWidth(Double.MAX_VALUE);      // Makes it stretch to fill horizontal space
        
        Separator line = new Separator();
        line.setPrefWidth(400); // Optional: adjust width to fit your layout

        Label totalToys = new Label("Total Number of Toys: " + displayStats.getTotalToys());
        totalToys.setId("summary");
        Label totalSpent = new Label("Total Amount Spent: PHP" + displayStats.getTotalAmount());
        totalSpent.setId("summary");
        Label mostExpensive = new Label("Most Expensive Toy: " + displayStats.getMostExpensive());
        mostExpensive.setId("summary");
        Label topSupplier = new Label("Top Supplier: " + displayStats.getTopSupplier());
        topSupplier.setId("summary");
        
        // Spending Trend Chart (Amount)
        CategoryAxis xAxis3 = new CategoryAxis();
        xAxis3.setLabel("Month");
        NumberAxis yAxis3 = new NumberAxis();
        yAxis3.setLabel("Gross Amount");
        
        LineChart<String, Number> amountChart = new LineChart<>(xAxis3, yAxis3);
        amountChart.setLegendVisible(false);
        amountChart.setTitle("Gross Amount Spending Trend");
        XYChart.Series<String,Number> series3 = new XYChart.Series<>();
        Map<String, Double> grossSpending = displayStats.getGrossSpending();
        
        List<String> sortedKeys3 = new ArrayList<>(grossSpending.keySet());
        Collections.sort(sortedKeys3, Comparator.comparing(s -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
            return LocalDate.parse("01 " + s, DateTimeFormatter.ofPattern("dd MMM yyyy"));
        }));
        for (String key : sortedKeys3) {
            double total = grossSpending.get(key);
            series3.getData().add(new XYChart.Data<>(key, total));
        }
        
        amountChart.getData().add(series3);
        amountChart.setMinHeight(300);
        amountChart.setMinWidth(500);
        
        // Spending Trend Chart (Balance) 
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Month");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Spending");

        LineChart<String, Number> spendingChart = new LineChart<>(xAxis, yAxis);
        spendingChart.setLegendVisible(false);
        spendingChart.setTitle("Remaining Balance Trend");
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        Map<String, Double> monthlySpending = displayStats.getMonthlySpending();

        // To control month order manually (since map is unordered)
        List<String> sortedKeys = new ArrayList<>(monthlySpending.keySet());
        Collections.sort(sortedKeys, Comparator.comparing(s -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
            return LocalDate.parse("01 " + s, DateTimeFormatter.ofPattern("dd MMM yyyy"));
        }));
        for (String key : sortedKeys) {
            double total = monthlySpending.get(key);
            series.getData().add(new XYChart.Data<>(key, total));
        }

        spendingChart.getData().add(series);
        spendingChart.setMinHeight(300); // optional
        spendingChart.setMinWidth(500);  // optional
        
        //Bar chart
        CategoryAxis xAxis2 = new CategoryAxis();
        xAxis2.setLabel("Month");
        NumberAxis yAxis2 = new NumberAxis();
        yAxis2.setLabel("No. of Orders");
        
        BarChart <String, Number> orderChart = new BarChart<String, Number>(xAxis2, yAxis2);
        orderChart.setLegendVisible(false);
        orderChart.setTitle("Order Frequency");
        XYChart.Series<String,Number> series2 = new XYChart.Series<>();
        Map<String, Integer> orderFrequency = displayStats.getNumOrders();
        
        List<String> sortedKeys2 = new ArrayList<>(orderFrequency.keySet());
        Collections.sort(sortedKeys2, Comparator.comparing(s -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
            return LocalDate.parse("01 " + s, DateTimeFormatter.ofPattern("dd MMM yyyy"));
        }));
        for (String key2 : sortedKeys2) {
            double total2 = orderFrequency.get(key2);
            series2.getData().add(new XYChart.Data<>(key2, total2));
        }
        
        orderChart.getData().add(series2);
        orderChart.setMinHeight(300);
        orderChart.setMinWidth(500);
        
        // Pie Chart
        PieChart paid = new PieChart();
        paid.setLegendVisible(false);
        paid.setTitle("Fully Paid vs Not Fully Paid");

        Map<String, Integer> statusCounts = displayStats.getPaymentStatus();
        int total = statusCounts.values().stream().mapToInt(Integer::intValue).sum();

        for (Map.Entry<String, Integer> entry : statusCounts.entrySet()) {
            String status = entry.getKey();
            int count = entry.getValue();
            String label = status.equals("YES") ? "Fully Paid" : "Not Fully Paid";
            double percentage = (count * 100.0) / total;

            PieChart.Data slice = new PieChart.Data(label, count);
            paid.getData().add(slice);

            // Tooltip
            Tooltip tooltip = new Tooltip(String.format("%s: %d (%.1f%%)", label, count, percentage));
            tooltip.setShowDelay(Duration.millis(100));

            // Apply style and tooltip after node is ready
            Platform.runLater(() -> {
                Node node = slice.getNode();
                Tooltip.install(node, tooltip);

                // You can customize color here
                if (label.equals("Fully Paid")) {
                    node.setStyle("-fx-pie-color: #84CC16;");
                } else {
                    node.setStyle("-fx-pie-color: #D32F2F;");
                }
            });
        }
        //4 containers for the statistics
        VBox con1 = new VBox(subHeader, line, totalToys, totalSpent, mostExpensive, topSupplier);
        con1.setId("box1");
        VBox con2 = new VBox(spendingChart);
        con2.setId("box2");
        VBox con3 = new VBox(orderChart);
        con3.setId("box3");
        VBox con4 = new VBox(paid);
        con4.setId("box4");
        VBox con5 = new VBox(amountChart);
        con5.setId("box5");
        
        //Contact Labels
        Label one = new Label("Version 1.0.0 \t Need Some Inquiries? Want to Suggest Improvements? \n");
        one.setId("contact-head");
        Label two = new Label("Email: charlesque404@gmail.com");
        two.setId("email");
        Label three = new Label("Facebook: https://www.facebook.com/kyan.so4");
        three.setId("fb");
        Label four = new Label("LinkedIn: https://www.linkedin.com/in/kyan-so/");
        four.setId("linkedin");
        
        VBox contact = new VBox();
        contact.setStyle("-fx-background-color: #383535; -fx-background-radius: 5px;");
        contact.setAlignment(Pos.CENTER);
        contact.getChildren().addAll(one,two,three,four);
        
        VBox mainContainer = new VBox();
        mainContainer.setId("main");
        mainContainer.getChildren().addAll(header,con1, con5, con2,con3,con4,contact);
        mainContainer.setSpacing(10); // optional: for spacing between sections
        mainContainer.setPadding(new Insets(10));
        
        StackPane centerPane = new StackPane(mainContainer);
        centerPane.setAlignment(Pos.CENTER);
        centerPane.setStyle("-fx-background-color: #D6931C;");
        rootLayout.setCenter(centerPane);
    }
    public static void main(String[] args) {
        launch(args);
    }
    //Add Form when clicked
    protected void openAddForm(TableView<Toy> tableView) {
        Stage formStage = new Stage();
        
        TextField nameField = new TextField();
        TextField brandField = new TextField();
        TextField categoryField = new TextField();
        TextField supplierField = new TextField();
        TextField amountField = new TextField();
        TextField downpaymentField = new TextField();
        TextField discountField = new TextField("0.05"); // Default discount
        TextField barcodeField = new TextField();

        DatePicker dateOrderPicker = new DatePicker();
        DatePicker dateReceivePicker = new DatePicker();

        ComboBox<String> paidBox = new ComboBox<>();
        paidBox.getItems().addAll("YES", "NO");
        paidBox.setValue("NO");

        // Image Upload
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Toy Image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Button uploadImageButton = new Button("Upload Image");
        ImageView previewImageView = new ImageView();
        previewImageView.setFitWidth(100);
        previewImageView.setFitHeight(100);
        previewImageView.setPreserveRatio(true);

        final String[] imagePath = {null};

        uploadImageButton.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                imagePath[0] = selectedFile.getAbsolutePath();
                Image image = new Image(selectedFile.toURI().toString());
                previewImageView.setImage(image);
            }
        });

        // Submit Button
        Button submitButton = new Button("Add Toy");

        submitButton.setOnAction(e -> {
            try {
                String name = nameField.getText();
                String brand = brandField.getText();
                String category = categoryField.getText();
                String supplier = supplierField.getText();
                double amount = Double.parseDouble(amountField.getText());
                double downpayment = Double.parseDouble(downpaymentField.getText());
                double discount = Double.parseDouble(discountField.getText());
                double balance = amount - (downpayment + (amount * discount));
                String barcode = barcodeField.getText();
                String dateOrder = dateOrderPicker.getValue() != null ? dateOrderPicker.getValue().toString() : "";
                String dateReceive = dateReceivePicker.getValue() != null ? dateReceivePicker.getValue().toString() : "";
                String paid = paidBox.getValue();
                String imgPath = imagePath[0]; // Can be null if user didn't upload

                Toy toy = new Toy(0, name, dateOrder, dateReceive, brand, category, supplier,
                        amount, downpayment, discount, balance, paid, barcode, imgPath);

                boolean success = DatabaseHelper.addToy(toy);
                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Toy added successfully!");
                    alert.showAndWait();

                    // Optional: fetch latest added toy from database (safer way)
                    Toy latestToy = DatabaseHelper.getLastInsertedToy(); // You need to make this
                    tableView.getItems().add(latestToy); // Adds to the table

                    formStage.close(); // Close the add form
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to add toy.");
                    alert.showAndWait();
                }
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid input: " + ex.getMessage());
                alert.showAndWait();
            }
        });
        // Layout
        VBox formLayout = new VBox(10,
            new Label("Name"), nameField,
            new Label("Brand"), brandField,
            new Label("Category"), categoryField,
            new Label("Supplier"), supplierField,
            new Label("Amount"), amountField,
            new Label("Downpayment"), downpaymentField,
            new Label("Discount"), discountField,
            new Label("Barcode"), barcodeField,
            new Label("Order Date"), dateOrderPicker,
            new Label("Receive Date"), dateReceivePicker,
            new Label("Fully Paid"), paidBox,
            new Label("Image"), uploadImageButton, previewImageView,
            submitButton
        );
        formLayout.setPadding(new Insets(20));
        formLayout.setAlignment(Pos.CENTER_LEFT);

        ScrollPane scrollPane = new ScrollPane(formLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        formStage.setTitle("Add New Toy");
        formStage.setScene(new Scene(scrollPane, 400, 600));
        formStage.show();
    }
    //Edit Form when clicked
    protected void openEditForm(TableView<Toy> tableView) {
        Stage stage = new Stage();
        VBox editForm = new VBox(10);
        editForm.setPadding(new Insets(15));

        Label id = new Label("Enter ID of toy to edit:");
        TextField idField = new TextField();
        idField.setPromptText("Enter here");

        Button editBtn = new Button("Edit");

        editForm.getChildren().addAll(id, idField, editBtn);
        editForm.setAlignment(Pos.CENTER);
        Scene scene = new Scene(editForm);
        stage.setScene(scene);
        stage.setTitle("Edit Toy Details");
        stage.show();

        editBtn.setOnAction(e -> {
            try {
                int toyId = Integer.parseInt(idField.getText());
                Toy toy = DatabaseHelper.getToyById(toyId); 
                if (toy == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Toy not found.");
                    alert.showAndWait();
                    return;
                }

                // New stage for edit form
                Stage editStage = new Stage();
                VBox form = new VBox(10);
                form.setPadding(new Insets(15));

                TextField nameField = new TextField(toy.getName());
                TextField brandField = new TextField(toy.getBrandName());
                TextField categoryField = new TextField(toy.getCategory());
                TextField supplierField = new TextField(toy.getSupplier());
                TextField amountField = new TextField(String.valueOf(toy.getAmount()));
                TextField downpaymentField = new TextField(String.valueOf(toy.getDownpayment()));
                TextField discountField = new TextField(String.valueOf(toy.getDiscount()));
                TextField barcodeField = new TextField(toy.getBarcode());

                DatePicker dateOrderPicker = new DatePicker(safeParseDate(toy.getDateOrder()));
                DatePicker dateReceivePicker = new DatePicker(safeParseDate(toy.getDateReceive()));

                ComboBox<String> paidBox = new ComboBox<>();
                paidBox.getItems().addAll("YES", "NO");
                paidBox.setValue(toy.getFullyPaid());
                
                //Image Handling
                ImageView imageView = new ImageView();
                imageView.setFitWidth(120);
                imageView.setFitHeight(120);
                imageView.setPreserveRatio(true);

                if (toy.getImagePath() != null && !toy.getImagePath().isEmpty()) {
                    File imageFile = new File(toy.getImagePath());
                    if (imageFile.exists()) {
                        imageView.setImage(new Image(imageFile.toURI().toString()));
                    }
                }

                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choose New Toy Image");
                fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
                );

                Button changeImageBtn = new Button("Change Image");
                final String[] newImagePath = {toy.getImagePath()}; // preserve existing image

                changeImageBtn.setOnAction(changeEvent -> {
                    File selected = fileChooser.showOpenDialog(editStage);
                    if (selected != null) {
                        newImagePath[0] = selected.getAbsolutePath();
                        imageView.setImage(new Image(selected.toURI().toString()));
                    }
                });

                Button saveBtn = new Button("Save Changes");

                saveBtn.setOnAction(ev -> {
                    try {
                        toy.setName(nameField.getText());
                        toy.setBrandName(brandField.getText());
                        toy.setCategory(categoryField.getText());
                        toy.setSupplier(supplierField.getText());
                        toy.setAmount(Double.parseDouble(amountField.getText()));
                        toy.setDownpayment(Double.parseDouble(downpaymentField.getText()));
                        toy.setDiscount(Double.parseDouble(discountField.getText()));
                        toy.setBarcode(barcodeField.getText());
                        toy.setDateOrder(dateOrderPicker.getValue().toString());
                        toy.setDateReceive(dateReceivePicker.getValue().toString());
                        toy.setFullyPaid(paidBox.getValue());

                        double balance = toy.getAmount() - (toy.getDownpayment() + toy.getAmount() * toy.getDiscount());
                        toy.setBalance(balance);
                        toy.setImagePath(newImagePath[0]);

                        boolean updated = DatabaseHelper.updateToy(toy);
                        if (updated) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Toy updated!");
                            alert.showAndWait();

                            // Refresh table (reload all or update specific row)
                            tableView.getItems().clear();
                            tableView.getItems().addAll(DatabaseHelper.getAllToys());

                            editStage.close();
                            stage.close();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR, "Update failed.");
                            alert.showAndWait();
                        }
                    } catch (Exception ex) {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid input: " + ex.getMessage());
                        alert.showAndWait();
                    }
                });

                form.getChildren().addAll(
                    new Label("Name"), nameField,
                    new Label("Brand"), brandField,
                    new Label("Category"), categoryField,
                    new Label("Supplier"), supplierField,
                    new Label("Amount"), amountField,
                    new Label("Downpayment"), downpaymentField,
                    new Label("Discount"), discountField,
                    new Label("Barcode"), barcodeField,
                    new Label("Order Date"), dateOrderPicker,
                    new Label("Receive Date"), dateReceivePicker,
                    new Label("Fully Paid"), paidBox,
                    new Label("Current Image"), imageView, changeImageBtn,
                    saveBtn
                );
                
                ScrollPane scrollPane = new ScrollPane(form);
                scrollPane.setFitToWidth(true);
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
                
                Scene editScene = new Scene(scrollPane, 400, 600);
                editStage.setScene(editScene);
                editStage.setTitle("Edit Toy");
                editStage.show();

            } catch (NumberFormatException nfe) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid ID input.");
                alert.showAndWait();
            }
        });
    }
    //Delete All Form when clicked
    public static void openDeleteForm(Toynado app) {
        Stage stage = new Stage();
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        Label confirm = new Label("Are you sure you want to delete all?");
        
        Button noDelete = new Button("No");
        noDelete.setOnAction(e -> stage.close());
        //noDelete.setId("noDelete");
        Button yesDelete = new Button("Yes");
        //yesDelete.setId("yesDelete");
        root.getChildren().addAll(confirm, noDelete, yesDelete);
        
        yesDelete.setOnAction(e -> { 
            try {
                DatabaseHelper.deleteAll();
                stage.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        Scene scene = new Scene(root);
        root.setAlignment(Pos.CENTER);
        stage.setScene(scene);
        stage.setTitle("Delete Toy");
        stage.show();
    }
    //alert box when delete
    protected static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    //alert box when exit app
    protected void showExitConfirmation(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Confirmation");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("Choose 'Yes' to exit or 'No' to stay.");

        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yesButton, noButton);

        // Show the dialog and capture response
        alert.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                stage.close(); 
            }
        });
    }
    //Display data in table
    protected void loadToysIntoTable(TableView<Toy> tableView) {
        ObservableList<Toy> toys = FXCollections.observableArrayList();

        String sql = "SELECT * FROM toys";

        try (Connection conn = DatabaseHelper.connect();
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
            e.printStackTrace();
        }
        tableView.setItems(toys);
        
        tableView.setRowFactory(tv -> {
            TableRow<Toy> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    Toy clickedToy = row.getItem();
                    showToyDetails(clickedToy);
                }
            });
            return row;
        });
    }
    //Show Toy Details when clicked
    protected void showToyDetails(Toy toy) {
        Label nameLabel = new Label("Name: " + toy.getName());
        Label brandLabel = new Label("Brand: " + toy.getBrandName());
        Label categoryLabel = new Label("Category: " + toy.getCategory());
        Label supplierLabel = new Label("Supplier: " + toy.getSupplier());
        Label amountLabel = new Label("Amount: " + toy.getAmount());
        Label downpaymentLabel = new Label("Downpayment: " + toy.getDownpayment());
        Label discountLabel = new Label("Discount: " + toy.getDiscount());
        Label balanceLabel = new Label("Balance: " + toy.getBalance());
        Label paidLabel = new Label("Fully Paid: " + toy.getFullyPaid());
        Label orderDateLabel = new Label("Order Date: " + toy.getDateOrder());
        Label receiveDateLabel = new Label("Receive Date: " + toy.getDateReceive());
        Label barcodeLabel = new Label("Barcode: " + toy.getBarcode());

        // Image
        ImageView toyImage = new ImageView();
        try {
            String imagePath = toy.getImagePath(); // Get the saved image path from the object

            if (imagePath != null && !imagePath.isEmpty()) {
                // Convert backslashes to forward slashes
                imagePath = imagePath.replace("\\", "/");
                Path path = Paths.get(imagePath).toAbsolutePath();
                Image image = new Image(path.toUri().toString());

                if (!image.isError()) {
                    toyImage.setImage(image);
                    toyImage.setFitHeight(200);
                    toyImage.setPreserveRatio(true);
                } else {
                    System.out.println("Error loading image at: " + path);
                }
            } else {
                System.out.println("No image path provided.");
            }
        } catch (Exception e) {
            System.out.println("Failed to load image: " + e.getMessage());
        }
        VBox infoBox = new VBox(10,
            nameLabel, brandLabel, categoryLabel, supplierLabel,
            amountLabel, downpaymentLabel, discountLabel, balanceLabel,
            paidLabel, orderDateLabel, receiveDateLabel, barcodeLabel
        );
        
        Label imglabel = new Label("Image:");
        VBox imageBox = new VBox(imglabel, toyImage);
        
        HBox content = new HBox(20, infoBox, imageBox);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER_LEFT);

        Scene scene = new Scene(content, 650, 375);
        Stage detailStage = new Stage();
        detailStage.setTitle("Toy Details");
        detailStage.setScene(scene);
        detailStage.show();
    }
    //Show filtered toy table
    protected void applyFilters(String search, String supplier, String brand, String category,
                          LocalDate orderDateFrom, LocalDate orderDateTo, boolean paid, boolean unpaid) {
        ObservableList<Toy> filteredToys = FXCollections.observableArrayList();

        StringBuilder sql = new StringBuilder("SELECT * FROM toys WHERE 1=1");

        // Apply filters based on user input
        if (!search.isEmpty()) {
            sql.append(" AND name LIKE '%").append(search).append("%'");
        }
        if (!supplier.isEmpty()) {
            sql.append(" AND supplier LIKE '%").append(supplier).append("%'");
        }
        if (!brand.isEmpty()) {
            sql.append(" AND brand_name LIKE '%").append(brand).append("%'");
        }
        if (!category.isEmpty()) {
            sql.append(" AND category LIKE '%").append(category).append("%'");
        }
        if (orderDateFrom != null) {
            sql.append(" AND date_order >= '").append(orderDateFrom).append("'");
        }
        if (orderDateTo != null) {
            sql.append(" AND date_order <= '").append(orderDateTo).append("'");
        }
        if (paid && !unpaid) {
            sql.append(" AND fully_paid = 'YES'");
        } else if (unpaid && !paid) {
            sql.append(" AND fully_paid = 'NO'");
        }

        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql.toString())) {

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
                filteredToys.add(toy);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        tableView.setItems(filteredToys); // Update table view with filtered toys
    }
    //Parse Missing Dates
    private LocalDate safeParseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank() || dateStr.equalsIgnoreCase("N/A")) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr); // Or use a custom formatter if needed
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}