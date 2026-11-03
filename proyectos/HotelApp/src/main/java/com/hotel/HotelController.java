package com.hotel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

public class HotelController {

    private static final String VERDE   = "#2e9e4f";
    private static final String ROJO    = "#c62828";
    private static final String NARANJA = "#ef6c00";
    private static final String GRIS    = "#757575";

    @FXML private TextField txtNumeroHabitacion;
    @FXML private Circle circulo;
    @FXML private Label lblIcono;
    @FXML private Label lblResultado;   // título: "Habitación disponible", etc.
    @FXML private Label lblTipo;
    @FXML private Label lblPrecio;
    @FXML private Label lblCapacidad;

    private final ObservableList<Habitacion> habitaciones = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        cargarXml();
        limpiarResultado();
    }

    // Lee reservas.xml con DOM y rellena la lista.
    private void cargarXml() {
        try (InputStream in = getClass().getResourceAsStream("/reservas.xml")) {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);

            NodeList nodos = doc.getElementsByTagName("habitacion");
            for (int i = 0; i < nodos.getLength(); i++) {
                Element h = (Element) nodos.item(i);
                habitaciones.add(new Habitacion(
                        h.getAttribute("numero"),
                        h.getAttribute("estado"),
                        texto(h, "tipo"),
                        Integer.parseInt(texto(h, "capacidad")),
                        Double.parseDouble(texto(h, "precioNoche"))));
            }
        } catch (Exception e) {
            mostrarError("Error al leer reservas.xml: " + e.getMessage());
        }
    }

    private String texto(Element padre, String etiqueta) {
        return padre.getElementsByTagName(etiqueta).item(0).getTextContent().trim();
    }

    @FXML
    private void buscar() {
        String numero = txtNumeroHabitacion.getText().trim();
        limpiarResultado();

        if (numero.isEmpty()) {
            mostrarError("Introduce un número de habitación.");
            return;
        }

        for (Habitacion h : habitaciones) {
            if (h.getNumero().equals(numero)) {
                mostrarHabitacion(h);
                return;
            }
        }
        mostrarError("La habitación " + numero + " no existe.");
    }

    private void mostrarHabitacion(Habitacion h) {
        String estado = h.getEstado().trim().toLowerCase();

        String titulo;
        String icono;
        String color;

        switch (estado) {
            case "libre":
                titulo = "Habitación disponible";
                icono  = "✓";
                color  = VERDE;
                break;
            case "ocupada":
                titulo = "Habitación ocupada";
                icono  = "✗";
                color  = ROJO;
                break;
            default:
                titulo = "Habitación no disponible";
                icono  = "✗";
                color  = NARANJA;
                break;
        }

        // Icono y círculo
        lblIcono.setText(icono);
        circulo.setFill(Color.web(color));

        // Título con el estado
        lblResultado.setText(titulo);
        lblResultado.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 18px; -fx-font-weight: bold;");

        // Detalles
        lblTipo.setText("Tipo: " + h.getTipo());
        lblPrecio.setText("Precio: " + formatearPrecio(h.getPrecioNoche()) + " €");
        lblCapacidad.setText("Capacidad: " + h.getCapacidad()
                + (h.getCapacidad() == 1 ? " persona" : " personas"));
    }

    private void mostrarError(String mensaje) {
        lblIcono.setText("?");
        circulo.setFill(Color.web(GRIS));
        lblResultado.setText(mensaje);
        lblResultado.setStyle("-fx-text-fill: " + ROJO + "; -fx-font-size: 16px; -fx-font-weight: bold;");
        lblTipo.setText("");
        lblPrecio.setText("");
        lblCapacidad.setText("");
    }

    private void limpiarResultado() {
        lblIcono.setText("✓");
        circulo.setFill(Color.web(GRIS));
        lblResultado.setText("");
        lblResultado.setStyle("");
        lblTipo.setText("");
        lblPrecio.setText("");
        lblCapacidad.setText("");
    }

    private String formatearPrecio(double precio) {
        return precio == Math.floor(precio)
                ? String.valueOf((int) precio)
                : String.format("%.2f", precio).replace('.', ',');
    }
}