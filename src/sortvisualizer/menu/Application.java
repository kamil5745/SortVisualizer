package sortvisualizer.menu;

import java.util.Scanner;

import sortvisualizer.input.InputReader;
import sortvisualizer.metrics.Metrics;
import sortvisualizer.sort.Sorter;
import sortvisualizer.viz.Visualizer;

/**
 * Сценарий работы SortVisualizer:
 *   1) ввод массива (с клавиатуры или из файла);
 *   2) выбор одной сортировки из меню;
 *   3) выбор скорости анимации;
 *   4) пошаговая визуализация сортировки на копии массива;
 *   5) статистика: сравнения, перестановки/записи, чистое время в наносекундах.
 */
public class Application {

    private final Scanner in;

    public Application(Scanner in) {
        this.in = in;
    }

    public void run() {
        int[] source = InputReader.readArray(in);
        System.out.println("Исходный массив: " + Visualizer.list(source));
        if (source.length > 25) {
            System.out.println("Массив большой, кадров будет много - удобнее выбрать быструю скорость.");
        }

        boolean again = true;
        while (again) {
            Sorter sorter = Menu.chooseAlgorithm(in);
            int delay = Menu.chooseSpeed(in);

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
            Menu.printResult(source, array, metrics);

            again = Menu.askYesNo(in, "Выбрать другую сортировку?");
        }
    }
}
