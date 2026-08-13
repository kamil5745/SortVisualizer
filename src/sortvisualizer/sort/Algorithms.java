package sortvisualizer.sort;

/** Список доступных алгоритмов сортировки для меню. */
public final class Algorithms {

    /** Список алгоритмов. Чтобы добавить новый - допишите сюда его класс. */
    public static final Sorter[] ALL = {
            new QuickSort(),
            new MergeSort(),
            new HeapSort(),
            new BubbleSort(),
            new InsertionSort(),
            new SelectionSort()
    };

    private Algorithms() {
    }
}
