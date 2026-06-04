/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.puntoubi;

/**
 *
 * @author Gabriela
 */
//Fórmula Matmática
//implementamos el cálculo matemático basándonos en trigonometría esférica pura
//Distancia en línea recta para Guatemala
public class CalculadorDistancias {
    private static final double RADIO_TERRESTRE_KM = 6371.0088; // Radio medio de la Tierra preciso

    public static double entreCoordenadas(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        
        double ecuacionA = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                           Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                           Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double ecuacionC = 2 * Math.atan2(Math.sqrt(ecuacionA), Math.sqrt(1 - ecuacionA));
        return RADIO_TERRESTRE_KM * ecuacionC;
    }
    
}
