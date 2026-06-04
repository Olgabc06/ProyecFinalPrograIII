/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.puntoubi;
import com.mycompany.puntoubi.EstructuraPropias.Lista;
import com.mycompany.puntoubi.EstructuraPropias.Conjunto;
import com.mycompany.puntoubi.EstructuraPropias.Cola;
/**
 *
 * @author Gabriela
 */
public class RedVial {
    private class RegistroNodo {
        Ubicacion punto;
        Lista<TramoConexion> tramos;

        RegistroNodo(Ubicacion punto) {
            this.punto = punto;
            this.tramos = new Lista<>();
        }
    }

    private final Lista<RegistroNodo> mapaNodos;

    public RedVial() {
        this.mapaNodos = new Lista<>();
    }

    public void registrarNuevaUbicacion(Ubicacion u) {
        if (buscarRegistro(u.getCodigoUnico()) == null) {
            mapaNodos.agregar(new RegistroNodo(u));
        }
    }

    public void enlazarRuta(String codigoOrigen, String codigoDestino, double velocidadKmh,String tipoVia, boolean tienePeaje, boolean esRutaNacional) {
        RegistroNodo orig = buscarRegistro(codigoOrigen);
        RegistroNodo dest = buscarRegistro(codigoDestino);

        if (orig != null && dest != null) {
            double distancia = CalculadorDistancias.entreCoordenadas(orig.punto.getLatitudWGS(), orig.punto.getLongitudWGS(),dest.punto.getLatitudWGS(), dest.punto.getLongitudWGS());

            orig.tramos.agregar(new TramoConexion(dest.punto, distancia, velocidadKmh, tipoVia, tienePeaje, esRutaNacional));
            dest.tramos.agregar(new TramoConexion(orig.punto, distancia, velocidadKmh, tipoVia, tienePeaje, esRutaNacional));
        }
    }

    public void enlazarRuta(String codigoOrigen, String codigoDestino) {
        enlazarRuta(codigoOrigen, codigoDestino, 60.0, "CARRETERA", false, true);
    }

    public void eliminarRuta(String codigoOrigen, String codigoDestino) {
        RegistroNodo orig = buscarRegistro(codigoOrigen);
        RegistroNodo dest = buscarRegistro(codigoDestino);

        if (orig != null && dest != null) {
            for (int i = 0; i < orig.tramos.tamano(); i++) {
                if (orig.tramos.obtener(i).getDestino().getCodigoUnico().equals(dest.punto.getCodigoUnico())) {
                    orig.tramos.eliminar(i);
                    break;
                }
            }
            for (int i = 0; i < dest.tramos.tamano(); i++) {
                if (dest.tramos.obtener(i).getDestino().getCodigoUnico().equals(orig.punto.getCodigoUnico())) {
                    dest.tramos.eliminar(i);
                    break;
                }
            }
        }
    }

    public void eliminarUbicacion(String codigo) {
        String codigoBuscar = codigo.toUpperCase().trim();
        int indiceEliminar = -1;

        for (int i = 0; i < mapaNodos.tamano(); i++) {
            if (mapaNodos.obtener(i).punto.getCodigoUnico().equals(codigoBuscar)) {
                indiceEliminar = i;
                break;
            }
        }

        if (indiceEliminar == -1) return;

        RegistroNodo nodoEliminar = mapaNodos.obtener(indiceEliminar);
        for (int i = 0; i < mapaNodos.tamano(); i++) {
            RegistroNodo actual = mapaNodos.obtener(i);
            if (actual == nodoEliminar) continue;
            for (int j = 0; j < actual.tramos.tamano(); j++) {
                if (actual.tramos.obtener(j).getDestino().getCodigoUnico().equals(codigoBuscar)) {
                    actual.tramos.eliminar(j);
                    j--;
                }
            }
        }

        mapaNodos.eliminar(indiceEliminar);
    }

    
    public boolean existeUbicacion(String codigo) {
        return buscarRegistro(codigo.toUpperCase().trim()) != null;
    }

