package by.babanin.todo.view.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AppUtils {

    public static void writeObjectToFile(ObjectMapper objectMapper, Path path, Object object) throws IOException {
        Path parent = path.getParent();
        if(Files.notExists(parent)) {
            Files.createDirectories(parent);
        }
        Path tempFilePath = Files.createTempFile(parent, path.getFileName().toString(), "");
        try {
            objectMapper.writeValue(tempFilePath.toFile(), object);
            Files.deleteIfExists(path);
            Files.move(tempFilePath, path);
        }
        catch(IOException e) {
            Files.deleteIfExists(tempFilePath);
            throw e;
        }
    }
}
