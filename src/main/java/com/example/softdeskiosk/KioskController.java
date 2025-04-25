package com.example.softdeskiosk;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class KioskController {
    private Kiosk kiosk;

    @FXML
    private TableView<Item> cartTable;
    @FXML
    private TableColumn<Item, String> itemColumn;
    @FXML
    private TableColumn<Item, Integer> quantityColumn;
    @FXML
    private TableColumn<Item, Double> priceColumn;

    private ObservableList<Item> cartItems = FXCollections.observableArrayList();

    public void setKiosk(Kiosk kiosk) {
        this.kiosk = kiosk;
    }

    @FXML
    private void goToBooks() {
        try {
            kiosk.showBookScene();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToUniforms() {
        try {
            kiosk.showUniformScene();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToCheckout() {
        try {
            kiosk.showCheckoutScene();  // Show the checkout scene
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void restartKiosk() {
        try {
            // Logic to restart the kiosk
            kiosk.restart();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This method is called to populate the table with cart items from the CSV file
    public void loadCartFromCSV() {
        String csvFile = "temp.csv"; // Adjust path as needed
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                String itemCode = fields[1].trim();
                double price = Double.parseDouble(fields[2].trim());
                addItemToCart(itemCode, price);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // This method is called to add an item to the cart list and update the table
    private void addItemToCart(String itemName, double price) {
        boolean found = false;
        for (Item item : cartItems) {
            if (item.getItemName().equals(itemName)) {
                item.incrementQuantity();
                found = true;
                break;
            }
        }
        if (!found) {
            cartItems.add(new Item(itemName, 1, price));
        }
        updateCartTable();
    }

    // Updates the table view with the latest cart items
    public void updateCartTable() {
        itemColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        cartTable.setItems(cartItems);
    }

    // Custom Item class without CartItem class
    public static class Item {
        private String itemName;
        private int quantity;
        private double price;

        public Item(String itemName, int quantity, double price) {
            this.itemName = itemName;
            this.quantity = quantity;
            this.price = price;
        }

        public String getItemName() {
            return itemName;
        }

        public int getQuantity() {
            return quantity;
        }

        public void incrementQuantity() {
            this.quantity++;
        }

        public double getPrice() {
            return price;
        }
    }
}
