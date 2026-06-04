/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.puntoubi;

/**
 *
 * @author Gabriela
 */

//El Nodo
//Esta clase representa cada ciudad o punto de interés en Guatemala. Almacena las coordenadas WGS84 (latitud, longitud y altura)
public class Ubicacion {
    private final String codigoUnico;
    private final String nombreComercial;
    private final double latitudWGS;
    private final double longitudWGS;
    private final double altitudMetros;

    public Ubicacion(String codigoUnico, String nombreComercial, double latitudWGS, double longitudWGS, double altitudMetros) {
        this.codigoUnico = codigoUnico.toUpperCase().trim();
        this.nombreComercial = nombreComercial;
        this.latitudWGS = latitudWGS;
        this.longitudWGS = longitudWGS;
        this.altitudMetros = altitudMetros;
    }

    public String getCodigoUnico() { return codigoUnico; }
    public String getNombreComercial() { return nombreComercial; }
    public double getLatitudWGS() { return latitudWGS; }
    public double getLongitudWGS() { return longitudWGS; }
    public double getAltitudMetros() { return altitudMetros; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Ubicacion other = (Ubicacion) obj;
        return this.codigoUnico.equals(other.codigoUnico);
    }

    @Override
    public int hashCode() {
        return codigoUnico.hashCode();
    }

    @Override
    public String toString() {
        return nombreComercial + " (" + codigoUnico + ")";
    }
    
}
