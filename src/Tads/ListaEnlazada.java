package Tads;

public class ListaEnlazada<T> {
    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private int tamanio;

    public static class Nodo<T> {
        private T dato;
        private Nodo<T> siguiente;

        public Nodo(T dato) {
            this.dato = dato;
            this.siguiente = null;
        }

        public T getDato() {
            return dato;
        }

        public Nodo<T> getSiguiente() {
            return siguiente;
        }

        public void setSiguiente(Nodo<T> siguiente) {
            this.siguiente = siguiente;
        }
    }

    public ListaEnlazada() {
        cabeza = null;
        cola = null;
        tamanio = 0;
    }

    public void insertar(T elemento) {
        Nodo<T> nuevo = new Nodo<>(elemento);
        if (cabeza == null) {
            cabeza = nuevo;
            cola = nuevo;
        } else {
            cola.setSiguiente(nuevo);
            cola = nuevo;
        }
        tamanio++;
    }

    public void insertarAlFinal(T elemento) {
        insertar(elemento);
    }

    public boolean estaVacia() {
        return cabeza == null;
    }

    public int tamanio() {
        return tamanio;
    }

    public Nodo<T> getCabeza() {
        return cabeza;
    }

    public void setCabeza(Nodo<T> cabeza) {
        this.cabeza = cabeza;
    }

    public void recorrer(Consumidor<T> accion) {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            accion.aplicar(actual.getDato());
            actual = actual.getSiguiente();
        }
    }

    public interface Consumidor<T> {
        void aplicar(T elemento);
    }

    public void clear() {
        cabeza = null;
        cola = null;
        tamanio = 0;
    }


    public T obtener(int posicion) {
        if (posicion < 0 || posicion >= tamanio) {
            throw new IndexOutOfBoundsException("Índice fuera de rango");
        }

        Nodo<T> actual = cabeza;
        for (int i = 0; i < posicion; i++) {
            actual = actual.getSiguiente();
        }
        return actual.getDato();
    }

}