package Tads;


import java.util.Iterator;
import java.util.NoSuchElementException;

public class ColaPrioridad<T extends Comparable<T>> implements Iterable<T> {

    private Nodo<T> cabeza;

    // Inserta un elemento manteniendo la prioridad (mayor primero)
    public void insertar(T elemento) {
        Nodo<T> nuevo = new Nodo<>(elemento);

        // Si la lista está vacía o el nuevo tiene más prioridad que la cabeza
        if (cabeza == null || nuevo.dato.compareTo(cabeza.dato) > 0) {
            nuevo.siguiente = cabeza;
            cabeza = nuevo;
        } else {
            Nodo<T> actual = cabeza;
            // Buscar la posición correcta para insertar
            while (actual.siguiente != null && nuevo.dato.compareTo(actual.siguiente.dato) <= 0) {
                actual = actual.siguiente;
            }
            nuevo.siguiente = actual.siguiente;
            actual.siguiente = nuevo;
        }
    }

    // Extrae el elemento con mayor prioridad (al frente)
    public T extraer() {
        if (cabeza == null) return null;
        T dato = cabeza.dato;
        cabeza = cabeza.siguiente;
        return dato;
    }

    public boolean estaVacia() {
        return cabeza == null;
    }

    // Devuelve un iterador para recorrer los elementos en orden de prioridad
    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private Nodo<T> actual = cabeza;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                T dato = actual.dato;
                actual = actual.siguiente;
                return dato;
            }
        };
    }

    // Clase interna para nodos de la lista
    private static class Nodo<T> {
        T dato;
        Nodo<T> siguiente;

        public Nodo(T dato) {
            this.dato = dato;
            this.siguiente = null;
        }
    }
}
