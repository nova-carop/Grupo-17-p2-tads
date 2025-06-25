package Tads;

import interfaces.MyHash;

public class TablaHash<K, V> implements MyHash<K, V> {
    private static final double LOAD_FACTOR_THRESHOLD = 0.75;

    private ListaEnlazada<Nodo<K, V>>[] tabla;
    private int size;
    private int capacidad;

    @SuppressWarnings("unchecked")
    public TablaHash() {
        this.capacidad = 1000;
        this.tabla = new ListaEnlazada[capacidad];
        for (int i = 0; i < capacidad; i++) {
            tabla[i] = new ListaEnlazada<>();
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
        ListaEnlazada<Nodo<K, V>> bucket = tabla[indice];

        // Buscar si la clave ya existe
        ListaEnlazada.Nodo<Nodo<K, V>> actual = bucket.getCabeza();
        while (actual != null) {
            Nodo<K, V> nodo = actual.getDato();
            if (!nodo.isDeleted() && nodo.getClave().equals(clave)) {
                nodo.valor = valor; // Actualizar valor existente
                return;
            }
            actual = actual.getSiguiente();
        }

        // Si no existe, agregar nuevo nodo
        bucket.insertar(new Nodo<>(clave, valor));
        size++;
    }

    public V get(K clave) {
        int indice = hash(clave);
        ListaEnlazada<Nodo<K, V>> bucket = tabla[indice];

        ListaEnlazada.Nodo<Nodo<K, V>> actual = bucket.getCabeza();
        while (actual != null) {
            Nodo<K, V> nodo = actual.getDato();
            if (!nodo.isDeleted() && nodo.getClave().equals(clave)) {
                return nodo.getValor();
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    public boolean containsKey(K clave) {
        return get(clave) != null;
    }

    public ListaEnlazada<V> values() {
        ListaEnlazada<V> lista = new ListaEnlazada<>();
        for (int i = 0; i < capacidad; i++) {
            ListaEnlazada<Nodo<K, V>> bucket = tabla[i];
            ListaEnlazada.Nodo<Nodo<K, V>> actual = bucket.getCabeza();
            while (actual != null) {
                Nodo<K, V> nodo = actual.getDato();
                if (!nodo.isDeleted()) {
                    lista.insertar(nodo.getValor());
                }
                actual = actual.getSiguiente();
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
        ListaEnlazada<Nodo<K, V>>[] nuevaTabla = new ListaEnlazada[nuevaCapacidad];

        for (int i = 0; i < nuevaCapacidad; i++) {
            nuevaTabla[i] = new ListaEnlazada<>();
        }

        // Rehashear todos los elementos no eliminados
        for (int i = 0; i < capacidad; i++) {
            ListaEnlazada<Nodo<K, V>> bucket = tabla[i];
            ListaEnlazada.Nodo<Nodo<K, V>> actual = bucket.getCabeza();
            while (actual != null) {
                Nodo<K, V> nodo = actual.getDato();
                if (!nodo.isDeleted()) {
                    int nuevoIndice = Math.abs(nodo.getClave().hashCode()) % nuevaCapacidad;
                    nuevaTabla[nuevoIndice].insertar(nodo);
                }
                actual = actual.getSiguiente();
            }
        }

        this.tabla = nuevaTabla;
        this.capacidad = nuevaCapacidad;
    }

    public ListaEnlazada<K> claves() {
        ListaEnlazada<K> lista = new ListaEnlazada<>();
        for (int i = 0; i < capacidad; i++) {
            ListaEnlazada<Nodo<K, V>> bucket = tabla[i];
            ListaEnlazada.Nodo<Nodo<K, V>> actual = bucket.getCabeza();
            while (actual != null) {
                Nodo<K, V> nodo = actual.getDato();
                if (!nodo.isDeleted()) {
                    lista.insertar(nodo.getClave());
                }
                actual = actual.getSiguiente();
            }
        }
        return lista;
    }
}