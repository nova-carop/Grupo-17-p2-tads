package Tads;

public class Nodo<K, V> {
    K clave;
    V valor;
    boolean eliminado;

    public Nodo(K clave, V valor) {
        this.clave = clave;
        this.valor = valor;
        this.eliminado = false;
    }

    public K getClave() {
        return clave;
    }

    public V getValor() {
        return valor;
    }

    public boolean isDeleted() {
        return eliminado;
    }

    public void setDeleted(boolean eliminado) {
        this.eliminado = eliminado;
    }
}
