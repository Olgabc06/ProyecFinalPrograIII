/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.puntoubi;
import com.mycompany.puntoubi.EstructuraPropias.Lista;
import com.mycompany.puntoubi.EstructuraPropias.Mapa;
import com.mycompany.puntoubi.EstructuraPropias.Conjunto;
import com.mycompany.puntoubi.EstructuraPropias.ColaPrioridad;
import com.mycompany.puntoubi.EstructuraPropias.Pila;
/**
 *
 * @author eagab
 */
public class CalculadorRuta{
    
     public static class ResultadoRuta {
        private final Lista<Ubicacion> camino;
        private final double distanciaTotalKm;
        private final double tiempoTotalHoras;
        private final double tiempoTotalMinutos;
        private final Lista<String> pasosDijkstra;
        private final Lista<DetalleTramo> detallesTramo;
        private final CriterioRuta criterioUsado;
        private final HorarioTrafico horarioUsado;
        private final int numeroRuta; 

        public ResultadoRuta(Lista<Ubicacion> camino, double distanciaTotalKm, double tiempoTotalHoras,Lista<String> pasosDijkstra, Lista<DetalleTramo> detallesTramo,CriterioRuta criterio, HorarioTrafico horario, int numeroRuta) {
            this.camino = camino;
            this.distanciaTotalKm = distanciaTotalKm;
            this.tiempoTotalHoras = tiempoTotalHoras;
            this.tiempoTotalMinutos = tiempoTotalHoras * 60;
            this.pasosDijkstra = pasosDijkstra;
            this.detallesTramo = detallesTramo;
            this.criterioUsado = criterio;
            this.horarioUsado = horario;
            this.numeroRuta = numeroRuta;
        }

        public Lista<Ubicacion> getCamino() {
            return camino;
        }
        public double getDistanciaTotalKm() { 
            return distanciaTotalKm; 
        }
        public double getTiempoTotalHoras() { 
            return tiempoTotalHoras;
        }
        public double getTiempoTotalMinutos() {
            return tiempoTotalMinutos;
        }
        public Lista<DetalleTramo> getDetallesTramo() {
            return detallesTramo;
        }
        public CriterioRuta getCriterioUsado() { 
            return criterioUsado; 
        }
        public HorarioTrafico getHorarioUsado() {
            return horarioUsado; 
        }
        public int getNumeroRuta() { 
            return numeroRuta;
        }

        public void mostrarSimulacionViaje() {
            System.out.println("\n╔══════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                    SIMULACIÓN DE VIAJE GPS                           ║");
            System.out.println("╠══════════════════════════════════════════════════════════════════════╣");
            System.out.printf("║  Ruta #%d | Criterio: %-20s | Hora: %02d:00%n", 
                numeroRuta, criterioUsado.getDescripcion(), horarioUsado.getHoraActual());
            System.out.printf("║  Período: %-20s | Condición: %-20s%n",
                horarioUsado.getPeriodoActual().getNombre(),
                horarioUsado.getCondicionActual().getDescripcion());
            System.out.printf("║  Factor congestión: %.0f%% | Factor condición: %.0f%% | Factor total: %.0f%%%n",
                horarioUsado.getPeriodoActual().getFactorCongestion() * 100,
                horarioUsado.getCondicionActual().getFactorRetraso() * 100,
                horarioUsado.calcularFactorTotalRetraso() * 100);
            System.out.println("╚══════════════════════════════════════════════════════════════════════╝");

            if (camino.estaVacia()) {
                System.out.println("  No existe ruta disponible.");
                return;
            }

            System.out.println("\n  RESUMEN DEL VIAJE:");
            System.out.printf("  • Distancia total: %.1f km%n", distanciaTotalKm);
            System.out.printf("  • Tiempo estimado: %.0f minutos (%.1f horas)%n",tiempoTotalMinutos, tiempoTotalHoras);
            System.out.println("\n  ──────────────────────────────────────────────────────────────────");
            System.out.println("  INSTRUCCIONES PASO A PASO:\n");

            for (int i = 0; i < detallesTramo.tamano(); i++) {
                DetalleTramo dt = detallesTramo.obtener(i);
                System.out.printf("  [%d] %-20s → %-20s%n", 
                    i + 1, dt.origen, dt.destino);
                System.out.printf("      ├─ Distancia: %.1f km%n", dt.distanciaKm);
                System.out.printf("      ├─ Tipo de vía: %s%n", dt.tipoVia);
                System.out.printf("      ├─ Velocidad promedio: %.0f km/h%n", dt.velocidadKmh);
                System.out.printf("      ├─ Tiempo estimado: %.0f minutos%n", dt.tiempoMinutos);
                System.out.printf("      ├─ Peaje: No aplica (sin peajes)%n");
                System.out.printf("      └─ Condición: %s%n", dt.condicionEspecial);
                System.out.println();
            }

            System.out.println("  ──────────────────────────────────────────────────────────────────");
            System.out.printf("   LLEGADA A DESTINO: %s%n", 
                camino.obtener(camino.tamano() - 1).getNombreComercial());
            System.out.println("  ══════════════════════════════════════════════════════════════════");
        }

