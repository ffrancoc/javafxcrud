package com.github.ffrancoc.crudapp;

import com.github.ffrancoc.crudapp.models.Producto;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class PrimaryController implements Initializable {

    @FXML
    private HBox tableAction;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtCantidad;

    @FXML
    private TextField txtPrecio;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnActualizar;

    @FXML
    private Button btnCancelar;

    @FXML
    private TextField txtBuscar;

    @FXML
    private TableView<Producto> tablaProducto;

    @FXML
    private TableColumn<Producto, Integer> colID;

    @FXML
    private TableColumn<Producto, String> colNombre;

    @FXML
    private TableColumn<Producto, Integer> colCantidad;

    @FXML
    private TableColumn<Producto, Double> colPrecio;

    @FXML
    private void onActionCreate() throws IOException {
        String nombre = txtNombre.getText().trim();
        int cantidad = Integer.parseInt(txtCantidad.getText());
        double precio = Double.valueOf(txtPrecio.getText());

        if (nombre.length() > 0) {
            boolean estatus = Conexion.postProducto(new Producto(nombre, cantidad, precio));
            if (estatus) {
                clearFields();
                fillTablaProducto();
            }
        }
    }

    @FXML
    private void onActionActualizar() {
        int id = Integer.parseInt(txtId.getText());
        String nombre = txtNombre.getText().trim();
        int cantidad = Integer.parseInt(txtCantidad.getText());
        double precio = Double.valueOf(txtPrecio.getText());

        if (nombre.length() > 0) {
            boolean estatus = Conexion.patchProducto(new Producto(id, nombre, cantidad, precio));
            if (estatus) {
                fillTablaProducto();
                clearFields();
                editMode(false);
            }
        }
    }

    @FXML
    private void onActionCancelar() {
        clearFields();
        editMode(false);
    }

    @FXML
    private void onActionRefrescar() {
        fillTablaProducto();
    }

    @FXML
    private void onActionEliminar() {
        Producto producto = tablaProducto.getSelectionModel().getSelectedItem();
        if (producto != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "El producto " + producto.getNombre() + " se eliminara, desea continuar?", ButtonType.CANCEL, ButtonType.OK);
            Optional<ButtonType> op = alert.showAndWait();

            if (op.get().equals(ButtonType.OK)) {
                boolean estatus = Conexion.deleteProducto(producto.getId());
                if (estatus) {
                    fillTablaProducto();
                }
            }
        }
    }

    @FXML
    private void onActionEditar() throws IOException {
        Producto producto = tablaProducto.getSelectionModel().getSelectedItem();
        if (producto != null) {
            txtId.setText(producto.getId().toString());
            txtNombre.setText(producto.getNombre());
            txtCantidad.setText(producto.getCantidad().toString());
            txtPrecio.setText(producto.getPrecio().toString());

            editMode(true);
        }
    }

    private void clearFields() {
        txtId.clear();
        txtNombre.clear();
        txtCantidad.clear();
        txtPrecio.clear();
    }

    private void editMode(boolean editar) {
        if (editar) {
            btnGuardar.setDisable(true);
            btnActualizar.setDisable(false);
            btnCancelar.setDisable(false);
            tableAction.setDisable(true);
        } else {
            btnGuardar.setDisable(false);
            btnActualizar.setDisable(true);
            btnCancelar.setDisable(true);
            tableAction.setDisable(false);
        }
    }

    private void fillTablaProducto() {
        tablaProducto.getItems().clear();

        var productos = Conexion.getProductos();
        tablaProducto.setItems(productos);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        UnaryOperator<Change> integerFilter = change -> {
            String input = change.getText();
            if (input.matches("[0-9]*")) {
                return change;
            }
            return null;
        };

        txtCantidad.setTextFormatter(new TextFormatter<>(integerFilter));

        UnaryOperator<Change> decimalFilter = change -> {
            String input = change.getText();
            if (input.matches("[0-9]*\\.?[0-9]{0,2}")) {
                return change;
            }
            return null;
        };

        txtPrecio.setTextFormatter(new TextFormatter<>(decimalFilter));

        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        fillTablaProducto();

        
        ObservableList data =  tablaProducto.getItems();
        txtBuscar.textProperty().addListener((ObservableValue<? extends String> ov, String oldValue, String newValue) -> {
            if (oldValue != null && (newValue.length() < oldValue.length())) {
                tablaProducto.setItems(data);
            }
            String value = newValue.toLowerCase();
            ObservableList<Producto> subentries = FXCollections.observableArrayList();

            long count = tablaProducto.getColumns().stream().count();
            for (int i = 0; i < tablaProducto.getItems().size(); i++) {
                for (int j = 0; j < count; j++) {
                    String entry = "" + tablaProducto.getColumns().get(j).getCellData(i);
                    if (entry.toLowerCase().contains(value)) {
                        subentries.add(tablaProducto.getItems().get(i));
                        break;
                    }
                }
            }
            tablaProducto.setItems(subentries);
        });
    }

}
