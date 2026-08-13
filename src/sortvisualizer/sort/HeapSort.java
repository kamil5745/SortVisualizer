package sortvisualizer.sort;

import sortvisualizer.metrics.Metrics;
import sortvisualizer.viz.Visualizer;

/**
 * Пирамидальная сортировка (куча).
 * Массив показывается как текстовое двоичное дерево, нарушение свойства кучи
 * помечается звёздочками: *(x)*.
 */
public class HeapSort implements Sorter {

    private static final String BUILD = "Построение кучи (Heapify)";
    private static final String SIFT = "Просеивание элемента вниз (Heapify)";
    private static final String EXTRACT = "Извлечение максимума из кучи";

    @Override
    public String getName() {
        return "Пирамидальная сортировка (куча)";
    }

    @Override
    public void sort(int[] a, Visualizer viz, Metrics metrics) {
        int n = a.length;
        int lastParent = n / 2 - 1;

        if (n <= 1) {
            viz.pauseTimer();
            viz.show(frame(a, n, -1, null, BUILD,
                    n == 0 ? "Массив пуст, сортировать нечего."
                            : "В массиве один элемент - он уже на своём месте.",
                    "Построение кучи и просеивание не нужны."));
            return;
        }

        viz.pauseTimer();
        viz.show(frame(a, n, -1, null, BUILD,
                "Строим max-кучу: просеиваем вниз все элементы, у которых есть потомки.",
                "Начинаем с индекса " + lastParent + " и идём к корню."));

        for (int i = lastParent; i >= 0; i--) {
            viz.pauseTimer();
            viz.show(frame(a, n, i, "<-- Просеиваем этот элемент", BUILD,
                    "Берём элемент (" + a[i] + ") с индекса " + i + " и опускаем его на своё место."));
            siftDown(a, n, i, BUILD, viz, metrics);
        }

        for (int end = n - 1; end > 0; end--) {
            viz.pauseTimer();
            viz.show(frame(a, end + 1, 0, "<-- Максимум кучи", EXTRACT,
                    "Корень (" + a[0] + ") - самый большой элемент кучи.",
                    "Меняем его местами с последним элементом кучи (" + a[end] + ") на индексе " + end + "."));

            swap(a, 0, end);
            metrics.addSwap();

            viz.pauseTimer();
            viz.show(frame(a, end, -1, null, EXTRACT,
                    "Обмен выполнен: элемент " + a[end] + " занял своё место в отсортированной части.",
                    "В куче осталось элементов: " + end + ", но её свойство нарушено в корне."));

            siftDown(a, end, 0, SIFT, viz, metrics);
        }

        viz.pauseTimer();
        viz.show(frame(a, 1, -1, null, EXTRACT,
                "В куче остался один элемент - он и есть минимум.",
                "Массив полностью отсортирован."));
    }

    /** Просеивание элемента вниз: меняем родителя с наибольшим потомком, пока нужно. */
    private void siftDown(int[] a, int heapSize, int start, String stage, Visualizer viz, Metrics metrics) {
        int parent = start;
        while (true) {
            int left = 2 * parent + 1;
            int right = 2 * parent + 2;
            int largest = parent;

            if (left < heapSize) {
                metrics.addComparison();
                if (a[left] > a[largest]) {
                    largest = left;
                }
            }
            if (right < heapSize) {
                metrics.addComparison();
                if (a[right] > a[largest]) {
                    largest = right;
                }
            }

            if (largest == parent) {
                if (left < heapSize) {
                    viz.pauseTimer();
                    viz.show(frame(a, heapSize, parent, "<-- Здесь всё в порядке", stage,
                            "Родитель (" + a[parent] + ") не меньше своих потомков.",
                            "Свойство кучи выполнено, просеивание закончено."));
                }
                return;
            }

            viz.pauseTimer();
            viz.show(frame(a, heapSize, parent, "<-- Нарушение (родитель меньше потомков)", stage, true,
                    "Родитель (" + a[parent] + ") меньше своего максимального потомка (" + a[largest] + ").",
                    "Выполняется обмен элементов местами."));

            swap(a, parent, largest);
            metrics.addSwap();

            viz.pauseTimer();
            viz.show(frame(a, heapSize, largest, "<-- Элемент опустился сюда", stage,
                    "Обмен выполнен: " + a[parent] + " поднялся наверх, " + a[largest] + " опустился вниз.",
                    "Продолжаем проверку с индекса " + largest + "."));

            parent = largest;
        }
    }

