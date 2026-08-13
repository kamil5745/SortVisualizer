package sortvisualizer.sort;

import java.util.Map;

import sortvisualizer.metrics.Metrics;
import sortvisualizer.viz.Visualizer;

/** Пузырьковая сортировка: соседние элементы сравниваются и при необходимости меняются местами. */
public class BubbleSort implements Sorter {

    @Override
    public String getName() {
        return "Пузырьковая сортировка";
    }

    @Override
    public void sort(int[] a, Visualizer viz, Metrics metrics) {
        int n = a.length;
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                metrics.addComparison();
                if (a[j] > a[j + 1]) {
                    viz.pauseTimer();
                    viz.show(frame(a, i, j,
                            "Сравнение: " + a[j] + " > " + a[j + 1] + ". Выполняется ОБМЕН."));
                    int tmp = a[j];
                    a[j] = a[j + 1];
                    a[j + 1] = tmp;
                    metrics.addSwap();
                    swapped = true;
                    viz.pauseTimer();
                    viz.show(frame(a, i, j,
                            "Обмен выполнен: " + a[j] + " <-> " + a[j + 1] + ".",
                            "Больший элемент \"всплывает\" вправо."));
                } else {
                    viz.pauseTimer();
                    viz.show(frame(a, i, j,
                            "Сравнение: " + a[j] + " <= " + a[j + 1] + ". Обмен не нужен, идём дальше."));
                }
            }
            if (!swapped) {
                viz.pauseTimer();
                viz.show(frame(a, i, -1,
                        "За весь проход не было ни одного обмена.",
                        "Массив уже отсортирован, дальше проверять нечего."));
                return;
            }
        }
    }

    private String frame(int[] a, int i, int j, String... texts) {
        int width = Visualizer.cellWidth(a, 0, a.length - 1);

        Map<Integer, String> marks = Visualizer.marks();
        if (j >= 0) {
            Visualizer.addMark(marks, j, "[j]");
            Visualizer.addMark(marks, j + 1, "[j+1]");
        }

        String title = (j >= 0)
                ? " [Пузырьковая сортировка] Проход i=" + i + ", сравнение j=" + j + " и j=" + (j + 1)
                : " [Пузырьковая сортировка] Проход i=" + i + " завершён";

        StringBuilder sb = new StringBuilder();
        sb.append(Visualizer.header(title));
        sb.append(Visualizer.indexRow(0, a.length - 1, width)).append('\n');
        sb.append(Visualizer.arrayRow(a, 0, a.length - 1, width)).append('\n');
        sb.append(Visualizer.pointerRow(a, 0, a.length - 1, width, marks)).append('\n');
        sb.append(" Отсортированный хвост: ")
                .append(i == 0 ? "пока пуст" : "последние " + i + " элемент(ов) уже на своих местах")
                .append('\n');
        for (String text : texts) {
            sb.append(" -> ").append(text).append('\n');
        }
        sb.append(Visualizer.SINGLE_LINE);
        return sb.toString();
    }
}
