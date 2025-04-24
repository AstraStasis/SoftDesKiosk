package com.example.softdeskiosk;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class BookController {
    @FXML
    private ListView<String> bookListView;
    private Kiosk kiosk;

    public void setKiosk(Kiosk kiosk) {
        this.kiosk = kiosk;
    }

    @FXML
    public void initialize() {
        bookListView.getItems().addAll(
                "Math Textbook - $50",
                "Science Guide - $40",
                "History Book - $35"
        );
    }

    // Remove the ActionEvent parameter
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
}