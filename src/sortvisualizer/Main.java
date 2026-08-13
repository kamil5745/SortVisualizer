package sortvisualizer;

import java.util.Scanner;

import sortvisualizer.menu.Application;
import sortvisualizer.viz.Visualizer;

/** SortVisualizer - консольная визуализация сортировок. */
public class Main {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.println(Visualizer.DOUBLE_LINE);
        System.out.println(" SortVisualizer - пошаговая визуализация сортировок");
        System.out.println(Visualizer.DOUBLE_LINE);

        new Application(in).run();

        System.out.println("Работа завершена.");
        in.close();
    }
}
