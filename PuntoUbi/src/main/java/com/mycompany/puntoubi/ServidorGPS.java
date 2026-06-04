/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.puntoubi;

import com.mycompany.puntoubi.EstructuraPropias.Lista;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;


public class ServidorGPS {
     private final RedVial mapa;

    public ServidorGPS(RedVial mapa) {
        this.mapa = mapa;
    }
    
    public void iniciar() throws IOException {
        HttpServer servidor = HttpServer.create(new InetSocketAddress(8080), 0);

        // Ruta 1: devuelve todas las ciudades y rutas
        servidor.createContext("/mapa", (HttpExchange ex) -> {
            String json = GestorArchivos.exportarAJsonString(mapa);
            responder(ex, json);
        });

        // Ruta 2: calcula la ruta más corta entre dos ciudades
        servidor.createContext("/ruta", (HttpExchange ex) -> {
            String query = ex.getRequestURI().getQuery();
            String origen  = obtenerParam(query, "origen");
            String destino = obtenerParam(query, "destino");

            HorarioTrafico horario = new HorarioTrafico();
            horario.setHoraActual(12);

            CalculadorRuta.ResultadoRuta resultado =
                CalculadorRuta.encontrarRutaOptima(
                    mapa, origen, destino,
                    CriterioRuta.DISTANCIA_CORTA, horario
                );

            String json = resultadoAJson(resultado);
            responder(ex, json);
        });

            servidor.createContext("/", (HttpExchange ex) -> {
            responder(ex, "{}");
        });
        servidor.start();
        System.out.println(">> Servidor GPS corriendo en http://localhost:8080");
        System.out.println(">> Abrí gps_ui.html en el navegador");
        
        // Ruta 3: agregar una ciudad nueva
        servidor.createContext("/agregarCiudad", (HttpExchange ex) -> {
            try {
                String query = ex.getRequestURI().getQuery();
                String id     = obtenerParam(query, "id");
                String nombre = obtenerParam(query, "nombre");
                double lat    = Double.parseDouble(obtenerParam(query, "lat"));
                double lon    = Double.parseDouble(obtenerParam(query, "lon"));
                double alt    = Double.parseDouble(obtenerParam(query, "alt"));

                mapa.registrarNuevaUbicacion(new Ubicacion(id, nombre, lat, lon, alt));
                GestorArchivos.exportarAJsonParaVisualizacion(mapa, "mapa_web.json");

                responder(ex, "{\"ok\":true}");
            } catch (Exception e) {
                responder(ex, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        });

        // Ruta 4: conectar dos ciudades
        servidor.createContext("/agregarRuta", (HttpExchange ex) -> {
            try {
                String query   = ex.getRequestURI().getQuery();
                String origen  = obtenerParam(query, "origen");
                String destino = obtenerParam(query, "destino");
                double dist    = Double.parseDouble(obtenerParam(query, "dist"));

                mapa.enlazarRuta(origen, destino, (int)dist, "CARRETERA", false, false);
                GestorArchivos.exportarAJsonParaVisualizacion(mapa, "mapa_web.json");

                responder(ex, "{\"ok\":true}");
            } catch (Exception e) {
                responder(ex, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        });
        
        // Ruta 5: calcular tiempo y peaje de un camino específico
        servidor.createContext("/calcularCamino", (HttpExchange ex) -> {
            try {
                String body = new String(ex.getRequestBody().readAllBytes());

                String paradasStr = body.substring(
                    body.indexOf("[") + 1, body.indexOf("]")
                );
                String[] ids = paradasStr.replace("\"", "").split(",");

                int hora = 12; 
                if (body.contains("\"hora\":")) {
                    String horaStr = body.split("\"hora\":")[1].split("[},]")[0].trim();
                    hora = Integer.parseInt(horaStr);
                }
                
                double distanciaTotal = 0;
                double tiempoTotal = 0;
                double costoPeajes = 0;

                HorarioTrafico horarioTemp = new HorarioTrafico();
                horarioTemp.setHoraActual(hora);
                for (int i = 0; i < ids.length - 1; i++) {
                    String idA = ids[i].trim();
                    String idB = ids[i+1].trim();
                    Lista<TramoConexion> tramos = mapa.obtenerTramosDe(idA);
                    for (int j = 0; j < tramos.tamano(); j++) {
                        TramoConexion t = tramos.obtener(j);
                        if (t.getDestino().getCodigoUnico().equals(idB)) {
                            distanciaTotal += t.getKilometrosDistancia();
                            
                            tiempoTotal += t.calcularTiempoEstimadoMinutos(horarioTemp);
                            if (t.tienePeaje()) costoPeajes += 15.0;
                            break;
                        }
                    }
                }

                String json = String.format(java.util.Locale.US,
                    "{\"distanciaTotal\":%.2f,\"tiempoMinutos\":%.0f,\"costoPeajes\":%.2f}",
                    distanciaTotal, tiempoTotal, costoPeajes);

                responder(ex, json);
            } catch (Exception e) {
                responder(ex, "{\"error\":\"" + e.getMessage() + "\"}");
            }
        });
    }

    private void responder(HttpExchange ex, String json) throws IOException {
        byte[] bytes = json.getBytes("UTF-8");
        ex.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        ex.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        ex.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        // Manejar preflight OPTIONS
        if (ex.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            ex.sendResponseHeaders(204, -1);
            return;
        }

        ex.sendResponseHeaders(200, bytes.length);
        OutputStream os = ex.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private String obtenerParam(String query, String nombre) {
        if (query == null) return "";
        for (String par : query.split("&")) {
            String[] kv = par.split("=");
            if (kv.length == 2 && kv[0].equals(nombre)) return kv[1];
        }
        return "";
    }

    private String resultadoAJson(CalculadorRuta.ResultadoRuta r) {
        if (r == null) return "{\"error\":\"No existe ruta\"}";
        StringBuilder sb = new StringBuilder();
        sb.append("{\"paradas\":[");
        var paradas = r.getCamino();
        for (int i = 0; i < paradas.tamano(); i++) {
            Ubicacion u = paradas.obtener(i);
            if (i > 0) sb.append(",");
            sb.append("{");
            sb.append("\"id\":\"").append(u.getCodigoUnico()).append("\",");
            sb.append("\"nombre\":\"").append(u.getNombreComercial()).append("\",");
            sb.append("\"lat\":").append(u.getLatitudWGS()).append(",");
            sb.append("\"lon\":").append(u.getLongitudWGS());
            sb.append("}");
        }
        sb.append("],\"distanciaTotal\":").append(r.getDistanciaTotalKm());
        sb.append("}");
        return sb.toString();
    }
}
