package Tads;

import interfaces.MyHeap;

public class MyHeapImpl<T extends Comparable<T>> implements MyHeap<T> {
    private T[] heap;
    private int size;
    private static final int CAPACIDAD_INICIAL = 10;

    @SuppressWarnings("unchecked")
    public MyHeapImpl() {
        heap = (T[]) new Comparable[CAPACIDAD_INICIAL];
        size = 0;
    }

    @Override
    public void insertar(T elemento) {
        if (size == heap.length) {
            redimensionar();
        }
        heap[size] = elemento;
        subir(size);
        size++;
    }

    @Override
    public T eliminar() {
        if (estaVacio()) return null;
        T min = heap[0];
        heap[0] = heap[size - 1];
        size--;
        bajar(0);
        return min;
    }

    @Override
    public T verTope() {
        if (estaVacio()) return null;
        return heap[0];
    }

    @Override
    public boolean estaVacio() {
        return size == 0;
    }

    @Override
    public int tamaño() {
        return size;
    }

    @SuppressWarnings("unchecked")
    private void redimensionar() {
        T[] nuevo = (T[]) new Comparable[heap.length * 2];
        System.arraycopy(heap, 0, nuevo, 0, heap.length);
        heap = nuevo;
    }

    private void subir(int index) {
        while (index > 0) {
            int padre = (index - 1) / 2;
            if (heap[index].compareTo(heap[padre]) < 0) {
                intercambiar(index, padre);
                index = padre;
            } else {
                break;
            }
        }
    }

    private void bajar(int index) {
        while (index * 2 + 1 < size) {
            int hijoIzq = index * 2 + 1;
            int hijoDer = index * 2 + 2;
            int menor = hijoIzq;

            if (hijoDer < size && heap[hijoDer].compareTo(heap[hijoIzq]) < 0) {
                menor = hijoDer;
            }

            if (heap[menor].compareTo(heap[index]) < 0) {
                intercambiar(index, menor);
                index = menor;
            } else {
                break;
            }
        }
    }

    private void intercambiar(int i, int j) {
        T temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }
}
