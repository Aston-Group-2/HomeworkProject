package console;

import userservice.services.UserService;

import java.util.Scanner;

public enum MenuAction {
    CREATE_USER("Create User") {
        @Override
        public void execute(Scanner scanner, UserService userService) {
            System.out.print("Enter name: ");
            String name = scanner.nextLine();
            System.out.print("Enter email: ");
            String email = scanner.nextLine();
            int age = InputUtils.getIntInput(scanner, "Enter age: ");

            var user = userService.createUser(name, email, age);
            System.out.println("User created: " + user);
        }
    },
    READ_USER("Read User by ID") {
        @Override
        public void execute(Scanner scanner, UserService userService) {
            Long id = InputUtils.getLongInput(scanner, "Enter user ID: ");
            userService.getUserById(id)
                    .ifPresentOrElse(
                            user -> System.out.println(" Found: " + user),
                            () -> System.out.println("⚠ser not found.")
                    );
        }
    },
    READ_ALL("Read All Users") {
        @Override
        public void execute(Scanner scanner, UserService userService) {
            var users = userService.getAllUsers();
            if (users.isEmpty()) {
                System.out.println("No users found.");
            } else {
                System.out.println("All users:");
                users.forEach(System.out::println);
            }
        }
    },
    UPDATE_USER("Update User") {
        @Override
        public void execute(Scanner scanner, UserService userService) {
            Long id = InputUtils.getLongInput(scanner, "Enter user ID to update: ");
            System.out.print("Enter new name: ");
            String name = scanner.nextLine();
            System.out.print("Enter new email: ");
            String email = scanner.nextLine();
            int age = InputUtils.getIntInput(scanner, "Enter new age: ");

            var user = userService.updateUser(id, name, email, age);
            System.out.println("User updated: " + user);
        }
    },
    DELETE_USER("Delete User") {
        @Override
        public void execute(Scanner scanner, UserService userService) {
            Long id = InputUtils.getLongInput(scanner, "Enter user ID to delete: ");
            userService.deleteUser(id);
            System.out.println("User deleted.");
        }
    },
    EXIT("Exit") {
        @Override
        public void execute(Scanner scanner, UserService userService) {
            System.out.println("Exiting application...");
        }
    };

    private final String label;

    MenuAction(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public abstract void execute(Scanner scanner, UserService userService);

    public static MenuAction fromChoice(int choice) {
        if (choice < 1 || choice > values().length) {
            throw new IllegalArgumentException("Invalid option: " + choice);
        }
        return values()[choice - 1];
    }
}