        @Override
        public String toString() {
            if (camino.estaVacia()) {
                return "No existe ruta entre los puntos especificados.";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("\n┌─────────────────────────────────────────────────────────────┐\n");
            sb.append(String.format("│  RUTA ALTERNATIVA #%d | %s%n", numeroRuta, criterioUsado.getDescripcion()));
            sb.append(String.format("│  Hora: %02d:00 | %s | %s%n", 
                horarioUsado.getHoraActual(), 
                horarioUsado.getPeriodoActual().getNombre(),
                horarioUsado.getCondicionActual().getDescripcion()));
            sb.append(String.format("│  Factor tráfico: %.0f%% | Factor condición: %.0f%% %n",
                horarioUsado.getPeriodoActual().getFactorCongestion() * 100,
                horarioUsado.getCondicionActual().getFactorRetraso() * 100));
            sb.append("├─────────────────────────────────────────────────────────────┤\n");

            for (int i = 0; i < camino.tamano(); i++) {
                sb.append(String.format("│  %d. %s%n", i + 1, camino.obtener(i).getNombreComercial()));
                if (i < camino.tamano() - 1) {
                    sb.append("│      ↓\n");
                }
            }

            sb.append("├─────────────────────────────────────────────────────────────┤\n");
            sb.append(String.format("│   Distancia: %.1f km%n", distanciaTotalKm));
            sb.append(String.format("│  ️  Tiempo: %.0f min (%.1f h)%n", tiempoTotalMinutos, tiempoTotalHoras));
            sb.append("│  Peajes: Gratis (sin peajes)\n");
            sb.append("└─────────────────────────────────────────────────────────────┘\n");

            return sb.toString();
        }
    }

    public static class DetalleTramo {
        String origen;
        String destino;
        double distanciaKm;
        String tipoVia;
        double velocidadKmh;
        double tiempoMinutos;
        String condicionEspecial;

        public DetalleTramo(String origen, String destino, double distanciaKm, String tipoVia,double velocidadKmh, double tiempoMinutos, String condicion) {
            this.origen = origen;
            this.destino = destino;
            this.distanciaKm = distanciaKm;
            this.tipoVia = tipoVia;
            this.velocidadKmh = velocidadKmh;
            this.tiempoMinutos = tiempoMinutos;
            this.condicionEspecial = condicion;
        }
    }


    public static Lista<ResultadoRuta> encontrarRutasAlternativas(RedVial red, String codigoOrigen, String codigoDestino,CriterioRuta criterio, HorarioTrafico horario, int maxRutas) {

        Lista<ResultadoRuta> rutasEncontradas = new Lista<>();
        Mapa<String, Double> aristasPenalizadas = new Mapa<>();

        for (int rutaNum = 1; rutaNum <= maxRutas; rutaNum++) {
            ResultadoRuta ruta = encontrarRutaOptimaConPenalizacion(
                red, codigoOrigen, codigoDestino, criterio, horario, 
                aristasPenalizadas, rutaNum);

            if (ruta.camino.estaVacia()) {
                break; 
            }

            boolean esDiferente = true;
            for (int i = 0; i < rutasEncontradas.tamano(); i++) {
                if (sonRutasIguales(ruta, rutasEncontradas.obtener(i))) {
                    esDiferente = false;
                    break;
                }
            }

            if (esDiferente) {
                rutasEncontradas.agregar(ruta);
                penalizarAristas(ruta, aristasPenalizadas);
            } else {
                penalizarAristasFuerte(ruta, aristasPenalizadas);
                rutaNum--; 
            }
        }

        return rutasEncontradas;
    }

