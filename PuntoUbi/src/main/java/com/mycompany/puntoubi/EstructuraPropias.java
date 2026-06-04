package com.mycompany.puntoubi;
/**
 *
 * @author eagab
 */
public class EstructuraPropias {
    public static class Lista<T>{
        private Object[] elementos;
        private int tamano;
        private static final int CAPACIDAD_INICIAL = 10;
        
        public Lista(){
            this.elementos = new Object[CAPACIDAD_INICIAL];
            this.tamano = 0;
        }
        
        public void agregar(T elemento){
            if (tamano == elementos.length){
                expandir();
            }
            elementos[tamano++] = elemento;
        }
        
        public T obtener (int indice){
            if (indice < 0 || indice >= tamano){
                throw new IndexOutOfBoundsException("indice: " + indice + ", Tamaño: "+ tamano);
            }
            return (T) elementos[indice];
        }
        
        public int tamano(){
            return tamano;
        }
        
        public boolean estaVacia(){
            return tamano == 0;
        }
        
        public void eliminar(int indice){
            if (indice < 0 || indice >= tamano){
                throw new  IndexOutOfBoundsException("Indice: " + indice);
            }
            for (int i = indice; i < tamano - 1; i++){
                elementos[i] = elementos [i + 1];
            }
            elementos[--tamano] = null;
        }
        
        public boolean contiene(T elemento){
            for ( int i = 0; i < tamano; i++){
                if (elementos[i].equals(elemento)){
                    return true;
                }
            }
            return false;
        }
        
        public int indiceDe(T elemento){
            for (int i = 0; i < tamano; i++){
                if(elementos[i].equals(elemento)){
                   return i;
                }
            }
            return -1;
        }
        
        public void expandir(){
            Object[] nuevo = new Object[elementos.length * 2];
            for (int i = 0; i < tamano; i++){
                nuevo[i] = elementos[i];
            }
            elementos = nuevo;
        }
        
        public void limpiar(){
            for (int i = 0; i < tamano; i++){
                elementos[i] = null;
            }
            tamano = 0;
        }
    }
    
    public static class Cola<T>{
        private Nodo<T> frente;
        private Nodo<T> finalCola;
        private int tamano;
        
        private static class Nodo<T>{
            T dato;
            Nodo<T> siguiente;
            
            Nodo(T dato){
                this.dato = dato;
                this.siguiente = null;
            }
        }
        
        public Cola(){
            this.frente = null;
            this.finalCola = null;
            this.tamano = 0;
        }
        
        public void encolar(T elemento){
            Nodo<T> nuevo = new Nodo<>(elemento);
            if (finalCola == null){
                frente = finalCola = nuevo;
            }else{
                finalCola.siguiente = nuevo;
                finalCola = nuevo;
            }
            tamano++;
        }
        
        public T desencolar(){
            if (frente == null){
                throw new RuntimeException("Cola vacía");
            }
            T dato = frente.dato;
            frente = frente.siguiente;
            if(frente == null){
                finalCola = null;
            }
            tamano--;
            return dato;
        }
        
        public T verFrente(){
            if (frente == null){
                throw new RuntimeException("Cola vacía");
            }
            return frente.dato;
        }
        
        public boolean estaVacia(){
            return frente == null;
        }
        
        public int tamano(){
            return tamano;
        }
    }
    
    public static class Pila<T>{
        private Nodo<T> tope;
        private int tamano;
        
        private static class Nodo<T>{
            T dato;
            Nodo<T> siguiente;
            
            Nodo(T dato){
                this.dato = dato;
                this.siguiente = null;
            }
        }
        
        public Pila(){
            this.tope = null;
            this.tamano = 0;
        }
        
        public void apilar(T elemento){
            Nodo<T> nuevo = new Nodo<>(elemento);
            nuevo.siguiente = tope;
            tope = nuevo;
            tamano++;
        }
        
        public T desapilar(){
            if (tope == null){
                throw new RuntimeException("Pila Vacía");
            }
            T dato = tope.dato;
            tope = tope.siguiente;
            tamano--;
            return dato;
        }
        
        public T verTope(){
            if ( tope == null){
                throw new RuntimeException("Pila Vacía");
            }
            return tope.dato;
        }
        
        public boolean estaVacia(){
            return tope == null;
        }
        
        public int tamano(){
            return tamano;
        }
    }
    
    public static class ColaPrioridad{
        private NodoPrioridad[] elementos;
        private int tamano;
        
