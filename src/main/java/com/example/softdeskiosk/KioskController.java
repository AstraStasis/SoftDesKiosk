package com.example.softdeskiosk;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class KioskController {
    private Kiosk kiosk;

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
}