    private static boolean sonRutasIguales(ResultadoRuta r1, ResultadoRuta r2) {
        if (r1.getCamino().tamano() != r2.getCamino().tamano()) return false;
        for (int i = 0; i < r1.getCamino().tamano(); i++) {
            if (!r1.getCamino().obtener(i).getCodigoUnico().equals(
                 r2.getCamino().obtener(i).getCodigoUnico())) {
                return false;
            }
        }
        return true;
    }

    
    public static ResultadoRuta encontrarRutaOptimaPorHorario(RedVial red, String codigoOrigen, String codigoDestino,CriterioRuta criterio, int[] horariosAProbar,HorarioTrafico.CondicionEspecial[] condicionesAProbar) {

        ResultadoRuta mejorRutaGlobal = null;
        double mejorPuntajeGlobal = Double.MAX_VALUE;

        System.out.println("\n>>> ════════════════════════════════════════════════════════════════");
        System.out.println(">>> EVALUANDO RUTAS EN DIFERENTES HORARIOS Y CONDICIONES");
        System.out.println(">>> ════════════════════════════════════════════════════════════════\n");

        for (HorarioTrafico.CondicionEspecial condicion : condicionesAProbar) {
            System.out.println("  ?️  CONDICIÓN: " + condicion.getDescripcion() + " (factor retraso: " + (int)(condicion.getFactorRetraso()*100) + "%)");
            System.out.println("  ──────────────────────────────────────────────────────────────────");
            System.out.printf("  %-8s %-20s %-10s %-10s %-12s%n","HORA", "PERÍODO", "DIST(km)", "TIEM(min)", "PUNTAJE");
            System.out.println("  ──────────────────────────────────────────────────────────────────");

            ResultadoRuta mejorRutaCondicion = null;
            double mejorPuntajeCondicion = Double.MAX_VALUE;

            for (int hora : horariosAProbar) {
                HorarioTrafico horario = new HorarioTrafico();
                horario.setHoraActual(hora);
                horario.setCondicion(condicion);

                ResultadoRuta ruta = encontrarRutaOptima(red, codigoOrigen, codigoDestino, criterio, horario);

                if (!ruta.getCamino().estaVacia()) {
                    double puntaje = calcularPuntajeRuta(ruta, criterio);

                    System.out.printf("  %02d:00   %-20s %8.1f   %8.0f   %10.2f%n",
                        hora,
                        horario.getPeriodoActual().getNombre(),
                        ruta.getDistanciaTotalKm(),
                        ruta.getTiempoTotalMinutos(),
                        puntaje);

                    if (puntaje < mejorPuntajeCondicion) {
                        mejorPuntajeCondicion = puntaje;
                        mejorRutaCondicion = ruta;
                    }
                }
            }

            System.out.println("  ──────────────────────────────────────────────────────────────────");
            if (mejorRutaCondicion != null) {
                System.out.printf("   MEJOR para esta condición: Hora %02d:00 | Tiempo: %.0f min | Puntaje: %.2f%n",mejorRutaCondicion.getHorarioUsado().getHoraActual(),mejorRutaCondicion.getTiempoTotalMinutos(),mejorPuntajeCondicion);

                if (mejorPuntajeCondicion < mejorPuntajeGlobal) {
                    mejorPuntajeGlobal = mejorPuntajeCondicion;
                    mejorRutaGlobal = mejorRutaCondicion;
                }
            }
            System.out.println();
        }

        if (mejorRutaGlobal != null) {
            System.out.println(">>> ════════════════════════════════════════════════════════════════");
            System.out.println(">>>  RUTA ÓPTIMA GLOBAL ENCONTRADA:");
            System.out.printf(">>>    Hora: %02d:00 | %s | %s%n",
                mejorRutaGlobal.getHorarioUsado().getHoraActual(),
                mejorRutaGlobal.getHorarioUsado().getPeriodoActual().getNombre(),
                mejorRutaGlobal.getHorarioUsado().getCondicionActual().getDescripcion());
            System.out.printf(">>>    Tiempo: %.0f min | Distancia: %.1f km%n",
                mejorRutaGlobal.getTiempoTotalMinutos(),
                mejorRutaGlobal.getDistanciaTotalKm());
            System.out.println(">>> ════════════════════════════════════════════════════════════════\n");
        }

        return mejorRutaGlobal;
    }

