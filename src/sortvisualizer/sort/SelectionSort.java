package sortvisualizer.sort;

import java.util.Map;

import sortvisualizer.metrics.Metrics;
import sortvisualizer.viz.Visualizer;

/** Сортировка выбором: ищем минимум в неотсортированной части и ставим его на место i. */
public class SelectionSort implements Sorter {

    @Override
    public String getName() {
        return "Сортировка выбором";
    }

    @Override
    public void sort(int[] a, Visualizer viz, Metrics metrics) {
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            int min = i;

            viz.pauseTimer();
            viz.show(frame(a, i, -1, min,
                    "Ищем минимум на отрезке [" + i + ".." + (n - 1) + "].",
                    "Пока считаем минимальным элемент " + a[min] + " на индексе " + min + "."));

            for (int j = i + 1; j < n; j++) {
                metrics.addComparison();
                if (a[j] < a[min]) {
                    int previousMin = min;
                    min = j;
                    viz.pauseTimer();
                    viz.show(frame(a, i, j, min,
                            "Сравнение: " + a[j] + " < " + a[previousMin] + " (прошлый минимум). Новый минимум!",
                            "Минимум теперь на индексе " + min + "."));
                } else {
                    viz.pauseTimer();
                    viz.show(frame(a, i, j, min,
                            "Сравнение: " + a[j] + " >= " + a[min] + ". Минимум не меняется."));
                }
            }

            if (min != i) {
                viz.pauseTimer();
                viz.show(frame(a, i, -1, min,
                        "Минимум отрезка - " + a[min] + " на индексе " + min + ".",
                        "Меняем местами a[" + i + "] = " + a[i] + " и a[" + min + "] = " + a[min] + "."));
                int tmp = a[i];
                a[i] = a[min];
                a[min] = tmp;
                metrics.addSwap();
                viz.pauseTimer();
                viz.show(frame(a, i, -1, i,
                        "Обмен выполнен: " + a[i] + " встал на индекс " + i + ".",
                        "Отсортированная часть выросла до [0.." + i + "]."));
            } else {
                viz.pauseTimer();
                viz.show(frame(a, i, -1, min,
                        "Минимум уже стоит на индексе " + i + ", обмен не нужен."));
            }
        }
    }

    private String frame(int[] a, int i, int j, int min, String... texts) {
        int width = Visualizer.cellWidth(a, 0, a.length - 1);

        Map<Integer, String> marks = Visualizer.marks();
        Visualizer.addMark(marks, i, "[i]");
        if (j >= 0) {
            Visualizer.addMark(marks, j, "[j]");
        }
        Visualizer.addMark(marks, min, "[min]");

        StringBuilder sb = new StringBuilder();
        sb.append(Visualizer.header(
                " [Сортировка выбором] Шаг i=" + i + ", текущий минимум = " + a[min] + " (индекс " + min + ")"));
        sb.append(Visualizer.indexRow(0, a.length - 1, width)).append('\n');
        sb.append(Visualizer.arrayRow(a, 0, a.length - 1, width)).append('\n');
        sb.append(Visualizer.pointerRow(a, 0, a.length - 1, width, marks)).append('\n');
        sb.append(" Отсортированная часть: ")
                .append(i == 0 ? "пока пуста" : "индексы [0.." + (i - 1) + "]").append('\n');
        for (String text : texts) {
            sb.append(" -> ").append(text).append('\n');
        }
        sb.append(Visualizer.SINGLE_LINE);
        return sb.toString();
    }
}
