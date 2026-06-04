/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.puntoubi;

public class HorarioTrafico{
    
    public enum PeriodoDia{
        MADRUGADA(0, 5, "Madrugada", 0.0),
        HORA_PICO_MATUTINO(6, 9, "Hora pico matutino", 0.80),
        MEDIO_DIA(10, 16, "Medio día", 0.20),
        HORA_PICO_VESPERTINO(17, 20, "Hora pico vespertino", 0.70),
        NOCHE(21, 23, "Noche", 0.0);
        
        private final int horaInicio;
        private final int horaFin;
        private final String nombre;
        private final double factorCongestion;
        
        PeriodoDia(int inicio, int fin, String nombre, double factor){
            this.horaInicio = inicio;
            this.horaFin = fin;
            this.nombre = nombre;
            this.factorCongestion = factor;
        }
        
        public static PeriodoDia obtenerPeriodo(int hora){
            for (PeriodoDia p : values()) {
                if (hora >= p.horaInicio && hora <= p.horaFin) {
                    return p;
                }
            }
            return MEDIO_DIA;
        }
        
        public String getNombre(){
            return nombre;
        }
        public double getFactorCongestion(){ 
            return factorCongestion; 
        }
    }
    
    public enum CondicionEspecial{
        NORMAL("Condiciones normales", 0.0),
        LLUVIA("Lluvia moderada", 0.25),
        TORMENTA("Tormenta fuerte", 0.60),
        CONSTRUCCION("Obras en la vía", 0.40),
        ACCIDENTE("Accidente reportado", 0.50),
        EVENTO_ESPECIAL("Evento/fiesta patronal", 0.35);
        
        private final String descripcion;
        private final double factorRetraso;
        
        CondicionEspecial(String descripcion, double factor){
            this.descripcion = descripcion;
            this.factorRetraso = factor;
        }
        
        public String getDescripcion(){ 
            return descripcion; 
        }
        public double getFactorRetraso(){
            return factorRetraso;
        }
    }
    
    private int horaActual;
    private CondicionEspecial condicionActual;
    
    public HorarioTrafico(){
        this.horaActual = 12; 
        this.condicionActual = CondicionEspecial.NORMAL;
    }
    
    public void setHoraActual(int hora){
        if (hora < 0 || hora > 23) {
            throw new IllegalArgumentException("La hora debe estar entre 0 y 23");
        }
        this.horaActual = hora;
    }
    
    public void setCondicion(CondicionEspecial condicion){
        this.condicionActual = condicion;
    }
    
    public int getHoraActual(){
        return horaActual; 
    }
    public CondicionEspecial getCondicionActual(){
        return condicionActual; 
    }
    
    public PeriodoDia getPeriodoActual(){
        return PeriodoDia.obtenerPeriodo(horaActual);
    }
     
    public double calcularFactorTotalRetraso(){
        double factorHora = getPeriodoActual().getFactorCongestion();
        double factorCondicion = condicionActual.getFactorRetraso();
        return factorHora + factorCondicion; 
    }
    
    @Override
    public String toString(){
        return String.format("Hora: %02d:00 | Periodo: %s | Condición: %s | Factor total: %.0f%%",
            horaActual,
            getPeriodoActual().getNombre(),
            condicionActual.getDescripcion(),
            calcularFactorTotalRetraso() * 100);
    }
}
