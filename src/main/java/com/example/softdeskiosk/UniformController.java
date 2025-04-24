package com.example.softdeskiosk;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;

public class UniformController {
    @FXML
    private TableView<Uniform> uniformTableView;
    @FXML
    private TableColumn<Uniform, String> codeColumn;
    @FXML
    private TableColumn<Uniform, String> nameColumn;
    @FXML
    private TableColumn<Uniform, String> priceColumn;

    private Kiosk kiosk;

    public void setKiosk(Kiosk kiosk) {
        this.kiosk = kiosk;
    }

    String[][] uniforms = {
            {"U001", "Student Uniform", "800.00"},
            {"U002", "PE Uniform", "450.00"},
            {"U003", "Lab Gown", "600.00"},
            {"U004", "Sports Uniform", "750.00"}
    };

    @FXML
    public void initialize() {
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        for (String[] uniformData : uniforms) {
            uniformTableView.getItems().add(new Uniform(uniformData[0], uniformData[1], uniformData[2]));
        }
    }

    @FXML
    public void goBack(ActionEvent event) {
        try {
            kiosk.showMainMenu();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}