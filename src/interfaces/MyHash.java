package interfaces;

import Tads.ListaEnlazada;

public interface MyHash<K, V> {

    void put(K clave, V valor);
    V get(K clave);
    boolean containsKey(K clave);


    boolean isEmpty();
    int size();


    ListaEnlazada<K> claves();
    ListaEnlazada<V> values();
}