    private void swap(int[] a, int i, int j) {
        int tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }

    // ------------------------------------------------------------------
    // Кадры
    // ------------------------------------------------------------------

    private String frame(int[] a, int heapSize, int markNode, String markText, String stage, String... texts) {
        return frame(a, heapSize, markNode, markText, stage, false, texts);
    }

    private String frame(int[] a, int heapSize, int markNode, String markText, String stage,
                         boolean broken, String... texts) {
        StringBuilder sb = new StringBuilder();
        sb.append(Visualizer.header(" [Пирамидальная сортировка] " + stage));
        sb.append(" Текущее состояние двоичной кучи:\n");
        sb.append(tree(a, heapSize, markNode, markText, broken));
        if (heapSize < a.length) {
            sb.append(" Отсортированная часть массива: ")
                    .append(Visualizer.listRange(a, heapSize, a.length - 1)).append('\n');
        }
        sb.append(" Массив целиком: ").append(Visualizer.list(a)).append('\n');
        for (String text : texts) {
            sb.append(" -> ").append(text).append('\n');
        }
        sb.append(Visualizer.SINGLE_LINE);
        return sb.toString();
    }

    /** Рисует кучу как дерево: узлы вида (x), проблемный узел - *(x)*. */
    private String tree(int[] a, int heapSize, int markNode, String markText, boolean broken) {
        if (heapSize <= 0) {
            return " (куча пуста)\n";
        }

        int nodeWidth = 1;
        for (int i = 0; i < heapSize; i++) {
            nodeWidth = Math.max(nodeWidth, String.valueOf(a[i]).length());
        }
        int slot = nodeWidth + 4;

        int[] center = new int[heapSize];
        place(0, heapSize, center, new int[1], slot);

        StringBuilder sb = new StringBuilder();
        int first = 0;
        while (first < heapSize) {
            int last = Math.min(heapSize - 1, first * 2);

            StringBuilder nodes = new StringBuilder();
            for (int i = first; i <= last; i++) {
                String text = "(" + a[i] + ")";
                if (i == markNode) {
                    // нарушение кучи помечаем звёздочками, обычное выделение - стрелками
                    text = broken ? "*" + text + "*" : ">" + text + "<";
                }
                Visualizer.putCentered(nodes, center[i], text);
            }
            boolean noteOnSameLine = markNode >= first && markNode <= last && markText != null && markNode == last;
            boolean noteOnNextLine = markNode >= first && markNode <= last && markText != null && markNode != last;
            if (noteOnSameLine) {
                nodes.append("  ").append(markText);
            }
            sb.append(nodes).append('\n');
            if (noteOnNextLine) {
                StringBuilder note = new StringBuilder();
                int start = Math.max(0, center[markNode] - 1);
                while (note.length() < start) {
                    note.append(' ');
                }
                note.append(markText);
                sb.append(note).append('\n');
            }

            StringBuilder links = new StringBuilder();
            boolean hasLinks = false;
            for (int i = first; i <= last; i++) {
                int left = 2 * i + 1;
                int right = 2 * i + 2;
                if (left < heapSize) {
                    Visualizer.putCentered(links, (center[i] + center[left]) / 2, "/");
                    hasLinks = true;
                }
                if (right < heapSize) {
                    Visualizer.putCentered(links, (center[i] + center[right]) / 2, "\\");
                    hasLinks = true;
                }
            }
            if (hasLinks) {
                sb.append(links).append('\n');
            }

            first = last + 1;
        }
        return sb.toString();
    }

    /**
     * Считает колонку для каждого узла обходом "слева - узел - справа",
     * тогда дерево не наезжает само на себя.
     */
    private void place(int node, int heapSize, int[] center, int[] counter, int slot) {
        if (node >= heapSize) {
            return;
        }
        place(2 * node + 1, heapSize, center, counter, slot);
        center[node] = 1 + counter[0] * slot + slot / 2;
        counter[0]++;
        place(2 * node + 2, heapSize, center, counter, slot);
    }
}
