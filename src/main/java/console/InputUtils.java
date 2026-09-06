package console;

import java.util.Scanner;

public class InputUtils {

    public static int getIntInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. " + prompt);
            scanner.next();
        }
        int val = scanner.nextInt();
        scanner.nextLine();
        return val;
    }

    public static Long getLongInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextLong()) {
            System.out.print("Invalid input. " + prompt);
            scanner.next();
        }
        long val = scanner.nextLong();
        scanner.nextLine();
        return val;
    }
}