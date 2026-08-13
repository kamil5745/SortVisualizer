package sortvisualizer.input;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/** Ввод массива: вручную с клавиатуры или из текстового файла. */
public class InputReader {

    /** Спрашивает способ ввода и возвращает готовый массив (пустой массив не вернёт). */
    public static int[] readArray(Scanner in) {
        while (true) {
            System.out.println();
            System.out.println("Откуда взять массив?");
            System.out.println("  1 - ввести вручную с клавиатуры");
            System.out.println("  2 - прочитать из файла");
            System.out.print("Ваш выбор: ");

            String choice = in.nextLine().trim();
            int[] array = null;

            if (choice.equals("1")) {
                array = readFromKeyboard(in);
            } else if (choice.equals("2")) {
                array = readFromFile(in);
            } else {
                System.out.println("Нет такого пункта. Введите 1 или 2.");
                continue;
            }

            if (array != null && array.length > 0) {
                return array;
            }
        }
    }

    private static int[] readFromKeyboard(Scanner in) {
        System.out.print("Введите целые числа через пробел: ");
        String line = in.nextLine();
        return parse(line);
    }

    private static int[] readFromFile(Scanner in) {
        System.out.print("Введите путь к файлу: ");
        String path = in.nextLine().trim();

        File file = new File(path);
        if (!file.exists()) {
            System.out.println("Файл не найден: " + file.getAbsolutePath());
            return null;
        }

        StringBuilder text = new StringBuilder();
        Scanner fileScanner = null;
        try {
            fileScanner = new Scanner(file, "UTF-8");
            while (fileScanner.hasNextLine()) {
                text.append(fileScanner.nextLine()).append(' ');
            }
        } catch (FileNotFoundException e) {
            System.out.println("Не получилось открыть файл: " + e.getMessage());
            return null;
        } finally {
            if (fileScanner != null) {
                fileScanner.close();
            }
        }

        int[] array = parse(text.toString());
        if (array != null) {
            System.out.println("Из файла прочитано чисел: " + array.length);
        }
        return array;
    }

    /** Разбирает строку с числами. Разделители: пробелы, запятые, точки с запятой. */
    private static int[] parse(String text) {
        String[] parts = text.trim().split("[\\s,;]+");
        List<Integer> numbers = new ArrayList<Integer>();

        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            try {
                numbers.add(Integer.parseInt(part));
            } catch (NumberFormatException e) {
                System.out.println("Это не целое число: \"" + part + "\". Попробуйте ещё раз.");
                return null;
            }
        }

        if (numbers.isEmpty()) {
            System.out.println("Не найдено ни одного числа. Попробуйте ещё раз.");
            return null;
        }

        int[] array = new int[numbers.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = numbers.get(i);
        }
        return array;
    }
}
