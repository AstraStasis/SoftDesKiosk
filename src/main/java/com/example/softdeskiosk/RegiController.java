package com.example.softdeskiosk;

import javafx.fxml.FXML;

public class RegiController {
    private Kiosk kiosk;

    /** Called by Kiosk when loading this scene */
    public void setKiosk(Kiosk kiosk) {
        this.kiosk = kiosk;
    }

    /** Back button handler */
    @FXML
    private void handleBackToMainMenu() {
        try {
            kiosk.showMainMenu();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
