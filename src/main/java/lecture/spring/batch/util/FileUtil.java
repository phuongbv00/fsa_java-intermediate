package lecture.spring.batch.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {
    public void createFile(Path path) throws IOException {
        Files.createFile(path);
    }
}
