/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.puntoubi;

/**
 *
 * @author Gabriela
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import com.mycompany.puntoubi.EstructuraPropias.Lista;

public class GestorArchivos {
    // Método para guardar todo a un archivo de texto disque plano
    public static void guardarMapaATexto(RedVial red, String rutaArchivo) {
        try (PrintWriter escritor = new PrintWriter(new FileWriter(rutaArchivo))) {
            
            // Primero guarda las ubicaciones o sea los Nodos
            escritor.println("[UBICACIONES]<");
            Lista<Ubicacion> ubicaciones = red.obtenerCatalogoUbicaciones();
            for (int i = 0; i < ubicaciones.tamano(); i++) {
                Ubicacion u = ubicaciones.obtener(i);
                // CODIGO;Nombre;Latitud;Longitud;Altura
                escritor.printf("%s;%s;%.6f;%.6f;%.2f\n", 
                        u.getCodigoUnico(), 
                        u.getNombreComercial(), 
                        u.getLatitudWGS(), 
                        u.getLongitudWGS(), 
                        u.getAltitudMetros());
            }

            // Segundo guarda las conexiones que son las Aristas
            escritor.println("[TRAMOS]");
            for (int i = 0; i < ubicaciones.tamano(); i++){
                Ubicacion u = ubicaciones.obtener(i);
                Lista<TramoConexion> tramos = red.obtenerTramosDe(u.getCodigoUnico());
                for (int j = 0; j < tramos.tamano(); j++){
                    TramoConexion tramo = tramos.obtener(j);
                    // Para no duplicar tramos en el archivo, solo guardamos si el código origen es menor alfabéticamente
                    if (u.getCodigoUnico().compareTo(tramo.getDestino().getCodigoUnico()) < 0) {
                        escritor.printf("%s;%s\n", u.getCodigoUnico(), tramo.getDestino().getCodigoUnico());
                    }
                }
            }
            
            System.out.println(">> Sistema: Datos respaldados exitosamente en: " + rutaArchivo);
            
        } catch (IOException e) {
            System.err.println("Se ha detectado un herror crítico al guardar el archivo: " + e.getMessage());
        }
    }

    // Método para leer el archivo de texto y reconstruir el mapa desde cero
    public static RedVial cargarMapaDesdeTexto(String rutaArchivo) {
        RedVial nuevaRed = new RedVial();
        String seccionActual = "";

        try (BufferedReader lector = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            
            while ((linea = lector.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;

                // Detecta en qué sección del archivo estamos
                if (linea.equals("[UBICACIONES]<")) {
                    seccionActual = "UBICACIONES";
                    continue;
                } else if (linea.equals("[TRAMOS]")) {
                    seccionActual = "TRAMOS";
                    continue;
                }

                // Procesa la información según la sección
                String[] datos = linea.split(";");
                
                if (seccionActual.equals("UBICACIONES") && datos.length == 5) {
                    String codigo = datos[0];
                    String nombre = datos[1];
                    double lat = Double.parseDouble(datos[2]);
                    double lon = Double.parseDouble(datos[3]);
                    double alt = Double.parseDouble(datos[4]);
                    
                    Ubicacion u = new Ubicacion(codigo, nombre, lat, lon, alt);
                    nuevaRed.registrarNuevaUbicacion(u);
                    
                } else if (seccionActual.equals("TRAMOS") && datos.length == 2) {
                    String origen = datos[0];
                    String destino = datos[1];
                    // Enlaza de nuevo (nuestro método calculará los KM automáticamente)
                    nuevaRed.enlazarRuta(origen, destino);
                }
            }
            System.out.println(">> Sistema: Mapa recuperado con éxito desde: " + rutaArchivo);
            
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error al cargar o interpretar el archivo: " + e.getMessage());
        }

        return nuevaRed;
    }
    
    // Método personalizado para generar el JSON que Olga leerá desde su HTML
    public static void exportarAJsonParaVisualizacion(RedVial red, String rutaJson) {
        try (PrintWriter escritor = new PrintWriter(new FileWriter(rutaJson))) {
            Lista<Ubicacion> ubicaciones = red.obtenerCatalogoUbicaciones();
            
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            
            //  Sección de Ciudades/Nodos
            json.append("  \"ciudades\": [\n");
            for (int i = 0; i < ubicaciones.tamano(); i++) {
                Ubicacion u = ubicaciones.obtener(i);
                json.append("    {\n");
                json.append("      \"id\": \"").append(u.getCodigoUnico()).append("\",\n");
                json.append("      \"nombre\": \"").append(u.getNombreComercial()).append("\",\n");
                json.append("      \"latitud\": ").append(u.getLatitudWGS()).append(",\n");
                json.append("      \"longitud\": ").append(u.getLongitudWGS()).append(",\n");
                json.append("      \"altura\": ").append(u.getAltitudMetros()).append("\n");
                json.append("    }");
                if (i < ubicaciones.tamano() - 1) json.append(",");
                json.append("\n");
            }
            json.append("  ],\n");
            
            //Sección de Conexiones/Aristas
            json.append("  \"rutas\": [\n");
            boolean primeraRuta = true;
            for (int i = 0; i < ubicaciones.tamano(); i++) {
                Ubicacion u = ubicaciones.obtener(i);
                Lista<TramoConexion> tramos = red.obtenerTramosDe(u.getCodigoUnico());
                for (int j = 0; j < tramos.tamano(); j++) {
                    TramoConexion tramo = tramos.obtener(j);
                    // Evitamos duplicar rutas de ida y vuelta en el JSON
                    if (u.getCodigoUnico().compareTo(tramo.getDestino().getCodigoUnico()) < 0) {
                        if (!primeraRuta) {
                            json.append(",\n");
                        }
                        json.append("    {\n");
                        json.append("      \"origen\": \"").append(u.getCodigoUnico()).append("\",\n");
                        json.append("      \"destino\": \"").append(tramo.getDestino().getCodigoUnico()).append("\",\n");
                        json.append("      \"distancia_km\": ").append(String.format(java.util.Locale.US, "%.2f", tramo.getKilometrosDistancia())).append("\n");
                        json.append("    }");
                        primeraRuta = false;
                    }
                }
            }
            json.append("\n  ]\n");
            json.append("}");
            
            escritor.print(json.toString());
            System.out.println(">> Sistema: Archivo JSON exportado exitosamente para HTML en: " + rutaJson);
            
        } catch (IOException e) {
            System.err.println("Error al generar el JSON: " + e.getMessage());
        }
    }
    
    // Versión para el servidor HTTP — devuelve el JSON como String
    public static String exportarAJsonString(RedVial red) {
        Lista<Ubicacion> ubicaciones = red.obtenerCatalogoUbicaciones();
        StringBuilder json = new StringBuilder();
        json.append("{\n");

        json.append("  \"ciudades\": [\n");
        for (int i = 0; i < ubicaciones.tamano(); i++) {
            Ubicacion u = ubicaciones.obtener(i);
            json.append("    {\n");
            json.append("      \"id\": \"").append(u.getCodigoUnico()).append("\",\n");
            json.append("      \"nombre\": \"").append(u.getNombreComercial()).append("\",\n");
            json.append("      \"lat\": ").append(u.getLatitudWGS()).append(",\n");
            json.append("      \"lon\": ").append(u.getLongitudWGS()).append(",\n");
            json.append("      \"alt\": ").append(u.getAltitudMetros()).append("\n");
            json.append("    }");
            if (i < ubicaciones.tamano() - 1) json.append(",");
            json.append("\n");
        }
        json.append("  ],\n");

        json.append("  \"rutas\": [\n");
        boolean primeraRuta = true;
        for (int i = 0; i < ubicaciones.tamano(); i++) {
            Ubicacion u = ubicaciones.obtener(i);
            Lista<TramoConexion> tramos = red.obtenerTramosDe(u.getCodigoUnico());
            for (int j = 0; j < tramos.tamano(); j++) {
                TramoConexion tramo = tramos.obtener(j);
                if (u.getCodigoUnico().compareTo(tramo.getDestino().getCodigoUnico()) < 0) {
                    if (!primeraRuta) json.append(",\n");
                    json.append("    {\n");
                    json.append("      \"idOrigen\": \"").append(u.getCodigoUnico()).append("\",\n");
                    json.append("      \"idDestino\": \"").append(tramo.getDestino().getCodigoUnico()).append("\",\n");
                    json.append("      \"distancia\": ").append(String.format(java.util.Locale.US, "%.2f", tramo.getKilometrosDistancia())).append("\n");
                    json.append("    }");
                    primeraRuta = false;
                }
            }
        }
        json.append("\n  ]\n}");
        return json.toString();
    }  
}
