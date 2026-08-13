package sortvisualizer.metrics;

/**
 * Счётчики выбранного алгоритма: сравнения, перестановки/записи и чистое время.
 *
 * "Чистое время" - это время работы самого алгоритма. Пока визуализатор рисует
 * кадр и ждёт (скорость анимации), таймер стоит на паузе, поэтому анимация
 * не попадает в измерение.
 */
public class Metrics {

    private final String algorithmName;

    private long comparisons;
    private long swaps;

    private long totalNs;
    private long startNs;
    private boolean running;

    public Metrics(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    /** Одно сравнение элементов. */
    public void addComparison() {
        comparisons++;
    }

    /** Один обмен элементов местами. */
    public void addSwap() {
        swaps++;
    }

    /** Одна запись значения в ячейку (используется в сортировке слиянием и вставками). */
    public void addWrite() {
        swaps++;
    }

    public void startTimer() {
        totalNs = 0;
        startNs = System.nanoTime();
        running = true;
    }

    public void pauseTimer() {
        if (running) {
            totalNs += System.nanoTime() - startNs;
            running = false;
        }
    }

    public void resumeTimer() {
        if (!running) {
            startNs = System.nanoTime();
            running = true;
        }
    }

    public void stopTimer() {
        pauseTimer();
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public long getComparisons() {
        return comparisons;
    }

    public long getSwaps() {
        return swaps;
    }

    public long getTimeNs() {
        return totalNs;
    }
}