    public static ResultadoRuta encontrarRutaOptimaPorHorario(RedVial red, String codigoOrigen, String codigoDestino,CriterioRuta criterio, int[] horariosAProbar) {

        HorarioTrafico.CondicionEspecial[] condiciones = {
            HorarioTrafico.CondicionEspecial.NORMAL
        };

        return encontrarRutaOptimaPorHorario(red, codigoOrigen, codigoDestino, criterio, horariosAProbar, condiciones);
    }

    private static double calcularPuntajeRuta(ResultadoRuta ruta, CriterioRuta criterio) {
        switch (criterio) {
            case TIEMPO_MINIMO:
                return ruta.getTiempoTotalMinutos();
            case DISTANCIA_CORTA:
                return ruta.getDistanciaTotalKm();
            case RUTA_ECONOMICA:
                return ruta.getDistanciaTotalKm(); 
            case RUTA_SEGURA:
                return ruta.getTiempoTotalMinutos() * 0.8;
            case RUTA_ESCENICA:
                return ruta.getTiempoTotalMinutos() * 1.5;
            default:
                return ruta.getTiempoTotalMinutos() + ruta.getDistanciaTotalKm();
        }
    }

    private static ResultadoRuta encontrarRutaOptimaConPenalizacion(RedVial red, String codigoOrigen, String codigoDestino,CriterioRuta criterio, HorarioTrafico horario,Mapa<String, Double> aristasPenalizadas,int numeroRuta) {
        String origen = codigoOrigen.toUpperCase().trim();
        String destino = codigoDestino.toUpperCase().trim();

        Mapa<String, Double> distancias = new Mapa<>();
        Mapa<String, String> predecesores = new Mapa<>();
        Conjunto<String> visitados = new Conjunto<>();
        ColaPrioridad colaPrioridad = new ColaPrioridad();
        Lista<String> pasos = new Lista<>();

        Mapa<String, TramoConexion> tramoElegido = new Mapa<>();

        int pasoNumero = 1;
        pasos.agregar("[" + (pasoNumero++) + "] INICIO RUTA #" + numeroRuta + ": Origen = " + origen + 
                     " | Hora = " + horario.getHoraActual() + ":00 | " + horario.getPeriodoActual().getNombre());

        Lista<Ubicacion> todasUbicaciones = red.obtenerCatalogoUbicaciones();
        for (int i = 0; i < todasUbicaciones.tamano(); i++) {
            String cod = todasUbicaciones.obtener(i).getCodigoUnico();
            distancias.put(cod, Double.MAX_VALUE);
        }
        distancias.put(origen, 0.0);
        colaPrioridad.insertar(origen, 0.0);

        while (!colaPrioridad.estaVacia()) {
            String codigoActual = colaPrioridad.extraerMinimo();

            if (visitados.contiene(codigoActual)) continue;
            visitados.agregar(codigoActual);

            if (codigoActual.equals(destino)) {
                break;
            }

            Lista<TramoConexion> tramos = red.obtenerTramosDe(codigoActual);
            for (int i = 0; i < tramos.tamano(); i++) {
                TramoConexion tramo = tramos.obtener(i);
                String codigoVecino = tramo.getDestino().getCodigoUnico();

                if (visitados.contiene(codigoVecino)) continue;

                double pesoTramo = tramo.obtenerPeso(criterio, horario);

                String claveArista = codigoActual + "-" + codigoVecino;
                Double penalizacion = aristasPenalizadas.get(claveArista);
                if (penalizacion != null) {
                    pesoTramo += penalizacion;
                }

                double distanciaActual = distancias.get(codigoActual);
                double nuevaDistancia = distanciaActual + pesoTramo;
                double distanciaVecinoActual = distancias.get(codigoVecino);

                if (nuevaDistancia < distanciaVecinoActual) {
                    distancias.put(codigoVecino, nuevaDistancia);
                    predecesores.put(codigoVecino, codigoActual);
                    tramoElegido.put(codigoVecino, tramo);
                    colaPrioridad.insertar(codigoVecino, nuevaDistancia);
                }
            }
        }

        Lista<Ubicacion> camino = new Lista<>();
        Lista<DetalleTramo> detalles = new Lista<>();

        if (!predecesores.containskey(destino) && !origen.equals(destino)) {
            return new ResultadoRuta(camino, 0, 0, pasos, detalles, criterio, horario, numeroRuta);
        }

        Pila<Ubicacion> pila = new Pila<>();
        String paso = destino;
        while (paso != null) {
            for (int i = 0; i < todasUbicaciones.tamano(); i++) {
                if (todasUbicaciones.obtener(i).getCodigoUnico().equals(paso)) {
                    pila.apilar(todasUbicaciones.obtener(i));
                    break;
                }
            }
            paso = predecesores.get(paso);
        }

        while (!pila.estaVacia()) {
            camino.agregar(pila.desapilar());
        }

        double distanciaTotal = 0;
        double tiempoTotal = 0;

        for (int i = 0; i < camino.tamano() - 1; i++) {
            Ubicacion orig = camino.obtener(i);
            Ubicacion dest = camino.obtener(i + 1);

            TramoConexion tramo = null;
            Lista<TramoConexion> tramosOrigen = red.obtenerTramosDe(orig.getCodigoUnico());
            for (int j = 0; j < tramosOrigen.tamano(); j++) {
                if (tramosOrigen.obtener(j).getDestino().getCodigoUnico().equals(dest.getCodigoUnico())) {
                    tramo = tramosOrigen.obtener(j);
                    break;
                }
            }

            if (tramo != null) {
                distanciaTotal += tramo.getKilometrosDistancia();
                tiempoTotal += tramo.calcularTiempoEstimadoHoras(horario);

                detalles.agregar(new DetalleTramo(
                    orig.getNombreComercial(),
                    dest.getNombreComercial(),
                    tramo.getKilometrosDistancia(),
                    tramo.getTipoVia(),
                    tramo.getVelocidadPromedioKmh(),
                    tramo.calcularTiempoEstimadoMinutos(horario),
                    horario.getPeriodoActual().getNombre() + " | " + horario.getCondicionActual().getDescripcion()
                ));
            }
        }

        return new ResultadoRuta(camino, distanciaTotal, tiempoTotal,pasos, detalles, criterio, horario, numeroRuta);
    }

