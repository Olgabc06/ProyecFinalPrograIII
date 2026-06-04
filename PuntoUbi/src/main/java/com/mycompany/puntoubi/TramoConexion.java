/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.puntoubi;

/**
 *
 * @author Gabriela
 */

//La Arista
//Representa la carretera o conexión directa en línea recta entre dos puntos y almacena el peso (kilómetros calculados).
public class TramoConexion {
     private final Ubicacion destino;
    private final double kilometrosDistancia;
    private final double velocidadPromedioKmh;
    private final String tipoVia;
    private final boolean esRutaNA;

    private static final double FACTOR_RURAL = 0.30;
    private static final double FACTOR_URBANA = 0.50;
    private static final double FACTOR_CARRETERA = 0.10;
    private static final double FACTOR_AUTOPISTA = 0.0;

    public TramoConexion(Ubicacion destino, double kilometrosDistancia, double velocidadPromedioKmh,String tipoVia, boolean tienePeaje, boolean esRutaNA) {
        this.destino = destino;
        this.kilometrosDistancia = kilometrosDistancia;
        this.velocidadPromedioKmh = velocidadPromedioKmh;
        this.tipoVia = tipoVia;
        this.esRutaNA = esRutaNA;
    }

    public Ubicacion getDestino() { 
        return destino; 
    }
    public double getKilometrosDistancia() { 
        return kilometrosDistancia;
    }
    public double getVelocidadPromedioKmh() { 
        return velocidadPromedioKmh;
    }
    public String getTipoVia() {
        return tipoVia;
    }
    public boolean tienePeaje() { 
        return false;
    } 
    public boolean EsRutaNA() { 
        return esRutaNA; 
    }

    /**
     * @return 
     */
    public double calcularTiempoBaseHoras() {
        double tiempoBase = kilometrosDistancia / velocidadPromedioKmh;

        switch (tipoVia.toUpperCase()) {
            case "RURAL" -> tiempoBase *= (1 + FACTOR_RURAL);
            case "URBANA" -> tiempoBase *= (1 + FACTOR_URBANA);
            case "CARRETERA" -> tiempoBase *= (1 + FACTOR_CARRETERA);
            case "AUTOPISTA" -> tiempoBase *= (1 + FACTOR_AUTOPISTA);
        }

        return tiempoBase;
    }

    /**
     * @param factorCongestion
     * @param factorCondicion
     * @return 
     */
    public double calcularTiempoEstimadoHoras(double factorCongestion, double factorCondicion) {
        double tiempoBase = calcularTiempoBaseHoras();

        double factorAplicado = esRutaNA ? factorCongestion * 0.5 : factorCongestion;

        double factorTotal = 1.0 + factorAplicado + factorCondicion;

        return tiempoBase * factorTotal;
    }

    /**
     * @param horario
     * @return 
     */
    public double calcularTiempoEstimadoHoras(HorarioTrafico horario) {
        double factorCongestion = horario.getPeriodoActual().getFactorCongestion();
        double factorCondicion = horario.getCondicionActual().getFactorRetraso();
        return calcularTiempoEstimadoHoras(factorCongestion, factorCondicion);
    }

    public double calcularTiempoEstimadoMinutos(HorarioTrafico horario) {
        return calcularTiempoEstimadoHoras(horario) * 60;
    }

    /**
     * @param criterio
     * @param horario
     * @return 
     */
    public double obtenerPeso(CriterioRuta criterio, HorarioTrafico horario) {
        double factorCongestion = horario.getPeriodoActual().getFactorCongestion();
        double factorCondicion = horario.getCondicionActual().getFactorRetraso();

        return switch (criterio) {
            case DISTANCIA_CORTA -> kilometrosDistancia;
            case TIEMPO_MINIMO -> calcularTiempoEstimadoHoras(factorCongestion, factorCondicion);
            case RUTA_ECONOMICA -> kilometrosDistancia; 
            case RUTA_SEGURA -> esRutaNA ? kilometrosDistancia * 0.8 : kilometrosDistancia;
            case RUTA_ESCENICA -> tipoVia.equalsIgnoreCase("RURAL") ? kilometrosDistancia * 0.9 : kilometrosDistancia * 1.2;
            default -> kilometrosDistancia;
        };
    }

    @Override
    public String toString() {
        return String.format("%s | %.1f km | %s |  %s",
            destino.getNombreComercial(),
            kilometrosDistancia,
            tipoVia,
            esRutaNA ? "Ruta Nacional" : "Ruta Local");
    }
}
