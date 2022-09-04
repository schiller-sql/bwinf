import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        List<String> book = new ArrayList<>();
        String[] stoerungen = new String[6];
        for(Path path : getPaths()) {
            if(path.getFileName().toString().equals("Alice_im_Wunderland.txt")) {
                book.addAll(getLines(path));
            } else {
                for(String line : getLines(path)) {
                    stoerungen[Integer.parseInt(path.getFileName().toString().replaceAll("[^0-9]+", ""))] = line;
                }
            }
        }
        printBook(book);
        printStoerungen(stoerungen);
    }

    private static void printBook(List<String> book) {
        book.forEach(System.out::println);
    }

    private static void printStoerungen(String[] stoerungen) {
        Arrays.stream(stoerungen).forEach(System.out::println);
    }

    private static List<Path> getPaths() {
        List<Path> paths;
        try (Stream<Path> walk = Files.walk(Paths.get("Aufgabe1/", "Eingabedateien/"))) {
            paths = walk.filter(Files::isRegularFile).filter(path -> path.getFileName().toString().endsWith(".txt")).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return paths;
    }

    private static List<String> getLines(Path path) {
        List<String> data;
        try (Stream<String> lines = Files.lines(path)) {
            data = new ArrayList<>(lines.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return data;
    }
}
