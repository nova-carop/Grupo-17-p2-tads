package Tads;

public class ListaEnlazada<T> {

    private Nodo<T> cabeza;
    private Nodo<T> cola; // puntero al último nodo
    private int tamanio = 0;

    // Clase interna Nodo
    private static class Nodo<T> {
        T dato;
        Nodo<T> siguiente;

        Nodo(T dato) {
            this.dato = dato;
        }
    }

    public ListaEnlazada() {
        cabeza = null;
        cola = null;
    }

    // Inserta al final en O(1)
    public void insertar(T elemento) {
        Nodo<T> nuevo = new Nodo<>(elemento);
        if (cabeza == null) {
            cabeza = nuevo;
            cola = nuevo;
        } else {
            cola.siguiente = nuevo;
            cola = nuevo;
        }
        tamanio++;
    }

    public boolean estaVacia() {
        return cabeza == null;
    }

    public int tamanio() {
        return tamanio;
    }

    // Recorrer elementos
    public void recorrer(Consumidor<T> accion) {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            accion.aplicar(actual.dato);
            actual = actual.siguiente;
        }
    }

    // Interfaz funcional simple
    public interface Consumidor<T> {
        void aplicar(T elemento);
    }


    public T obtenerPrimero() {
        return cabeza != null ? cabeza.dato : null;
    }

    // En tu clase ListaEnlazada.java
    public T obtener(int indice) {
        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException("Índice fuera de rango");
        }

        Nodo<T> actual = cabeza;
        for (int i = 0; i < indice; i++) {
            actual = actual.siguiente;
        }
        return actual.dato;
    }







}


