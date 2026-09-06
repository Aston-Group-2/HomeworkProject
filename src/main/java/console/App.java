package console;

import userservice.services.UserService;
import userservice.util.HibernateUtil;

import java.util.Scanner;

public class App {

    private static final UserService userService = new UserService();
    private static final Scanner scanner = new Scanner(System.in);

    public void run() {
        System.out.println("=== User Service Console App ===");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = InputUtils.getIntInput(scanner, "Choose an option: ");

            try {
                MenuAction action = MenuAction.fromChoice(choice);

                if (action == MenuAction.EXIT) {
                    running = false;
                }

                action.execute(scanner, userService);

            } catch (IllegalArgumentException e) {
                System.err.println(" Invalid option. Please choose 1-" + MenuAction.values().length);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }

            System.out.println("-----------------------------");
        }

        HibernateUtil.shutdown();
    }

    private static void printMenu() {
        System.out.println("\nMenu:");
        for (int i = 0; i < MenuAction.values().length; i++) {
            System.out.printf("%d. %s%n", i + 1, MenuAction.values()[i].getLabel());
        }
    }
}