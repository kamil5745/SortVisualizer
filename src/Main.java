import java.util.Scanner;

/**
 * SortVisualizer - консольная визуализация сортировок.
 *
 * Сценарий работы:
 *   1) ввод массива (с клавиатуры или из файла);
 *   2) выбор одной сортировки из меню;
 *   3) выбор скорости анимации;
 *   4) пошаговая визуализация сортировки на копии массива;
 *   5) статистика: сравнения, перестановки/записи, чистое время в наносекундах.
 */
public class Main {

    /** Список алгоритмов. Чтобы добавить новый - допишите сюда его класс. */
    private static final Sorter[] ALGORITHMS = {
            new QuickSort(),
            new MergeSort(),
            new HeapSort(),
            new BubbleSort(),
            new InsertionSort(),
            new SelectionSort()
    };

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.println(Visualizer.DOUBLE_LINE);
        System.out.println(" SortVisualizer - пошаговая визуализация сортировок");
        System.out.println(Visualizer.DOUBLE_LINE);

        int[] source = InputReader.readArray(in);
        System.out.println("Исходный массив: " + Visualizer.list(source));
        if (source.length > 25) {
            System.out.println("Массив большой, кадров будет много - удобнее выбрать быструю скорость.");
        }

        boolean again = true;
        while (again) {
            Sorter sorter = chooseAlgorithm(in);
            int delay = chooseSpeed(in);

            int[] array = new int[source.length];
            System.arraycopy(source, 0, array, 0, source.length);

            Metrics metrics = new Metrics(sorter.getName());
            Visualizer viz = new Visualizer(delay, metrics, in);

            System.out.println();
            System.out.println("Запускаем: " + sorter.getName());
            System.out.print("Нажмите Enter для старта: ");
            in.nextLine();

            metrics.startTimer();
            sorter.sort(array, viz, metrics);
            metrics.stopTimer();

            viz.clearScreen();
            printResult(source, array, metrics);

            again = askYesNo(in, "Выбрать другую сортировку?");
        }

        System.out.println("Работа завершена.");
        in.close();
    }

    private static Sorter chooseAlgorithm(Scanner in) {
        while (true) {
            System.out.println();
            System.out.println("Выберите сортировку:");
            for (int i = 0; i < ALGORITHMS.length; i++) {
                System.out.println("  " + (i + 1) + " - " + ALGORITHMS[i].getName());
            }
            System.out.print("Ваш выбор: ");

            String line = in.nextLine().trim();
            int number = toNumber(line);
            if (number >= 1 && number <= ALGORITHMS.length) {
                return ALGORITHMS[number - 1];
            }
            System.out.println("Нет такого пункта. Введите число от 1 до " + ALGORITHMS.length + ".");
        }
    }

    /** Возвращает задержку между кадрами в миллисекундах. */
    private static int chooseSpeed(Scanner in) {
        while (true) {
            System.out.println();
            System.out.println("Выберите скорость анимации:");
            System.out.println("  1 - очень быстро (без задержки)");
            System.out.println("  2 - быстро (150 мс на кадр)");
            System.out.println("  3 - средне (400 мс на кадр)");
            System.out.println("  4 - медленно (900 мс на кадр)");
            System.out.println("  5 - вручную (следующий кадр по нажатию Enter)");
            System.out.print("Ваш выбор: ");

            String line = in.nextLine().trim();
            int number = toNumber(line);
            switch (number) {
                case 1:
                    return 0;
                case 2:
                    return 150;
                case 3:
                    return 400;
                case 4:
                    return 900;
                case 5:
                    return Visualizer.STEP_BY_STEP;
                default:
                    System.out.println("Нет такого пункта. Введите число от 1 до 5.");
            }
        }
    }

    private static void printResult(int[] source, int[] sorted, Metrics metrics) {
        System.out.println(Visualizer.DOUBLE_LINE);
        System.out.println(" СОРТИРОВКА ЗАВЕРШЕНА");
        System.out.println(Visualizer.DOUBLE_LINE);
        System.out.println(" Исходный массив:        " + Visualizer.list(source));
        System.out.println(" Отсортированный массив: " + Visualizer.list(sorted));
        System.out.println(Visualizer.SINGLE_LINE);
        System.out.println(" СТАТИСТИКА АЛГОРИТМА");
        System.out.println(" Алгоритм:              " + metrics.getAlgorithmName());
        System.out.println(" Сравнения:             " + metrics.getComparisons());
        System.out.println(" Перестановки/записи:   " + metrics.getSwaps());
        System.out.println(" Чистое время:          " + metrics.getTimeNs() + " нс");
        System.out.println(" (время без учёта отрисовки кадров и пауз анимации)");
        System.out.println(Visualizer.DOUBLE_LINE);
    }

    private static boolean askYesNo(Scanner in, String question) {
        while (true) {
            System.out.print(question + " (y/n): ");
            String answer = in.nextLine().trim().toLowerCase();
            if (answer.equals("y") || answer.equals("yes")) {
                return true;
            }
            if (answer.equals("n") || answer.equals("no")) {
                return false;
            }
            System.out.println("Не понял ответ. Введите \"y\" или \"n\".");
        }
    }

    private static int toNumber(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
