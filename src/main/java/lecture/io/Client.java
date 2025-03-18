package lecture.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try (SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("localhost", 1120));
             Scanner scanner = new Scanner(System.in)) {

            ByteBuffer buffer = ByteBuffer.allocate(256);
            System.out.println("🚀 Connected to server. Type messages:");

            while (true) {
                System.out.print("> ");
                String message = scanner.nextLine();
                buffer.clear();
                buffer.put(message.getBytes());
                buffer.flip();
                socketChannel.write(buffer);

                buffer.clear();
                socketChannel.read(buffer);
                buffer.flip();
                System.out.println("📩 Server response: " + new String(buffer.array(), 0, buffer.limit()));

                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
