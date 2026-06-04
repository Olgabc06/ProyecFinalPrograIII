/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.puntoubi;

import java.util.Scanner;

public class PuntoUbi {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        System.out.println("╔══════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                     SISTEMA GPS                                     ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════╝\n");

        RedVial mapa = new RedVial();
        cargarDatosSinPeajes(mapa);
        mapa.imprimirEstructuraRed();

        boolean continuar = true;
        while (continuar) {
            System.out.println("\n═══════════════════════════════════════════════════════════════════════");
            System.out.println("  MENÚ PRINCIPAL");
            System.out.println("═══════════════════════════════════════════════════════════════════════");
            System.out.println("  1. Buscar ruta óptima entre dos puntos");
            System.out.println("  2. Ver rutas alternativas");
            System.out.println("  3. Comparar rutas por horario");
            System.out.println("  4. Simulación de viaje");
            System.out.println("  5. Ver todas las ubicaciones");
            System.out.println("  6. Salir e iniciar servidor para HTML");
            System.out.println("═══════════════════════════════════════════════════════════════════════");
            System.out.print("  Seleccione una opción: ");

            int opcion = leerEntero();

            switch (opcion) {
                case 1: buscarRutaOptima(mapa); break;
                case 2: buscarRutasAlternativas(mapa); break;
                case 3: compararRutasPorHorario(mapa); break;
                case 4: simularViaje(mapa); break;
                case 5: mostrarUbicaciones(mapa); break;
                case 6: continuar = false; break;
                default: System.out.println("  Opción no válida.");
            }
        }

        // Guardar archivos
        System.out.println("\n>>> Guardando datos...");
        GestorArchivos.guardarMapaATexto(mapa, "mapa_umg.txt");
        GestorArchivos.exportarAJsonParaVisualizacion(mapa, "mapa_web.json");
        System.out.println(">>> Archivos generados correctamente.");

