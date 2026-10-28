public class Habitacion {
    private final int numero;
    private final String tipo;
    private final int capacidad;
    private final double precio;
    private final String estado;

    public Habitacion(int numero, String tipo, int capacidad, double precio, String estado) {
        this.numero = numero;
        this.tipo = tipo;
        this.capacidad = capacidad;
        this.precio = precio;
        this.estado = estado;
    }

    public int getNumero() { return numero; }
    public String getTipo() { return tipo; }
    public int getCapacidad() { return capacidad; }
    public double getPrecio() { return precio; }
    public String getEstado() { return estado; }
}
