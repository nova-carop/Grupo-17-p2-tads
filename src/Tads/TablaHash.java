package Tads;



import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TablaHash<K, V> {
    private static final int CAPACIDAD_INICIAL = 100000;

    private List<Nodo<K, V>>[] tabla;

    @SuppressWarnings("unchecked")
    public TablaHash() {
        tabla = new List[CAPACIDAD_INICIAL];
        for (int i = 0; i < CAPACIDAD_INICIAL; i++) {
            tabla[i] = new LinkedList<>();
        }
    }

    private int hash(K clave) {
        return Math.abs(clave.hashCode()) % CAPACIDAD_INICIAL;
    }

    public void put(K clave, V valor) {
        int indice = hash(clave);
        for (Nodo<K, V> nodo : tabla[indice]) {
            if (nodo.clave.equals(clave)) {
                nodo.valor = valor;
                return;
            }
        }
        tabla[indice].add(new Nodo<>(clave, valor));
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

    public List<V> values() {
        List<V> lista = new ArrayList<>();
        for (List<Nodo<K, V>> bucket : tabla) {
            for (Nodo<K, V> nodo : bucket) {
                lista.add(nodo.valor);
            }
        }
        return lista;
    }


}
