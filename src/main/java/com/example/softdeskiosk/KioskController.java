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
import java.nio.file.Files;
import java.nio.file.Paths;

public class KioskController {
    private Kiosk kiosk;

    @FXML private TableView<Item> cartTable;
    @FXML private TableColumn<Item, String> itemColumn;
    @FXML private TableColumn<Item, Integer> quantityColumn;
    @FXML private TableColumn<Item, Double> priceColumn;

    private final ObservableList<Item> cartItems = FXCollections.observableArrayList();

    public void setKiosk(Kiosk kiosk) {
        this.kiosk = kiosk;
    }

    /** Called from Kiosk.showMainMenu() */
    public void loadCartFromCSV() {
        cartItems.clear();
        try (BufferedReader br = new BufferedReader(new FileReader("temp.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] f = line.split(",", 3);
                String name = f[1].trim();
                double price = Double.parseDouble(f[2].trim());

                boolean found = false;
                for (Item it : cartItems) {
                    if (it.getItemName().equals(name)) {
                        it.incrementQuantity();
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    cartItems.add(new Item(name, 1, price));
                }
            }
        } catch (IOException ignored) {
            // no temp.csv yet → cart remains empty
        }
    }

    /** Also called from showMainMenu() */
    public void updateCartTable() {
        itemColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        cartTable.setItems(cartItems);
    }

    @FXML private void goToBooks() {
        try { kiosk.showBookScene(); } catch (Exception e) { e.printStackTrace(); }
    }
    @FXML private void goToUniforms() {
        try { kiosk.showUniformScene(); } catch (Exception e) { e.printStackTrace(); }
    }
    @FXML private void goToCheckout() {
        try { kiosk.showCheckoutScene(); } catch (Exception e) { e.printStackTrace(); }
    }
    @FXML private void restartKiosk() {
        try {
            Files.deleteIfExists(Paths.get("temp.csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadCartFromCSV();
        updateCartTable();
    }

    /** Data holder */
    public static class Item {
        private final String itemName;
        private int quantity;
        private final double price;
        public Item(String n,int q,double p){ itemName=n; quantity=q; price=p; }
        public String getItemName(){ return itemName; }
        public Integer getQuantity(){ return quantity; }
        public Double getPrice(){ return price; }
        public void incrementQuantity(){ quantity++; }
    }
}
