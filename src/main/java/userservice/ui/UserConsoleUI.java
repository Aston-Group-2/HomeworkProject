package userservice.ui;

import java.util.List;
import java.util.Scanner;

import userservice.model.User;
import userservice.service.UserService;
import userservice.service.UserServiceImpl;

public class UserConsoleUI {
    private final UserService userService = new UserServiceImpl();
    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        while (true) {
            try {
                System.out.println("\n=== МЕНЮ УПРАВЛЕНИЯ ПОЛЬЗОВАТЕЛЯМИ ===");
                System.out.println("1. Создать пользователя");
                System.out.println("2. Найти пользователя по ID");
                System.out.println("3. Показать всех пользователей");
                System.out.println("4. Обновить пользователя");
                System.out.println("5. Удалить пользователя");
                System.out.println("0. Выход");
                System.out.print("Выберите действие: ");

                int choise = Integer.parseInt(scanner.nextLine());
                switch (choise) {
                    case 1 -> createUserMenu();
                    case 2 -> findUserByIdMenu();
                    case 3 -> showAllUsersMenu();
                    case 4 -> updateUserMenu();
                    case 5 -> deleteUserMenu();
                    case 0 -> {
                        System.out.println("Выход из программы");
                        return;
                    }
                    default -> System.out.println("Неверный пункт меню, попробуйте снова.");
                }
            } catch (NumberFormatException e) {
                System.err.println("Ошибка: Вводите только числа!");
            } catch (Exception e) {
                System.err.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private void createUserMenu() {
        System.out.print("Введите имя: ");
        String name = scanner.nextLine();

        System.out.print("Введите email: ");
        String email = scanner.nextLine();

        System.out.print("Введите возраст: ");
        int age = Integer.parseInt(scanner.nextLine());

        User user = userService.create(name, age, email);
        System.out.println("Пользователь успешно создан (ID: " + user.getId() + ")!");
    }

    private void findUserByIdMenu() {
        System.out.print("Введите ID пользователя: ");
        Long id = Long.parseLong(scanner.nextLine());
        User user = userService.findById(id);
        if (user == null) {
            System.out.println("Пользователь с ID " + id + " не найден.");
        } else {
            System.out.println(user);
        }
    }

    private void deleteUserMenu() {
        System.out.print("Введите ID пользователя: ");
        Long id = Long.parseLong(scanner.nextLine());
        userService.deleteById(id);
        System.out.println("Пользователь успешно удален!");
    }

    private void showAllUsersMenu() {
        List<User> users = userService.findAll();

        if (users.isEmpty()) {
            System.out.println("База данных пуста.");
            return;
        }

        for (User user : users) {
            System.out.println(user);
        }
    }

    private void updateUserMenu() {
        System.out.print("Введите ID пользователя: ");
        Long id = Long.parseLong(scanner.nextLine());

        System.out.print("Введите имя: ");
        String name = scanner.nextLine();

        System.out.print("Введите email: ");
        String email = scanner.nextLine();

        System.out.print("Введите возраст: ");
        int age = Integer.parseInt(scanner.nextLine());

        userService.update(id, name, age, email);
        System.out.println("Пользователь успешно изменен!");
    }
}