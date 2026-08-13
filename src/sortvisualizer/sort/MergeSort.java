package sortvisualizer.sort;

import sortvisualizer.metrics.Metrics;
import sortvisualizer.viz.Visualizer;

/**
 * Сортировка слиянием.
 *
 * Кадр состоит из четырёх блоков:
 *   1) индикатор рекурсии (деление/слияние и глубина ">>")
 *   2) зона основного массива (границы подмассивов и указатели [L] / [R])
 *   3) зона временного буфера ("_" - пусто)
 *   4) текстовый лог шага
 *
 * Метрики:
 *   - сравнения считаются только для пары "элемент слева vs элемент справа";
 *   - записи считаются и при копировании в буфер, и при возврате в массив.
 */
public class MergeSort implements Sorter {

    @Override
    public String getName() {
        return "Сортировка слиянием";
    }

    @Override
    public void sort(int[] a, Visualizer viz, Metrics metrics) {
        int[] buffer = new int[a.length];
        mergeSort(a, buffer, 0, a.length - 1, 0, viz, metrics);
    }

    private void mergeSort(int[] a, int[] buffer, int lo, int hi, int depth, Visualizer viz, Metrics metrics) {
        if (lo >= hi) {
            return;
        }
        int mid = (lo + hi) / 2;
        viz.pauseTimer();
        viz.show(splitFrame(a, lo, hi, mid, depth));
        mergeSort(a, buffer, lo, mid, depth + 1, viz, metrics);
        mergeSort(a, buffer, mid + 1, hi, depth + 1, viz, metrics);
        merge(a, buffer, lo, mid, hi, depth, viz, metrics);
    }

    private void merge(int[] a, int[] buffer, int lo, int mid, int hi, int depth,
                       Visualizer viz, Metrics metrics) {
        int[] before = new int[a.length];
        System.arraycopy(a, 0, before, 0, a.length);

        int i = lo;
        int j = mid + 1;
        int k = lo;

        while (i <= mid && j <= hi) {
            metrics.addComparison();
            if (a[i] <= a[j]) {
                boolean equal = a[i] == a[j];
                buffer[k] = a[i];
                metrics.addWrite();
                viz.pauseTimer();
                viz.show(mergeFrame(a, buffer, lo, mid, hi, i, j, k, depth,
                        " -> Логика: Сравниваем элементы под указателями: " + a[i] + " <= " + a[j] + ".",
                        " -> Результат: Элемент " + a[i] + (equal ? " равен правому, берём левый" : " меньше")
                                + ", копируем его в буфер на позицию " + k + ".",
                        " -> Действие: Указатель [L] смещается вправо на индекс " + (i + 1) + "."));
                i++;
            } else {
                buffer[k] = a[j];
                metrics.addWrite();
                viz.pauseTimer();
                viz.show(mergeFrame(a, buffer, lo, mid, hi, i, j, k, depth,
                        " -> Логика: Сравниваем элементы под указателями: " + a[i] + " > " + a[j] + ".",
                        " -> Результат: Элемент " + a[j] + " меньше, копируем его в буфер на позицию " + k + ".",
                        " -> Действие: Указатель [R] смещается вправо на индекс " + (j + 1) + "."));
                j++;
            }
            k++;
        }

        // Хвосты. Сравнений здесь нет - просто дописываем то, что осталось.
        while (i <= mid) {
            buffer[k] = a[i];
            metrics.addWrite();
            viz.pauseTimer();
            viz.show(mergeFrame(a, buffer, lo, mid, hi, i, -1, k, depth,
                    " -> Логика: Правая часть закончилась, сравнивать больше не с чем.",
                    " -> Результат: Копируем остаток левой части: элемент " + a[i] + " на позицию " + k + ".",
                    " -> Действие: Указатель [L] смещается вправо на индекс " + (i + 1) + "."));
            i++;
            k++;
        }
        while (j <= hi) {
            buffer[k] = a[j];
            metrics.addWrite();
            viz.pauseTimer();
            viz.show(mergeFrame(a, buffer, lo, mid, hi, -1, j, k, depth,
                    " -> Логика: Левая часть закончилась, сравнивать больше не с чем.",
                    " -> Результат: Копируем остаток правой части: элемент " + a[j] + " на позицию " + k + ".",
                    " -> Действие: Указатель [R] смещается вправо на индекс " + (j + 1) + "."));
            j++;
            k++;
        }

        for (int t = lo; t <= hi; t++) {
            a[t] = buffer[t];
            metrics.addWrite();
        }
        viz.pauseTimer();
        viz.show(syncFrame(before, buffer, a, lo, hi));
    }