    public void imprimirUbicaciones() {
        System.out.println("  ┌──────┬────────────────────────────────────────┐");
        System.out.println("  │ CÓD  │ NOMBRE                           │");
        System.out.println("  ├──────┼────────────────────────────────────────┤");
        for (int i = 0; i < mapaNodos.tamano(); i++) {
            Ubicacion u = mapaNodos.obtener(i).punto;
            System.out.printf("  │ %-4s │ %-38s │%n", u.getCodigoUnico(), u.getNombreComercial());
        }
        System.out.println("  └──────┴────────────────────────────────────────┘");
    }

    public boolean existeConexionDirecta(String codigoA, String codigoB) {
        RegistroNodo nodoA = buscarRegistro(codigoA);
        if (nodoA != null) {
            for (int i = 0; i < nodoA.tramos.tamano(); i++) {
                if (nodoA.tramos.obtener(i).getDestino().getCodigoUnico().equals(codigoB.toUpperCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean existeCamino(String codigoA, String codigoB) {
        String inicio = codigoA.toUpperCase().trim();
        String fin = codigoB.toUpperCase().trim();

        if (inicio.equals(fin)) return true;

        Conjunto<String> visitados = new Conjunto<>();
        Cola<String> cola = new Cola<>();

        cola.encolar(inicio);
        visitados.agregar(inicio);

        while (!cola.estaVacia()) {
            String actual = cola.desencolar();
            RegistroNodo nodoActual = buscarRegistro(actual);

            if (nodoActual != null) {
                for (int i = 0; i < nodoActual.tramos.tamano(); i++) {
                    String vecino = nodoActual.tramos.obtener(i).getDestino().getCodigoUnico();
                    if (vecino.equals(fin)) {
                        return true;
                    }
                    if (!visitados.contiene(vecino)) {
                        visitados.agregar(vecino);
                        cola.encolar(vecino);
                    }
                }
            }
        }
        return false;
    }

    private RegistroNodo buscarRegistro(String codigo) {
        String codigoBuscar = codigo.toUpperCase().trim();
        for (int i = 0; i < mapaNodos.tamano(); i++) {
            RegistroNodo reg = mapaNodos.obtener(i);
            if (reg.punto.getCodigoUnico().equals(codigoBuscar)) {
                return reg;
            }
        }
        return null;
    }

    public Lista<Ubicacion> obtenerCatalogoUbicaciones() {
        Lista<Ubicacion> lista = new Lista<>();
        for (int i = 0; i < mapaNodos.tamano(); i++) {
            lista.agregar(mapaNodos.obtener(i).punto);
        }
        return lista;
    }

    public Lista<TramoConexion> obtenerTramosDe(String codigo) {
        RegistroNodo reg = buscarRegistro(codigo);
        return (reg != null) ? reg.tramos : new Lista<>();
    }

    public void imprimirEstructuraRed() {
        System.out.println("---MAPA DE CONEXIONES (RED VIAL)----");
        for (int i = 0; i < mapaNodos.tamano(); i++) {
            RegistroNodo reg = mapaNodos.obtener(i);
            System.out.println("\n┌─ " + reg.punto.getNombreComercial() + " [" + reg.punto.getCodigoUnico() + "]");
            System.out.println("│  Coordenadas: " + reg.punto.getLatitudWGS() + ", " + reg.punto.getLongitudWGS());
            System.out.println("│  Conexiones:");

            for (int j = 0; j < reg.tramos.tamano(); j++) {
                TramoConexion tramo = reg.tramos.obtener(j);
                System.out.printf("│    → %-20s | %5.1f km | %s | %s%n",
                    tramo.getDestino().getNombreComercial(),
                    tramo.getKilometrosDistancia(),
                    tramo.getTipoVia(),
                    tramo.tienePeaje() ? "Q" : "Gratis");
            }
        }
    }  
}