    private static void penalizarAristas(ResultadoRuta ruta,Mapa<String, Double> aristasPenalizadas) {

        Lista<Ubicacion> camino = ruta.getCamino();
        double penalizacionBase = 1000.0;

        for (int i = 0; i < camino.tamano() - 1; i++) {
            String origen = camino.obtener(i).getCodigoUnico();
            String destino = camino.obtener(i + 1).getCodigoUnico();

            String claveIda = origen + "-" + destino;
            String claveVuelta = destino + "-" + origen;

            Double penalizacionActual = aristasPenalizadas.get(claveIda);
            if (penalizacionActual == null) {
                penalizacionActual = 0.0;
            }

            double nuevaPenalizacion = penalizacionActual + penalizacionBase;
            aristasPenalizadas.put(claveIda, nuevaPenalizacion);
            aristasPenalizadas.put(claveVuelta, nuevaPenalizacion);
        }
    }

    private static void penalizarAristasFuerte(ResultadoRuta ruta, Mapa<String, Double> aristasPenalizadas) {
        Lista<Ubicacion> camino = ruta.getCamino();
        double penalizacionBase = 5000.0;

        for (int i = 0; i < camino.tamano() - 1; i++) {
            String origen = camino.obtener(i).getCodigoUnico();
            String destino = camino.obtener(i + 1).getCodigoUnico();

            String claveIda = origen + "-" + destino;
            String claveVuelta = destino + "-" + origen;

            Double penalizacionActual = aristasPenalizadas.get(claveIda);
            if (penalizacionActual == null) {
                penalizacionActual = 0.0;
            }

            double nuevaPenalizacion = penalizacionActual + penalizacionBase;
            aristasPenalizadas.put(claveIda, nuevaPenalizacion);
            aristasPenalizadas.put(claveVuelta, nuevaPenalizacion);
        }
    }
    
    public static ResultadoRuta encontrarRutaOptima(RedVial red, String codigoOrigen,String codigoDestino,CriterioRuta criterio,HorarioTrafico horario) {
        Lista<ResultadoRuta> rutas = encontrarRutasAlternativas(
            red, codigoOrigen, codigoDestino, criterio, horario, 1);
        return rutas.estaVacia() ? 
            new ResultadoRuta(new Lista<>(), 0, 0, 
                new Lista<>(), new Lista<>(), 
                criterio, horario, 1) : 
            rutas.obtener(0);
    }

    public static ResultadoRuta encontrarRutaMasCorta(RedVial red, String origen, String destino) {
        return encontrarRutaOptima(red, origen, destino, 
            CriterioRuta.DISTANCIA_CORTA, new HorarioTrafico());
    }
}