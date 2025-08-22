package Controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileReader {

    public static String[] getFrasi(String filePath) throws IOException {

        Path path = Paths.get(filePath);
        String contenutoFile = Files.readString(path);

        return contenutoFile.split(",");
    }
}
