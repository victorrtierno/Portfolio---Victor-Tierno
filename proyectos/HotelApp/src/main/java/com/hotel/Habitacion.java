package com.hotel;

public class Habitacion {
    private final String numero;
    private final String estado;
    private final String tipo;
    private final int capacidad;
    private final double precioNoche;

    public Habitacion(String numero, String estado, String tipo, int capacidad, double precioNoche) {
        this.numero = numero;
        this.estado = estado;
        this.tipo = tipo;
        this.capacidad = capacidad;
        this.precioNoche = precioNoche;
    }

    public String getNumero() { 
        return numero; 
    }
    public String getEstado() { 
        return estado; 
    }
    public String getTipo() { 
        return tipo; 
    }
    public int getCapacidad() { 
        return capacidad; 
    }
    public double getPrecioNoche() { 
        return precioNoche; 
    }
}
