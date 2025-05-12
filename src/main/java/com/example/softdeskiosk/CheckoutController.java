package com.example.softdeskiosk;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CheckoutController {

    @FXML private TableView<Item>    cartTable;
    @FXML private TableColumn<Item,String> itemColumn;
    @FXML private TableColumn<Item,Integer> qtyColumn;
    @FXML private TableColumn<Item,Double> priceColumn;

    private final ObservableList<Item> cartItems = FXCollections.observableArrayList();
    private Kiosk kiosk;

    public void setKiosk(Kiosk kiosk) {
        this.kiosk = kiosk;
    }

    @FXML
    public void initialize() {
        // configure columns
        itemColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        qtyColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        cartTable.setEditable(false);
        itemColumn.setReorderable(false);
        qtyColumn.setReorderable(false);
        priceColumn.setReorderable(false);


        loadCartFromCSV();
        cartTable.setItems(cartItems);
    }

    private void loadCartFromCSV() {
        String csvFile = "temp.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] f = line.split(",", 3);
                String name = f[1].trim();
                double price = Double.parseDouble(f[2].trim());
                addItem(name, price);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addItem(String name, double price) {
        // aggregate quantities
        for (Item it : cartItems) {
            if (it.getItemName().equals(name)) {
                it.incrementQuantity();
                return;
            }
        }
        cartItems.add(new Item(name, 1, price));
    }

    @FXML
    private void handleBackToMainMenu() {
        try {
            if (kiosk != null) {
                kiosk.showMainMenu();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Inner data class
    public static class Item {
        private final String itemName;
        private int quantity;
        private final double price;

        public Item(String itemName, int quantity, double price) {
            this.itemName = itemName;
            this.quantity = quantity;
            this.price = price;
        }
        public String getItemName() { return itemName; }
        public Integer getQuantity() { return quantity; }
        public Double getPrice()    { return price; }
        public void incrementQuantity() { this.quantity++; }
    }
}