    // ------------------------------------------------------------------
    // Кадры
    // ------------------------------------------------------------------

    /** Этап 1: разделение массива. */
    private String splitFrame(int[] a, int lo, int hi, int mid, int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(Visualizer.header(
                " [Сортировка слиянием] РАЗДЕЛЕНИЕ МАССИВА",
                " Глубина рекурсии: " + depthText(depth)));
        sb.append(" Текущее состояние задачи:\n");
        sb.append(" Активный диапазон: индексы [").append(lo).append("..").append(hi).append("]\n");
        sb.append(" ").append(Visualizer.listRange(a, lo, hi)).append('\n');
        sb.append(" -> Разделение по центру (индекс ").append(mid).append(")\n");
        sb.append(" Результат деления:\n");
        sb.append(" Левая часть:  ").append(Visualizer.listRange(a, lo, mid)).append('\n');
        sb.append(" Правая часть: ").append(Visualizer.listRange(a, mid + 1, hi)).append('\n');
        sb.append(" -> Задача разбита на две подзадачи. Переход к левой части...\n");
        sb.append(Visualizer.SINGLE_LINE);
        return sb.toString();
    }

    /** Этап 2: слияние подмассивов. */
    private String mergeFrame(int[] a, int[] buffer, int lo, int mid, int hi,
                              int i, int j, int filledTo, int depth, String... texts) {
        StringBuilder sb = new StringBuilder();
        sb.append(Visualizer.header(
                " [Сортировка слиянием] СЛИЯНИЕ ПОДМАССИВОВ [" + lo + ".." + mid + "] и ["
                        + (mid + 1) + ".." + hi + "]",
                " Глубина рекурсии: " + depthText(depth)));
        sb.append(" ИСХОДНЫЕ ПОДМАССИВЫ (в основном массиве):\n");
        sb.append(subArrayBlock(a, lo, mid, hi, i, j));
        sb.append(" ВРЕМЕННЫЙ БУФЕР (Выделенная память O(N)):\n");
        sb.append(bufferBlock(buffer, lo, hi, filledTo));
        for (String text : texts) {
            sb.append(text).append('\n');
        }
        sb.append(Visualizer.SINGLE_LINE);
        return sb.toString();
    }

    /** Этап 3: перезапись отсортированного куска из буфера в основной массив. */
    private String syncFrame(int[] before, int[] buffer, int[] a, int lo, int hi) {
        int labelWidth = 26;
        StringBuilder sb = new StringBuilder();
        sb.append(Visualizer.header(" [Сортировка слиянием] СИНХРОНИЗАЦИЯ С ОСНОВНЫМ МАССИВОМ"));
        sb.append(Visualizer.padRight(" МАССИВ ДО ОБНОВЛЕНИЯ:", labelWidth))
                .append(Visualizer.listRange(before, lo, hi)).append('\n');
        sb.append(Visualizer.padRight(" ВРЕМЕННЫЙ БУФЕР:", labelWidth))
                .append(Visualizer.listRange(buffer, lo, hi)).append('\n');
        sb.append(" ===========> Перезапись области [").append(lo).append("..").append(hi).append("]\n");
        sb.append(Visualizer.padRight(" МАССИВ ПОСЛЕ ОБНОВЛЕНИЯ:", labelWidth))
                .append(Visualizer.listRange(a, lo, hi)).append('\n');
        sb.append(" -> Подмассив [").append(lo).append("..").append(hi)
                .append("] успешно отсортирован и возвращен в основную память.\n");
        sb.append(Visualizer.SINGLE_LINE);
        return sb.toString();
    }

