import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

public class Controller {

    @FXML private TextField txtNumero;
    @FXML private TableView<Habitacion> tablaHabitaciones;
    @FXML private TableColumn<Habitacion, Integer> colNumero;
    @FXML private TableColumn<Habitacion, String> colTipo;
    @FXML private TableColumn<Habitacion, Integer> colCapacidad;
    @FXML private TableColumn<Habitacion, Double> colPrecio;
    @FXML private TableColumn<Habitacion, String> colEstado;

    // Lista con TODAS las habitaciones del XML
    private ObservableList<Habitacion> todas = FXCollections.observableArrayList();

    // Lista que se muestra en la tabla (todas o solo la buscada)
    private ObservableList<Habitacion> visibles = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Relacionamos cada columna con un atributo de la clase Habitacion
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colCapacidad.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tablaHabitaciones.setItems(visibles);
        cargarDatosXML();
    }

    private void cargarDatosXML() {
        try {
            InputStream is = getClass().getResourceAsStream("/reservas.xml");
            if (is == null) {
                mostrarAlerta("Error", "No se encontró reservas.xml en resources.");
                return;
            }

            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();

            NodeList lista = doc.getElementsByTagName("habitacion");

            for (int i = 0; i < lista.getLength(); i++) {
                Element e = (Element) lista.item(i);

                int numero = Integer.parseInt(leer(e, "numero"));
                String tipo = leer(e, "tipo");
                int capacidad = Integer.parseInt(leer(e, "capacidad"));

                // El precio puede llamarse de varias formas en el XML
                String textoPrecio = leer(e, "precioNoche");
                if (textoPrecio.isEmpty()) {
                    textoPrecio = leer(e, "precio_noche");
                }
                double precio = 0;
                if (!textoPrecio.isEmpty()) {
                    precio = Double.parseDouble(textoPrecio);
                }

                String estado = leer(e, "estado");

                todas.add(new Habitacion(numero, tipo, capacidad, precio, estado));
            }

            // Al empezar mostramos todas
            visibles.setAll(todas);

        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarAlerta("Error", "Error al leer el archivo XML.");
        }
    }

    @FXML
    private void buscarHabitacion() {
        String texto = txtNumero.getText().trim();

        // Si el campo está vacío, mostramos todas otra vez
        if (texto.isEmpty()) {
            visibles.setAll(todas);
            return;
        }

        // Buscamos la habitación con ese número
        for (Habitacion h : todas) {
            if (String.valueOf(h.getNumero()).equals(texto)) {
                visibles.clear();
                visibles.add(h);   // solo mostramos la buscada
                return;
            }
        }

        // Si llegamos aquí, no se ha encontrado
        visibles.clear();
        mostrarAlerta("Aviso", "La habitación " + texto + " NO EXISTE.");
    }

    // Lee un dato del XML: primero como atributo y si no existe, como etiqueta hija
    private String leer(Element e, String nombre) {
        if (e.hasAttribute(nombre)) {
            return e.getAttribute(nombre).trim();
        }
        NodeList hijos = e.getElementsByTagName(nombre);
        if (hijos.getLength() > 0) {
            return hijos.item(0).getTextContent().trim();
        }
        return "";
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}