import java.io.IOException;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Использование: java Server <порт>");
            return;
        }

        int port = Integer.parseInt(args[0]);

        try (DatagramSocket socket = new DatagramSocket(port)) {
            System.out.println("\n===== СЕРВЕР ЗАПУЩЕН =====");
            System.out.println("Прослушивание порта: " + port);
            System.out.println("Ожидание пакетов...\n");


            int totalPackets = 0;
            int lostPackets = 0;
            long totalBytes = 0;
            long totalDelay = 0;
            int lastSequence = -1;
            long sessionStartTime = -1;
            long lastPacketTime = System.currentTimeMillis();

            byte[] buffer = new byte[65507];
            final long TIMEOUT_MS = 3000;

            while (true) {
                try {
                    socket.setSoTimeout(1000);
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    long receiveTime = System.currentTimeMillis();


                    int sequenceNumber = 0;
                    for (int i = 0; i < 4; i++) {
                        sequenceNumber = (sequenceNumber << 8) | (buffer[i] & 0xFF);
                    }


                    long timestamp = 0;
                    for (int i = 0; i < 8; i++) {
                        timestamp = (timestamp << 8) | (buffer[4 + i] & 0xFF);
                    }

                    if (sessionStartTime == -1) {
                        sessionStartTime = receiveTime;
                    }

                    long delay = receiveTime - timestamp;


                    if (lastSequence != -1 && sequenceNumber > lastSequence + 1) {
                        int lost = sequenceNumber - lastSequence - 1;
                        lostPackets += lost;
                        System.out.printf("⚠️ Потеряно пакетов: %d (ожидали %d, получили %d)\n",
                                lost, lastSequence + 1, sequenceNumber);
                    }
                    lastSequence = sequenceNumber;


                    totalPackets++;
                    totalBytes += packet.getLength();
                    totalDelay += delay;
                    lastPacketTime = receiveTime;

                    if (totalPackets % 10 == 0 || totalPackets <= 110000) {
                        System.out.printf("✅ Пакет #%d получен | Задержка: %d мс | Размер: %d байт\n",
                                sequenceNumber + 1, delay, packet.getLength());
                    }

                } catch (SocketTimeoutException e) {

                    if (System.currentTimeMillis() - lastPacketTime > TIMEOUT_MS && totalPackets > 0) {
                        break;
                    }

                }
            }


            long sessionEndTime = System.currentTimeMillis();
            double durationSec = (sessionEndTime - sessionStartTime) / 1000.0;
            double actualSpeed = totalBytes / durationSec / 1024.0;

            System.out.println("\n" + "=".repeat(50));
            System.out.println("Прием завершен!");
            System.out.printf("Всего получено пакетов: %d\n", totalPackets);
            System.out.printf("Фактическая скорость приема: %.2f KB/s\n", actualSpeed);
            System.out.printf("Общее время: %.2f сек\n", durationSec-2);
            System.out.println("=".repeat(50));

        } catch (IOException e) {
            System.out.println("Ошибка сервера: " + e.getMessage());
        }
    }
}