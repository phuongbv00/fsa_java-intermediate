package lecture.io;

import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Stream;

public class IOTest {
    @Test
    public void readWriteTextFileUsingBinaryStreams() {
        String filename = "data/readWriteTextFileUsingBinaryStreams.txt";
        String content = "Hello, Java I/O!\nThis is a basic file read-write example.";

        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(content.getBytes());
            System.out.println("File written successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileInputStream fis = new FileInputStream(filename)) {
            int ch;
            System.out.println("File content:");
            while ((ch = fis.read()) != -1) {
                System.out.print((char) ch);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void readWriteTextFileUsingCharStreams() {
        String filename = "data/readWriteTextFileUsingCharStreams.txt";
        String content = """
                1. Complete Java I/O Lab
                2. Review Serialization
                3. Experiment with NIO Channels
                4. Implement Decorator Pattern
                """;

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(content);
            System.out.println("File written successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileReader reader = new FileReader(filename)) {
            int ch;
            System.out.println("File content:");
            while ((ch = reader.read()) != -1) {
                System.out.print((char) ch);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void readWriteTextFileUsingBufferedStreams() {
        String filename = "data/readWriteTextFileUsingBufferedStreams.txt";
        List<String> names = List.of("Alice", "Bob", "Charlie", "Dang", "Hung", "An", "Cong", "Doanh", "Vu", "Phuong");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String name : names) {
                writer.write(name);
                writer.newLine();
                if (name.startsWith("C")) {
                    writer.flush();
                }
            }
            System.out.println("Names written to file!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            System.out.println("Names from file:");
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void readWriteSerializable() {
        String filename = "data/readWriteSerializable.ser";

        List<Person> people = List.of(
                new Person("Adam", 25),
                new Person("Eve", 29)
        );

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(people);
            System.out.println("People serialized!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            List<Person> deserializedPeople = (List<Person>) ois.readObject();
            System.out.println("Deserialized people: " + deserializedPeople);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void readWriteTextFileUsingNIOUtils() {
        Path dirPath = Path.of("data").resolve("nio");
        Path filePath = dirPath.resolve("notes.txt");
        Path backupPath = dirPath.resolve("backup_notes.txt");

        try {
            // Create a directory
            if (!Files.exists(dirPath)) {
                Files.createDirectory(dirPath);
                System.out.println("✅ Directory created: " + dirPath);
            }

            // Create a file and write text
            String content = "Java NIO makes file handling easy!";
            Files.writeString(filePath, content);
            System.out.println("✅ File created & written: " + filePath);

            // Read and display file content
            String fileContent = Files.readString(filePath);
            System.out.println("📄 File Content:\n" + fileContent);

            // Copy file to backup
            Files.copy(filePath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("✅ File copied to: " + backupPath);

            // Delete backup file
            Files.deleteIfExists(backupPath);
            System.out.println("✅ Backup file deleted: " + backupPath);

            System.out.println("Folder tree:");
            try (Stream<Path> tree = Files.walk(Path.of("src").resolve("main").resolve("java"), 5)) {
                tree.map(Path::toAbsolutePath).forEach(System.out::println);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void readWriteTextFileUsingNIOUtilsWithBufferedStreams() throws IOException {
        Path dirPath = Path.of("data").resolve("nio");
        Path filePath = dirPath.resolve("buffered_notes.txt");

        if (!Files.exists(dirPath)) {
            Files.createDirectory(dirPath);
            System.out.println("✅ Directory created: " + dirPath);
        }

        // Write using BufferedWriter
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write("Buffered I/O improves performance!");
            writer.newLine();
            writer.write("It avoids excessive disk reads/writes.");
            System.out.println("✅ File written with BufferedWriter!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Read using BufferedReader
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            System.out.println("📄 File Content:");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void readWriteTextFileUsingNIOUtilsWithChannelAndBuffer() {
        // TODO: there is a bug somewhere, let's find it out ツ
        Path filePath = Path.of("data/nio/large_file.txt");
        String sampleData = "This is a sample line in a large file.\n";

        // Efficiently Write a Large File
        try (FileChannel writeChannel = FileChannel.open(filePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            ByteBuffer buffer = ByteBuffer.allocate(8192); // Use an 8KB buffer

            // Simulating a large file with 100,000 lines
            for (int i = 0; i < 100_000; i++) {
                buffer.put(sampleData.getBytes());

                // When buffer is full, flush to file
                if (!buffer.hasRemaining()) {
                    buffer.flip();
                    writeChannel.write(buffer);
                    buffer.clear(); // Clear buffer for next batch
                }
            }

            // Flush any remaining data in the buffer
            buffer.flip();
            while (buffer.hasRemaining()) {
                writeChannel.write(buffer);
            }

            System.out.println("✅ Large file written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Efficiently Read the Large File in Chunks
        try (FileChannel readChannel = FileChannel.open(filePath, StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate(8192); // Use an 8KB buffer

            System.out.println("📄 Reading large file in chunks...");
            while (readChannel.read(buffer) > 0) {
                buffer.flip();
                System.out.print(new String(buffer.array(), 0, buffer.limit())); // Process data chunk
                buffer.clear(); // Clear buffer for next read
            }

            System.out.println("\n✅ Large file read successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class Person implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private String name;
        private int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return name + " (" + age + " years old)";
        }
    }
}
