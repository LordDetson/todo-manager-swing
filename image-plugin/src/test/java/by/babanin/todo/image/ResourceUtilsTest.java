package by.babanin.todo.image;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import by.babanin.todo.image.exception.ResourceException;

class ResourceUtilsTest {

    @Test
    void buildResourcePath() {
        String path = "directory/";
        String name = "name";
        String extension = "extension";

        String resourcePath = ResourceUtils.buildResourcePath(path, name, extension);

        assertEquals("directory/name.extension", resourcePath);
    }

    @Test
    void buildResourcePathWithEmptyPath() {
        List<String> paths = new ArrayList<>();
        paths.add(null);
        paths.add("");
        paths.add("   ");
        String name = "name";
        String extension = "extension";

        paths.forEach(path -> {
            String resourcePath = ResourceUtils.buildResourcePath(path, name, extension);
            assertEquals("name.extension", resourcePath);
        });
    }

    @Test
    void buildResourcePathWithEmptyName() {
        String path = "directory/";
        List<String> names = new ArrayList<>();
        names.add(null);
        names.add("");
        names.add("   ");
        String extension = "extension";

        names.forEach(name -> assertThrows(ResourceException.class, () -> ResourceUtils.buildResourcePath(path, name, extension)));
    }

    @Test
    void buildResourcePathWithEmptyExtension() {
        String path = "directory/";
        String name = "name";
        List<String> extensions = new ArrayList<>();
        extensions.add(null);
        extensions.add("");
        extensions.add("   ");

        extensions.forEach(extension -> {
            String resourcePath = ResourceUtils.buildResourcePath(path, name, extension);
            assertEquals("directory/name", resourcePath);
        });
    }
}