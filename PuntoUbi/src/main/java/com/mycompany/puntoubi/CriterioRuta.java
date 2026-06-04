/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.puntoubi;

/**
 *
 * @author eagab
 */
public enum CriterioRuta {
    DISTANCIA_CORTA("Ruta más corta en kilómetros"),
    TIEMPO_MINIMO("Ruta más rápida (menor tiempo)"),
    RUTA_ECONOMICA("Ruta con menos peajes"),
    RUTA_SEGURA("Ruta por carreteras nacionales"),
    RUTA_ESCENICA("Ruta con mejor paisaje (menor velocidad, rural)");
    
    private final String descripcion;
    
    CriterioRuta(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
