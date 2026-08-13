package sortvisualizer.sort;

import java.util.Map;

import sortvisualizer.metrics.Metrics;
import sortvisualizer.viz.Visualizer;

/**
 * Сортировка вставками: берём очередной элемент (key) и вставляем его
 * на правильное место в уже отсортированную левую часть.
 * Записи считаются так: каждый сдвиг элемента вправо + сама вставка key.
 */
public class InsertionSort implements Sorter {

    @Override
    public String getName() {
        return "Сортировка вставками";
    }

    @Override
    public void sort(int[] a, Visualizer viz, Metrics metrics) {
        for (int i = 1; i < a.length; i++) {
            int key = a[i];
            int j = i - 1;
            int shifts = 0;

            viz.pauseTimer();
            viz.show(frame(a, i, j, key,
                    "Берём элемент key = " + key + " с индекса " + i + ".",
                    "Ищем ему место в отсортированной части [0.." + (i - 1) + "]."));

            while (j >= 0) {
                metrics.addComparison();
                if (a[j] > key) {
                    viz.pauseTimer();
                    viz.show(frame(a, i, j, key,
                            "Сравнение: " + a[j] + " > key = " + key + ". Элемент сдвигается вправо."));
                    a[j + 1] = a[j];
                    metrics.addWrite();
                    shifts++;
                    viz.pauseTimer();
                    viz.show(frame(a, i, j, key,
                            "Сдвиг выполнен: " + a[j + 1] + " переехал на индекс " + (j + 1) + ".",
                            "Двигаем указатель влево."));
                    j--;
                } else {
                    viz.pauseTimer();
                    viz.show(frame(a, i, j, key,
                            "Сравнение: " + a[j] + " <= key = " + key + ". Место найдено."));
                    break;
                }
            }

            if (shifts > 0) {
                a[j + 1] = key;
                metrics.addWrite();
                viz.pauseTimer();
                viz.show(frame(a, i, j, j + 1, key,
                        "Вставляем key = " + key + " на индекс " + (j + 1) + ".",
                        "Часть массива [0.." + i + "] снова отсортирована."));
            } else {
                viz.pauseTimer();
                viz.show(frame(a, i, j, key,
                        "Элемент " + key + " уже стоит на своём месте, сдвигать ничего не нужно."));
            }
        }
    }

    private String frame(int[] a, int i, int j, int key, String... texts) {
        return frame(a, i, j, -1, key, texts);
    }

    private String frame(int[] a, int i, int j, int keyPos, int key, String... texts) {
        int width = Visualizer.cellWidth(a, 0, a.length - 1);

        Map<Integer, String> marks = Visualizer.marks();
        if (j >= 0) {
            Visualizer.addMark(marks, j, "[j]");
        }
        Visualizer.addMark(marks, i, "[i]");
        if (keyPos >= 0) {
            Visualizer.addMark(marks, keyPos, "[key]");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(Visualizer.header(
                " [Сортировка вставками] Вставляем элемент key = " + key + " (взят с индекса " + i + ")"));
        sb.append(Visualizer.indexRow(0, a.length - 1, width)).append('\n');
        sb.append(Visualizer.arrayRow(a, 0, a.length - 1, width)).append('\n');
        sb.append(Visualizer.pointerRow(a, 0, a.length - 1, width, marks)).append('\n');
        sb.append(" Отсортированная часть: индексы [0..").append(i - 1).append("]\n");
        for (String text : texts) {
            sb.append(" -> ").append(text).append('\n');
        }
        sb.append(Visualizer.SINGLE_LINE);
        return sb.toString();
    }
}