    /**
     * Блок основного массива: индексы, данные с разделителем "|" между половинами,
     * стрелки "^" и подписи [L] / [R] под текущими элементами.
     */
    private String subArrayBlock(int[] a, int lo, int mid, int hi, int i, int j) {
        int valueWidth = 1;
        int indexWidth = 1;
        for (int t = lo; t <= hi; t++) {
            valueWidth = Math.max(valueWidth, String.valueOf(a[t]).length());
            indexWidth = Math.max(indexWidth, String.valueOf(t).length());
        }
        int cellWidth = Math.max(valueWidth + 2, indexWidth + 2);

        StringBuilder indexes = new StringBuilder(Visualizer.padRight(" Индексы:", 10));
        StringBuilder data = new StringBuilder(Visualizer.padRight(" Данные:", 10));
        int leftCenter = -1;
        int rightCenter = -1;

        for (int t = lo; t <= hi; t++) {
            String value = Visualizer.padLeft(String.valueOf(a[t]), cellWidth - 2);
            String cell = (t == i || t == j) ? "[" + value + "]" : " " + value + " ";
            int start = data.length();
            data.append(cell);

            int center = start + (cellWidth - 1) / 2;
            Visualizer.putCentered(indexes, center, String.valueOf(t));
            if (t == i) {
                leftCenter = center;
            }
            if (t == j) {
                rightCenter = center;
            }

            if (t == mid && t < hi) {
                data.append(" | ");
            } else if (t < hi) {
                data.append(' ');
            }
        }

        StringBuilder arrows = new StringBuilder();
        StringBuilder names = new StringBuilder();
        if (leftCenter >= 0) {
            Visualizer.putCentered(arrows, leftCenter, "^");
            Visualizer.putCentered(names, leftCenter, "[L]");
        }
        if (rightCenter >= 0) {
            Visualizer.putCentered(arrows, rightCenter, "^");
            Visualizer.putCentered(names, rightCenter, "[R]");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(indexes).append('\n');
        sb.append(data.toString().replaceAll("\\s+$", "")).append('\n');
        sb.append(arrows).append('\n');
        sb.append(names).append('\n');
        return sb.toString();
    }

    /** Блок временного буфера: заполненные позиции показываем числами, пустые - "_". */
    private String bufferBlock(int[] buffer, int lo, int hi, int filledTo) {
        int valueWidth = 1;
        int indexWidth = 1;
        for (int t = lo; t <= hi; t++) {
            if (t <= filledTo) {
                valueWidth = Math.max(valueWidth, String.valueOf(buffer[t]).length());
            }
            indexWidth = Math.max(indexWidth, String.valueOf(t).length());
        }
        int width = Math.max(valueWidth, indexWidth) + 2;

        StringBuilder indexes = new StringBuilder(Visualizer.padRight(" Индексы:", Visualizer.LABEL_WIDTH));
        for (int t = lo; t <= hi; t++) {
            indexes.append(Visualizer.padLeft(String.valueOf(t), width));
        }

        StringBuilder cells = new StringBuilder(Visualizer.padRight(" Буфер:", Visualizer.LABEL_WIDTH + 1));
        cells.append('[');
        for (int t = lo; t <= hi; t++) {
            if (t > lo) {
                cells.append(", ");
            }
            String value = (t <= filledTo) ? String.valueOf(buffer[t]) : "_";
            cells.append(Visualizer.padLeft(value, width - 2));
        }
        cells.append(']');

        return indexes + "\n" + cells + "\n";
    }

    private String depthText(int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            sb.append(">> ");
        }
        return sb + "(Уровень " + depth + ")";
    }
}
