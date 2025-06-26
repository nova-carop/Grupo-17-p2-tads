package Tads;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TablaHash<K, V> {
    private static final double LOAD_FACTOR_THRESHOLD = 0.75;

    private List<Nodo<K, V>>[] tabla;
    private int size;
    private int capacidad;

    @SuppressWarnings("unchecked")
    public TablaHash() {
        this.capacidad = 1000;  // Empieza con una capacidad baja, crecerá sola
        this.tabla = new List[capacidad];
        for (int i = 0; i < capacidad; i++) {
            tabla[i] = new LinkedList<>();
        }
        this.size = 0;
    }

    private int hash(K clave) {
        return Math.abs(clave.hashCode()) % capacidad;
    }

    public void put(K clave, V valor) {
        if ((double) size / capacidad >= LOAD_FACTOR_THRESHOLD) {
            redimensionar();
        }

        int indice = hash(clave);
        for (Nodo<K, V> nodo : tabla[indice]) {
            if (nodo.clave.equals(clave)) {
                nodo.valor = valor;
                return;
            }
        }
        tabla[indice].add(new Nodo<>(clave, valor));
        size++;
    }

    public V get(K clave) {
        int indice = hash(clave);
        for (Nodo<K, V> nodo : tabla[indice]) {
            if (nodo.clave.equals(clave)) {
                return nodo.valor;
            }
        }
        return null;
    }

    public boolean containsKey(K clave) {
        return get(clave) != null;
    }

    public ListaEnlazada<V> values() {
        ListaEnlazada<V> lista = new ListaEnlazada<>();
        for (List<Nodo<K, V>> bucket : tabla) {
            for (Nodo<K, V> nodo : bucket) {
                lista.insertar(nodo.valor);
            }
        }
        return lista;
    }


    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    @SuppressWarnings("unchecked")
    private void redimensionar() {
        int nuevaCapacidad = capacidad * 2;
        List<Nodo<K, V>>[] nuevaTabla = new List[nuevaCapacidad];
        for (int i = 0; i < nuevaCapacidad; i++) {
            nuevaTabla[i] = new LinkedList<>();
        }

        // Rehashear todos los elementos
        for (List<Nodo<K, V>> bucket : tabla) {
            for (Nodo<K, V> nodo : bucket) {
                int nuevoIndice = Math.abs(nodo.clave.hashCode()) % nuevaCapacidad;
                nuevaTabla[nuevoIndice].add(nodo);
            }
        }

        this.tabla = nuevaTabla;
        this.capacidad = nuevaCapacidad;
    }

    public ListaEnlazada<K> claves() {
        ListaEnlazada<K> lista = new ListaEnlazada<>();
        for (List<Nodo<K, V>> bucket : tabla) {
            for (Nodo<K, V> nodo : bucket) {
                lista.insertar(nodo.clave);
            }
        }
        return lista;
    }








}