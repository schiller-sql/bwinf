import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Main {
    private static final List<String> book = new ArrayList<>();
    private static final List<String>[] stoerungen = new ArrayList[6];

    public static void main(String[] args) {
        String text = null;
        for (Path path : getPaths()) {
            if (path.getFileName().toString().equals("Alice_im_Wunderland.txt")) {
                text = getText(path).toLowerCase();
            }
        }
        assert text != null: "text has not been found in alice_im_wunderland.txt";
        for (Path path : getPaths()) {
            if (!path.getFileName().toString().equals("Alice_im_Wunderland.txt")) {
                String rawRegexStr = getText(path)
                        .replaceAll(" ", "[ \\\\n]")
                        .replaceAll("_", "[^.»«!?/\\\\n ]+"); //,_#
                System.out.println(path.getFileName() + ": ");
                Pattern regex = Pattern.compile(rawRegexStr);
                Matcher matcher = regex.matcher(text);
                while(matcher.find()) {
                    System.out.println(matcher.group() + "\n");
                }
                System.out.println("\n");
            }
        }
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

    private static String getText(Path path) {
        try {
            return Files.readString(path);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
