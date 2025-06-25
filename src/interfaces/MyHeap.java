package interfaces;



public interface MyHeap<T extends Comparable<T>> {
    void insertar(T elemento);
    T eliminar();
    T verTope();
    boolean estaVacio();
    int tamaño();
}