        // Iniciar servidor para el HTML
        System.out.println("\n>>> Iniciando servidor para HTML...");
        ServidorGPS servidor = new ServidorGPS(mapa);
        servidor.iniciar();
    }

    private static void buscarRutaOptima(RedVial mapa) {
        System.out.println("\n>>> BUSCAR RUTA ÓPTIMA");
        mostrarUbicaciones(mapa);

        System.out.print("\n  Ingrese código de ORIGEN: ");
        String origen = scanner.nextLine().trim().toUpperCase();
        System.out.print("  Ingrese código de DESTINO: ");
        String destino = scanner.nextLine().trim().toUpperCase();

        if (!mapa.existeUbicacion(origen) || !mapa.existeUbicacion(destino)) {
            System.out.println("   Ubicación no encontrada."); return;
        }

        System.out.print("  Ingrese hora actual (0-23): ");
        int hora = Math.max(0, Math.min(23, leerEntero()));

        System.out.println("  Condición: 1.NORMAL  2.LLUVIA  3.ACCIDENTE");
        System.out.print("  Opción: ");
        int cond = leerEntero();
        HorarioTrafico.CondicionEspecial condicion = switch(cond) {
            case 2 -> HorarioTrafico.CondicionEspecial.LLUVIA;
            case 3 -> HorarioTrafico.CondicionEspecial.ACCIDENTE;
            default -> HorarioTrafico.CondicionEspecial.NORMAL;
        };

        System.out.println("  Criterio: 1.Tiempo mínimo  2.Distancia mínima");
        System.out.print("  Opción: ");
        CriterioRuta criterio = leerEntero() == 2 ? 
            CriterioRuta.DISTANCIA_CORTA : CriterioRuta.TIEMPO_MINIMO;

        HorarioTrafico h = new HorarioTrafico();
        h.setHoraActual(hora);
        h.setCondicion(condicion);

        CalculadorRuta.ResultadoRuta r = CalculadorRuta.encontrarRutaOptima(
            mapa, origen, destino, criterio, h);

        if (r != null && !r.getCamino().estaVacia()) {
            System.out.println(r.toString());
        } else {
            System.out.println("  No se encontró ruta.");
        }
    }

    private static void buscarRutasAlternativas(RedVial mapa) {
        System.out.println("\n>>> RUTAS ALTERNATIVAS");
        mostrarUbicaciones(mapa);

        System.out.print("\n  Ingrese código de ORIGEN: ");
        String origen = scanner.nextLine().trim().toUpperCase();
        System.out.print("  Ingrese código de DESTINO: ");
        String destino = scanner.nextLine().trim().toUpperCase();

        if (!mapa.existeUbicacion(origen) || !mapa.existeUbicacion(destino)) {
            System.out.println("  Ubicación no encontrada."); return;
        }

        System.out.print("  Hora actual (0-23): ");
        int hora = Math.max(0, Math.min(23, leerEntero()));
        System.out.print("  Cantidad de rutas (máx 10): ");
        int cantidad = Math.max(1, Math.min(10, leerEntero()));

        HorarioTrafico h = new HorarioTrafico();
        h.setHoraActual(hora);

        EstructuraPropias.Lista<CalculadorRuta.ResultadoRuta> rutas =
            CalculadorRuta.encontrarRutasAlternativas(
                mapa, origen, destino, CriterioRuta.TIEMPO_MINIMO, h, cantidad);

        if (rutas.tamano() == 0) {
            System.out.println("  No se encontraron rutas."); return;
        }

        System.out.println("\n  " + rutas.tamano() + " RUTAS ALTERNATIVAS:");
        for (int r = 0; r < rutas.tamano(); r++) {
            System.out.println(rutas.obtener(r).toString());
        }
    }

    private static void compararRutasPorHorario(RedVial mapa) {
        System.out.println("\n>>> COMPARAR RUTAS POR HORARIO");
        mostrarUbicaciones(mapa);

        System.out.print("\n  Ingrese código de ORIGEN: ");
        String origen = scanner.nextLine().trim().toUpperCase();
        System.out.print("  Ingrese código de DESTINO: ");
        String destino = scanner.nextLine().trim().toUpperCase();

        if (!mapa.existeUbicacion(origen) || !mapa.existeUbicacion(destino)) {
            System.out.println("  Ubicación no encontrada."); return;
        }

        int[] horarios = {6, 7, 8, 12, 14, 17, 18, 19, 20, 22};
        HorarioTrafico.CondicionEspecial[] condiciones = {
            HorarioTrafico.CondicionEspecial.NORMAL,
            HorarioTrafico.CondicionEspecial.LLUVIA,
            HorarioTrafico.CondicionEspecial.ACCIDENTE
        };

        CalculadorRuta.ResultadoRuta rutaOptima =
            CalculadorRuta.encontrarRutaOptimaPorHorario(
                mapa, origen, destino, CriterioRuta.TIEMPO_MINIMO,
                horarios, condiciones);

        if (rutaOptima != null) {
            System.out.println("\n  RUTA ÓPTIMA FINAL:");
            System.out.println(rutaOptima.toString());
        }
    }

    private static void simularViaje(RedVial mapa) {
        System.out.println("\n>>> SIMULACIÓN DE VIAJE");
        mostrarUbicaciones(mapa);

        System.out.print("\n  Ingrese código de ORIGEN: ");
        String origen = scanner.nextLine().trim().toUpperCase();
        System.out.print("  Ingrese código de DESTINO: ");
        String destino = scanner.nextLine().trim().toUpperCase();

        if (!mapa.existeUbicacion(origen) || !mapa.existeUbicacion(destino)) {
            System.out.println("  Ubicación no encontrada."); return;
        }

        System.out.print("  Hora actual (0-23): ");
        int hora = Math.max(0, Math.min(23, leerEntero()));

        HorarioTrafico h = new HorarioTrafico();
        h.setHoraActual(hora);

        EstructuraPropias.Lista<CalculadorRuta.ResultadoRuta> rutas =
            CalculadorRuta.encontrarRutasAlternativas(
                mapa, origen, destino, CriterioRuta.TIEMPO_MINIMO, h, 5);

        if (rutas.tamano() > 0) {
            rutas.obtener(0).mostrarSimulacionViaje();
        } else {
            System.out.println("  No se encontró ruta para simular.");
        }
    }

    private static void mostrarUbicaciones(RedVial mapa) {
        System.out.println("\n  UBICACIONES DISPONIBLES:");
        mapa.imprimirUbicaciones();
    }

    private static int leerEntero() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("  Entrada inválida. Ingrese un número: ");
            }
        }
    }

    private static void cargarDatosSinPeajes(RedVial mapa) {
        System.out.println(">>> Cargando datos...\n");

        mapa.registrarNuevaUbicacion(new Ubicacion("ANT", "Antigua Guatemala (Centro)", 14.5573, -90.7332, 1530));
        mapa.registrarNuevaUbicacion(new Ubicacion("CV",  "Ciudad Vieja",               14.5256, -90.7694, 1550));
        mapa.registrarNuevaUbicacion(new Ubicacion("JOC", "Jocotenango",                14.5850, -90.7430, 1490));
        mapa.registrarNuevaUbicacion(new Ubicacion("SMP", "San Miguel Dueñas",          14.5217, -90.7967, 1580));
        mapa.registrarNuevaUbicacion(new Ubicacion("SAC", "Santiago Sacatepéquez",      14.6356, -90.6786, 1620));
        mapa.registrarNuevaUbicacion(new Ubicacion("PAST","Pastores",                   14.5950, -90.7550, 1570));
        mapa.registrarNuevaUbicacion(new Ubicacion("SJPC","San Juan del Obispo",        14.5450, -90.7100, 1680));
        mapa.registrarNuevaUbicacion(new Ubicacion("SMC", "Santa María de Jesús",       14.5150, -90.7200, 2100));
        mapa.registrarNuevaUbicacion(new Ubicacion("Z1",  "Zona 1 - Centro Histórico",  14.6407, -90.5133, 1490));
        mapa.registrarNuevaUbicacion(new Ubicacion("Z10", "Zona 10 - Zona Viva",        14.6030, -90.5080, 1520));
        mapa.registrarNuevaUbicacion(new Ubicacion("CAY", "Cayalá",                     14.6110, -90.4860, 1540));
        mapa.registrarNuevaUbicacion(new Ubicacion("AIR", "Aeropuerto La Aurora",       14.5833, -90.5275, 1509));
        mapa.registrarNuevaUbicacion(new Ubicacion("TIN", "Tikal Futura, Zona 7",       14.6210, -90.5200, 1500));
        mapa.registrarNuevaUbicacion(new Ubicacion("OBRA","Obrajes, Zona 10",           14.5980, -90.5150, 1530));
        mapa.registrarNuevaUbicacion(new Ubicacion("UMG", "Universidad Mariano Gálvez", 14.5820, -90.7450, 1480));

        mapa.enlazarRuta("Z1",  "Z10",  35, "URBANA",     false, false);
        mapa.enlazarRuta("Z10", "CAY",  40, "URBANA",     false, false);
        mapa.enlazarRuta("Z10", "AIR",  50, "CARRETERA",  false, false);
        mapa.enlazarRuta("Z1",  "TIN",  30, "URBANA",     false, false);
        mapa.enlazarRuta("Z10", "OBRA", 25, "URBANA",     false, false);
        mapa.enlazarRuta("CAY", "TIN",  45, "CARRETERA",  false, false);
        mapa.enlazarRuta("Z1",  "AIR",  40, "URBANA",     false, false);
        mapa.enlazarRuta("AIR", "OBRA", 20, "URBANA",     false, false);
        mapa.enlazarRuta("CAY", "AIR",  35, "CARRETERA",  false, false);
        mapa.enlazarRuta("Z1",  "ANT",  75, "AUTOPISTA",  false, false);
        mapa.enlazarRuta("Z10", "JOC",  60, "CARRETERA",  false, false);
        mapa.enlazarRuta("AIR", "ANT",  65, "CARRETERA",  false, false);
        mapa.enlazarRuta("CAY", "SAC",  50, "CARRETERA",  false, false);
        mapa.enlazarRuta("TIN", "SAC",  55, "CARRETERA",  false, false);
        mapa.enlazarRuta("ANT", "CV",   50, "CARRETERA",  false, false);
        mapa.enlazarRuta("ANT", "JOC",  40, "URBANA",     false, false);
        mapa.enlazarRuta("ANT", "PAST", 35, "RURAL",      false, false);
        mapa.enlazarRuta("ANT", "SJPC", 30, "RURAL",      false, false);
        mapa.enlazarRuta("CV",  "SMP",  45, "RURAL",      false, false);
        mapa.enlazarRuta("JOC", "SAC",  55, "CARRETERA",  false, false);
        mapa.enlazarRuta("SMP", "SMC",  25, "RURAL",      false, false);
        mapa.enlazarRuta("JOC", "PAST", 20, "RURAL",      false, false);
        mapa.enlazarRuta("CV",  "JOC",  15, "URBANA",     false, false);
        mapa.enlazarRuta("ANT", "SAC",  30, "CARRETERA",  false, false);
        mapa.enlazarRuta("JOC", "UMG",  15, "URBANA",     false, false);
        mapa.enlazarRuta("ANT", "UMG",  25, "CARRETERA",  false, false);
        mapa.enlazarRuta("CV",  "UMG",  20, "RURAL",      false, false);
        mapa.enlazarRuta("PAST","UMG",  18, "RURAL",      false, false);
        mapa.enlazarRuta("SJPC","UMG",  22, "RURAL",      false, false);
        mapa.enlazarRuta("SAC", "UMG",  35, "CARRETERA",  false, false);

        System.out.println("Datos cargados: 15 lugares + UMG, todas las rutas sin peaje.\n");
        GestorArchivos.exportarAJsonParaVisualizacion(mapa, "mapa_web.json");
    }
}