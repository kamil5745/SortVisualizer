package sortvisualizer.menu;

import java.util.Scanner;

import sortvisualizer.metrics.Metrics;
import sortvisualizer.sort.Algorithms;
import sortvisualizer.sort.Sorter;
import sortvisualizer.viz.Visualizer;

/** Меню: выбор алгоритма, скорости, подтверждение и вывод итогов. */
public final class Menu {

    private Menu() {
    }

    public static Sorter chooseAlgorithm(Scanner in) {
        while (true) {
            System.out.println();
            System.out.println("Выберите сортировку:");
            for (int i = 0; i < Algorithms.ALL.length; i++) {
                System.out.println("  " + (i + 1) + " - " + Algorithms.ALL[i].getName());
            }
            System.out.print("Ваш выбор: ");

            String line = in.nextLine().trim();
            int number = toNumber(line);
            if (number >= 1 && number <= Algorithms.ALL.length) {
                return Algorithms.ALL[number - 1];
            }
            System.out.println("Нет такого пункта. Введите число от 1 до " + Algorithms.ALL.length + ".");
        }
    }

    /** Возвращает задержку между кадрами в миллисекундах. */
    public static int chooseSpeed(Scanner in) {
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

    public static void printResult(int[] source, int[] sorted, Metrics metrics) {
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

    public static boolean askYesNo(Scanner in, String question) {
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