        private static class NodoPrioridad{
            String codigo;
            double distancia;
            
            NodoPrioridad (String codigo, double distancia){
                this.codigo = codigo;
                this.distancia = distancia;
            }
        }
        
        public ColaPrioridad(){
            this.elementos = new NodoPrioridad[20];
            this.tamano = 0;
        }
        
        public void insertar (String codigo, double distancia){
            if (tamano == elementos.length){
                expandir();
            }
            elementos[tamano] = new NodoPrioridad ( codigo, distancia);
            subir(tamano);
            tamano++;
        }
        
        public String extraerMinimo(){
            if (tamano == 0){
                throw new RuntimeException("Cola de pioridad vacía");
            }
            String minimo = elementos[0].codigo;
            elementos[0] = elementos[--tamano];
            bajar(0);
            return minimo;
        }
        
        public boolean estaVacia(){
            return tamano == 0;
        }
        
        public int tamano(){
            return tamano;
        }
        
        public void subir(int indice){
            while ( indice > 0){
                int padre = (indice -1) / 2;
                if (elementos[indice].distancia >= elementos[padre].distancia)
                    break;
                intercambiar(indice, padre);
                indice = padre;
            }
        }
        
        public void bajar(int indice){
            while (true){
                int menor = indice;
                int izquierda = 2 * indice +1;
                int derecha = 2 * indice +2;
                
                if (izquierda < tamano && elementos[izquierda].distancia < elementos[menor].distancia){
                    menor = izquierda;
                }
                if (derecha < tamano && elementos[derecha].distancia < elementos[menor].distancia){
                    menor = derecha;
                }
                if (menor == indice)
                    break;
                intercambiar (indice, menor);
                indice = menor;
            }
        }
        
        public void intercambiar(int i, int j){
            NodoPrioridad temp = elementos[i];
            elementos[i] = elementos[j];
            elementos[j] = temp;
        }
        
        public void expandir(){
            NodoPrioridad[] nuevo = new NodoPrioridad[elementos.length *2];
            for (int i = 0; i < tamano; i++){
                nuevo[i] = elementos[i];
            }
            elementos = nuevo;
        }
    }
    
    public static class Mapa<K,V>{
        private Par<K,V>[] tabla;
        private int capacidad;
        private int tamano;
        
        @SuppressWarnings("unchecked")
        public Mapa(){
            this.capacidad = 16;
            this.tabla = new Par[capacidad];
            this.tamano = 0;
        }
        
        public void put(K clave, V valor){
            int indice = hash(clave);
            Par<K,V> actual = tabla[indice];
            
            while (actual != null){
                if (actual.clave.equals(clave)){
                    actual.valor = valor;
                    return;
                }
                actual = actual.siguiente;
            }
            Par<K,V> nuevo = new Par<>(clave,valor);
            nuevo.siguiente = tabla[indice];
            tabla[indice] = nuevo;
            tamano++;
        }
        
        public V get(K clave){
            int indice = hash(clave);
            Par<K,V> actual = tabla[indice];
            
            while (actual != null){
                if (actual.clave.equals(clave)){
                    return actual.valor;
                }
                actual = actual.siguiente;
            }
            return null;
        }
        
        public boolean containskey(K clave){
            return get(clave) != null;
        }
        
        public int tamano(){
            return tamano;
        }
        
        public Lista<K> claves(){
            Lista<K> lista = new Lista<>();
            for (int i = 0; i < capacidad; i++){
                Par<K,V> actual = tabla[i];
                while (actual != null){
                    lista.agregar(actual.clave);
                    actual = actual.siguiente;
                }
            }
            return lista;
        }
        
        private int hash(K clave){
            return Math.abs(clave.hashCode()) % capacidad;
        }
        
        private static class Par<K,V>{
            K clave;
            V valor;
            Par<K,V> siguiente;
            
            Par(K clave, V valor){
                this.clave = clave;
                this.valor = valor;
                this.siguiente = null;
            }
        }
    }
    
    public static class Conjunto<T>{
        private Lista<T> elementos;
        
        public Conjunto(){
            this.elementos = new Lista<>();
        }
        
        public void agregar(T elemento){
            if (!elementos.contiene(elemento)){
                elementos.agregar(elemento);
            }
        }
        
        public boolean contiene(T elemento){
            return elementos.contiene(elemento);
        }
        
        public int tamano(){
            return elementos.tamano();
        }
    }
}
