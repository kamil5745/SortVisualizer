import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Визуализатор: очищает экран и печатает очередной кадр.
 * Здесь же лежат общие "кирпичики" кадра (строка индексов, строка массива,
 * строка указателей), чтобы все шесть сортировок рисовали в одном стиле.
 */
public class Visualizer {

    public static final String DOUBLE_LINE = repeat('=', 72);
    public static final String SINGLE_LINE = repeat('-', 72);

    /** Ширина левой подписи строки ("Индексы:", "Массив:" и т.п.). */
    public static final int LABEL_WIDTH = 9;

    /** Режим "по шагам": ждём Enter вместо задержки. */
    public static final int STEP_BY_STEP = -1;

    private static final int SCREEN_HEIGHT = 50;

    private final int delayMs;
    private final Metrics metrics;
    private final Scanner input;

    public Visualizer(int delayMs, Metrics metrics, Scanner input) {
        this.delayMs = delayMs;
        this.metrics = metrics;
        this.input = input;
    }

    /**
     * Останавливает таймер алгоритма.
     * Вызывается перед сборкой очередного кадра, чтобы подготовка текста
     * и сама отрисовка не попали в "чистое время" сортировки.
     */
    public void pauseTimer() {
        metrics.pauseTimer();
    }

    /**
     * Показывает один кадр: полная очистка экрана + новый кадр.
     * После показа таймер алгоритма снова запускается.
     */
    public void show(String frame) {
        metrics.pauseTimer();
        clearScreen();
        System.out.println(frame);
        pause();
        metrics.resumeTimer();
    }

    /** Очистка экрана без ANSI-кодов - просто прокручиваем консоль. */
    public void clearScreen() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SCREEN_HEIGHT; i++) {
            sb.append('\n');
        }
        System.out.print(sb);
    }

    private void pause() {
        if (delayMs == STEP_BY_STEP) {
            System.out.print(" [Enter] - следующий шаг: ");
            if (input.hasNextLine()) {
                input.nextLine();
            }
            return;
        }
        if (delayMs > 0) {
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // ------------------------------------------------------------------
    // Кирпичики кадра
    // ------------------------------------------------------------------

    /** Шапка кадра: линия '=', строки заголовка, снова линия '='. */
    public static String header(String... lines) {
        StringBuilder sb = new StringBuilder();
        sb.append(DOUBLE_LINE).append('\n');
        for (String line : lines) {
            sb.append(line).append('\n');
        }
        sb.append(DOUBLE_LINE).append('\n');
        return sb.toString();
    }

    /** Ширина одной колонки: самое длинное число или индекс + 2 пробела. */
    public static int cellWidth(int[] a, int from, int to) {
        int max = 1;
        for (int i = from; i <= to; i++) {
            max = Math.max(max, String.valueOf(a[i]).length());
            max = Math.max(max, String.valueOf(i).length());
        }
        return max + 2;
    }

    public static String indexRow(int from, int to, int width) {
        StringBuilder sb = new StringBuilder(padRight(" Индексы:", LABEL_WIDTH));
        for (int i = from; i <= to; i++) {
            sb.append(padLeft(String.valueOf(i), width));
        }
        return sb.toString();
    }

    public static String arrayRow(int[] a, int from, int to, int width) {
        StringBuilder sb = new StringBuilder(padRight(" Массив:", LABEL_WIDTH));
        for (int i = from; i <= to; i++) {
            sb.append(padLeft(String.valueOf(a[i]), width));
        }
        return sb.toString();
    }

    /**
     * Строка указателей: подписи ставятся ровно под своими элементами массива.
     * marks: индекс элемента -> текст указателя, например "[L]" или "[j+1]".
     */
    public static String pointerRow(int[] a, int from, int to, int width, Map<Integer, String> marks) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, String> mark : marks.entrySet()) {
            int index = mark.getKey();
            if (index < from || index > to) {
                continue;
            }
            int lastColumn = LABEL_WIDTH + width * (index - from) + width - 1;
            int valueLength = String.valueOf(a[index]).length();
            int center = lastColumn - (valueLength - 1) / 2;
            putCentered(sb, center, mark.getValue());
        }
        return sb.toString();
    }

    /** Пустая карта указателей (индексы идут по возрастанию). */
    public static Map<Integer, String> marks() {
        return new TreeMap<Integer, String>();
    }

    /** Добавляет указатель. Если на этом индексе уже есть подпись - объединяет их. */
    public static void addMark(Map<Integer, String> marks, int index, String text) {
        if (index < 0) {
            return;
        }
        String old = marks.get(index);
        if (old == null) {
            marks.put(index, text);
        } else {
            marks.put(index, old.substring(0, old.length() - 1) + "," + text.substring(1));
        }
    }

    /** Ставит текст так, чтобы его середина попала в колонку center. */
    public static StringBuilder putCentered(StringBuilder sb, int center, String text) {
        int start = center - (text.length() - 1) / 2;
        if (start < sb.length()) {
            start = sb.length();
        }
        while (sb.length() < start) {
            sb.append(' ');
        }
        sb.append(text);
        return sb;
    }

    /** Массив одной строкой: "[ 3, 8, 2, 5 ]". */
    public static String listRange(int[] a, int from, int to) {
        StringBuilder sb = new StringBuilder("[ ");
        for (int i = from; i <= to; i++) {
            if (i > from) {
                sb.append(", ");
            }
            sb.append(a[i]);
        }
        sb.append(" ]");
        return sb.toString();
    }

    public static String list(int[] a) {
        return listRange(a, 0, a.length - 1);
    }

    public static String padLeft(String text, int width) {
        StringBuilder sb = new StringBuilder();
        while (sb.length() + text.length() < width) {
            sb.append(' ');
        }
        return sb + text;
    }

    public static String padRight(String text, int width) {
        StringBuilder sb = new StringBuilder(text);
        while (sb.length() < width) {
            sb.append(' ');
        }
        return sb.toString();
    }

    public static String repeat(char symbol, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(symbol);
        }
        return sb.toString();
    }
}
