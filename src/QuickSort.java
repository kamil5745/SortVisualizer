import java.util.Map;

/**
 * Быстрая сортировка, разбиение по Хоару (два указателя L и R идут навстречу).
 * Кадр: индексы, массив, [L] / [R], опорный элемент и текст шага.
 */
public class QuickSort implements Sorter {

    @Override
    public String getName() {
        return "Быстрая сортировка (Хоара)";
    }

    @Override
    public void sort(int[] a, Visualizer viz, Metrics metrics) {
        quickSort(a, 0, a.length - 1, viz, metrics);
    }

    private void quickSort(int[] a, int lo, int hi, Visualizer viz, Metrics metrics) {
        if (lo >= hi) {
            return;
        }
        int border = partition(a, lo, hi, viz, metrics);
        quickSort(a, lo, border, viz, metrics);
        quickSort(a, border + 1, hi, viz, metrics);
    }

    /** Разбиение Хоара: возвращает индекс, по которому диапазон делится на две части. */
    private int partition(int[] a, int lo, int hi, Visualizer viz, Metrics metrics) {
        int middle = (lo + hi) / 2;
        int pivot = a[middle];

        viz.pauseTimer();
        viz.show(frame(a, lo, hi, pivot,
                "Разбиение диапазона [" + lo + ".." + hi + "]. Опорный элемент взят из середины (индекс "
                        + middle + ").",
                "Указатель [L] пойдёт слева направо, указатель [R] - справа налево."));

        int l = lo - 1;
        int r = hi + 1;

        while (true) {
            // L ищет элемент >= pivot
            while (true) {
                l++;
                metrics.addComparison();
                if (a[l] >= pivot) {
                    break;
                }
                int rShown = r > hi ? hi : r;
                viz.pauseTimer();
                viz.show(frame(a, lo, hi, pivot, l, rShown,
                        "Сканирование: L ищет элемент >= " + pivot + ", R ищет элемент <= " + pivot + "...",
                        "Элемент " + a[l] + " меньше " + pivot + ", указатель [L] сдвигается вправо."));
            }

            // R ищет элемент <= pivot
            while (true) {
                r--;
                metrics.addComparison();
                if (a[r] <= pivot) {
                    break;
                }
                viz.pauseTimer();
                viz.show(frame(a, lo, hi, pivot, l, r,
                        "Сканирование: L ищет элемент >= " + pivot + ", R ищет элемент <= " + pivot + "...",
                        "Элемент " + a[r] + " больше " + pivot + ", указатель [R] сдвигается влево."));
            }

            if (l >= r) {
                viz.pauseTimer();
                viz.show(frame(a, lo, hi, pivot, l, r,
                        "Указатели встретились (L = " + l + ", R = " + r + ").",
                        "Диапазон [" + lo + ".." + hi + "] разбит на [" + lo + ".." + r + "] и ["
                                + (r + 1) + ".." + hi + "]."));
                return r;
            }

            viz.pauseTimer();
            viz.show(frame(a, lo, hi, pivot, l, r,
                    "НАЙДЕНО: Элемент " + a[l] + " >= " + pivot + " и Элемент " + a[r] + " <= " + pivot
                            + ". Выполняется ОБМЕН (" + a[l] + " <-> " + a[r] + ")!"));

            int tmp = a[l];
            a[l] = a[r];
            a[r] = tmp;
            metrics.addSwap();

            viz.pauseTimer();
            viz.show(frame(a, lo, hi, pivot, l, r,
                    "Обмен выполнен: теперь на позиции " + l + " стоит " + a[l] + ", на позиции " + r
                            + " стоит " + a[r] + ".",
                    "Продолжаем сканирование навстречу друг другу."));
        }
    }

    /** Кадр без указателей (начало разбиения). */
    private String frame(int[] a, int lo, int hi, int pivot, String... texts) {
        return frame(a, lo, hi, pivot, -1, -1, texts);
    }

    /** Кадр по шаблону: шапка с Pivot, индексы, массив, указатели [L]/[R], текст шага. */
    private String frame(int[] a, int lo, int hi, int pivot, int l, int r, String... texts) {
        int width = Visualizer.cellWidth(a, 0, a.length - 1);

        Map<Integer, String> marks = Visualizer.marks();
        if (l >= lo && l <= hi) {
            Visualizer.addMark(marks, l, "[L]");
        }
        if (r >= lo && r <= hi) {
            Visualizer.addMark(marks, r, "[R]");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(Visualizer.header(" [Быстрая сортировка] Опорный элемент (Pivot) = " + pivot));
        sb.append(Visualizer.indexRow(0, a.length - 1, width)).append('\n');
        sb.append(Visualizer.arrayRow(a, 0, a.length - 1, width)).append('\n');
        sb.append(Visualizer.pointerRow(a, 0, a.length - 1, width, marks)).append('\n');
        for (String text : texts) {
            sb.append(" -> ").append(text).append('\n');
        }
        sb.append(Visualizer.SINGLE_LINE);
        return sb.toString();
    }
}
