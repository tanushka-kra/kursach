import java.util.Scanner;

public class MainApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("===== СЕТЕВОЙ АНАЛИЗАТОР =====");
        System.out.println("1. Запустить сервер (приемник)");
        System.out.println("2. Запустить клиент (генератор)");
        System.out.print("Выберите опцию: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                System.out.print("Введите порт сервера (1-65535): ");
                int serverPort = readValidPort(scanner);
                if (serverPort > 0) {
                    Server.main(new String[]{String.valueOf(serverPort)});
                }
                break;
            case 2:
                System.out.print("Введите хост сервера: ");
                String host = scanner.nextLine();

                System.out.print("Введите порт сервера (1-65535): ");
                int clientPort = readValidPort(scanner);
                if (clientPort <= 0) break;

                System.out.print("Введите размер пакета (в байтах): ");
                int packetSize = readPositiveInt(scanner, "размер пакета");
                if (packetSize <= 0) break;

                System.out.print("Введите количество пакетов: ");
                int packetCount = readPositiveInt(scanner, "количество пакетов");
                if (packetCount <= 0) break;

                System.out.print("Введите частоту отправки (пакетов в секунду): ");
                int frequency = readPositiveInt(scanner, "частоту отправки");
                if (frequency <= 0) break;

                Client.main(new String[]{
                        host,
                        String.valueOf(clientPort),
                        String.valueOf(packetSize),
                        String.valueOf(packetCount),
                        String.valueOf(frequency)
                });
                break;
            default:
                System.out.println("Неверный выбор.");
        }
        scanner.close();
    }

    private static int readValidPort(Scanner scanner) {
        while (true) {
            try {
                int port = scanner.nextInt();
                scanner.nextLine();
                if (port < 1 || port > 65535) {
                    System.out.print("Ошибка: порт должен быть в диапазоне 1-65535. Повторите ввод: ");
                    continue;
                }
                return port;
            } catch (Exception e) {
                System.out.print("Ошибка: введите корректное число для порта: ");
                scanner.nextLine();
            }
        }
    }

    private static int readPositiveInt(Scanner scanner, String field) {
        while (true) {
            try {
                int value = scanner.nextInt();
                scanner.nextLine();
                if (value <= 0) {
                    System.out.print("Ошибка: " + field + " должно быть положительным числом. Повторите ввод: ");
                    continue;
                }
                return value;
            } catch (Exception e) {
                System.out.print("Ошибка: введите корректное число для " + field + ": ");
                scanner.nextLine();
            }
        }
    }
}