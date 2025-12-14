import java.io.IOException;
import java.net.*;
import java.util.Random;

public class Client {
    public static void main(String[] args) {
        if (args.length != 5) {
            System.out.println("Использование: java Client <хост> <порт> <размер_пакета> <количество_пакетов> <частота>");
            return;
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        int packetSize = Integer.parseInt(args[2]);
        int packetCount = Integer.parseInt(args[3]);
        int frequency = Integer.parseInt(args[4]);

        if (packetSize < 12) {
            System.out.println("Ошибка: минимальный размер пакета — 12 байт");
            return;
        }

        try {
            InetAddress serverAddress = InetAddress.getByName(host);
            DatagramSocket socket = new DatagramSocket();

            System.out.println("\n===== КЛИЕНТ ЗАПУЩЕН =====");
            System.out.println("Отправка " + packetCount + " пакетов на " + host + ":" + port);
            System.out.println("Размер: " + packetSize + " байт, частота: " + frequency + " пакетов/сек\n");

            long startTime = System.currentTimeMillis();
            long interval = 1000 / frequency;

            for (int i = 0; i < packetCount; i++) {
                byte[] packet = new byte[packetSize];
                new Random().nextBytes(packet);

                packet[0] = (byte)(i >> 24);
                packet[1] = (byte)(i >> 16);
                packet[2] = (byte)(i >> 8);
                packet[3] = (byte)i;

                long timestamp = System.currentTimeMillis();
                for (int j = 0; j < 8; j++) {
                    packet[4 + j] = (byte)(timestamp >> (56 - j * 8));
                }

                socket.send(new DatagramPacket(packet, packet.length, serverAddress, port));

                if (i % Math.max(1, packetCount / 10) == 0 || i == packetCount - 1) {
                    System.out.printf("Отправлено: %d/%d (%.1f%%)\n", i + 1, packetCount, (i + 1) * 100.0 / packetCount);
                }

                if (i < packetCount - 1) {
                    Thread.sleep(interval);
                }
            }

            long endTime = System.currentTimeMillis();
            double durationSec = (endTime - startTime) / 1000.0;
            double actualSpeed = (packetCount * packetSize) / durationSec / 1024.0;

            System.out.println("\n" + "=".repeat(50));
            System.out.println("Отправка завершена!");
            System.out.printf("Всего отправлено пакетов: %d\n", packetCount);
            System.out.printf("Фактическая скорость передачи: %.2f KB/s\n", actualSpeed);
            System.out.printf("Общее время: %.2f сек\n", durationSec);
            System.out.println("=".repeat(50));

            socket.close();
        } catch (Exception e) {
            System.out.println("Ошибка клиента: " + e.getMessage());
        }
    }
}