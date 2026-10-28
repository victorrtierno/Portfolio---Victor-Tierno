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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

public class Controller {

    @FXML
    private TextField txtNumero;
    @FXML
    private TableView<Habitacion> tablaHabitaciones;
    @FXML
    private TableColumn<Habitacion, Integer> colNumero;
    @FXML
    private TableColumn<Habitacion, String> colTipo;
    @FXML
    private TableColumn<Habitacion, Integer> colCapacidad;
    @FXML
    private TableColumn<Habitacion, Double> colPrecio;
    @FXML
    private TableColumn<Habitacion, String> colEstado;

    private final ObservableList<Habitacion> habitaciones = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colNumero.setCellValueFactory(new PropertyValueFactory<>("numero"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colCapacidad.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tablaHabitaciones.setItems(habitaciones);
        cargarDatosXML();
    }

    private void cargarDatosXML() {
        try (InputStream is = getClass().getResourceAsStream("/reservas.xml")) {
            if (is == null) {
                mostrarAlerta("Error", "No se encontró reservas.xml en resources.");
                return;
            }

            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().parse(is);
            doc.getDocumentElement().normalize();

            NodeList lista = doc.getElementsByTagName("habitacion");
            for (int i = 0; i < lista.getLength(); i++) {
                Element e = (Element) lista.item(i);

                String numero = leer(e, "numero");
                if (numero.isEmpty())
                    continue;

                habitaciones.add(new Habitacion(
                        Integer.parseInt(numero),
                        leer(e, "tipo"),
                        parsearInt(leer(e, "capacidad")),
                        parsearDouble(leer(e, "precio")),
                        leer(e, "estado")));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarAlerta("Error", "Error al leer el archivo XML.");
        }
    }

    @FXML
    private void buscarHabitacion() {
        String texto = txtNumero.getText().trim();
        if (texto.isEmpty()) {
            mostrarAlerta("Aviso", "Por favor, introduce un número de habitación.");
            return;
        }

        for (Habitacion h : habitaciones) {
            if (String.valueOf(h.getNumero()).equals(texto)) {
                tablaHabitaciones.getSelectionModel().select(h);
                tablaHabitaciones.scrollTo(h);
                return;
            }
        }
        tablaHabitaciones.getSelectionModel().clearSelection();
        mostrarAlerta("Aviso", "La habitación " + texto + " NO EXISTE.");
    }

    // Lee el dato como atributo o, si no existe, como etiqueta hija
    private String leer(Element e, String nombre) {
        if (e.hasAttribute(nombre))
            return e.getAttribute(nombre).trim();
        NodeList hijos = e.getElementsByTagName(nombre);
        if (hijos.getLength() > 0) {
            Node n = hijos.item(0);
            return n.getTextContent().trim();
        }
        return "";
    }

    private int parsearInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private double parsearDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}