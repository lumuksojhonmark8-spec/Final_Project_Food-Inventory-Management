package utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class FileDBHelper {

    // Data is saved in your user folder, in ".food-inventory"
    private static final Path DATA_DIR = Paths.get(
            System.getProperty("user.home"), ".food-inventory");

    private FileDBHelper() {
    }

    public static void save(Object object, String fileName)
            throws IOException {

        Files.createDirectories(DATA_DIR);

        try (ObjectOutputStream out = new ObjectOutputStream(
                Files.newOutputStream(DATA_DIR.resolve(fileName)))) {

            out.writeObject(object);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T load(String fileName, T defaultValue)
            throws IOException, ClassNotFoundException {

        Path file = DATA_DIR.resolve(fileName);

        if (!Files.exists(file)) {
            return defaultValue;
        }

        try (ObjectInputStream in = new ObjectInputStream(
                Files.newInputStream(file))) {

            return (T) in.readObject();
        }
    }
}