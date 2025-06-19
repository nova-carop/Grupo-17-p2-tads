package Tads;

public class Nodo <K, V> {
    K clave;
    V valor;

    Nodo(K clave, V valor) {
        this.clave = clave;
        this.valor = valor;
    }
}