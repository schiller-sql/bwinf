import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Main {
    private static final List<String> book = new ArrayList<>();
    private static final List<String>[] stoerungen = new ArrayList[6];

    public static void main(String[] args) {
        evaluatingFiles();
        for (int i = 0; i < stoerungen.length; i++) {
            List<int[]> results = checkPositions(getPositions(stoerungen[i]), stoerungen[i]);
            System.out.println("Störung Nr." + i + ":");
            for (int index = 0; index < stoerungen[i].size(); index++) {
                System.out.print(stoerungen[i].get(index) + " ");
            }
            System.out.println();
            for (int[] result : results) {
                System.out.println("Zeile: " + result[0]);
                System.out.println("'" + getText(result[0], result[1]) + "'");
            }
            System.out.println();
        }
    }

    private static List<int[]> getPositions(List<String> stoerung) {
        List<int[]> positions = new ArrayList<>();
        for (int lineNumber = 0; lineNumber < book.size(); lineNumber++) {
            String line = book.get(lineNumber).toLowerCase();
            int index = line.indexOf(stoerung.get(0));
            while (index >= 0) {
                positions.add(new int[]{lineNumber, index});
                index = line.indexOf(stoerung.get(0), index + 1);
            }
        }
        return positions;
    }

    private static List<int[]> checkPositions(List<int[]> positions, List<String> stoerung) {
        List<int[]> rightPositions = new ArrayList<>();
        positions.forEach(position -> {
            StringBuilder text = new StringBuilder(book.get(position[0]).substring(position[1]).toLowerCase());
            int counter = 1;
             while (text.toString().split(" ").length < stoerung.size()) {
                if (position[0] + counter >= book.size()) {
                    return;
                }
                text.append(" ").append(book.get(position[0] + counter).toLowerCase());
                counter++;
            }
            String[] lines = text.toString().split(" ");
            for (int index = 0; index < lines.length; index++) {
                lines[index] = lines[index].replaceAll("[^[a-zA-Z0-9äöüß]]", "");
            }
            for (int index = 1; index < stoerung.size(); index++) {
                if (Objects.equals(stoerung.get(index), lines[index])) {
                    if (index == stoerung.size() - 1) {
                        rightPositions.add(position);
                    }
                } else if (!Objects.equals(stoerung.get(index), "_")) {
                    break;
                } else {
                    if (index == stoerung.size() - 1) {
                        rightPositions.add(position);
                    }
                }
            }
        });
        return rightPositions;
    }
    private static String getText(int line, int column) {
        StringBuilder text = new StringBuilder(book.get(line));
        int startIndex = formatText(text).lastIndexOf("#", column);
        int endIndex = formatText(text).indexOf("#", column);
        int counter = 1;
        while (startIndex < 0 || endIndex < 0) {
            if (startIndex < 0) {
                text.insert(0, (book.get(line - counter) + " "));
            }
            if (endIndex < 0) {
                text.append(" ").append(book.get(line + counter));
            }
            startIndex = formatText(text).lastIndexOf("#", column);
            endIndex = formatText(text).indexOf("#", column);
            counter++;
        }
        return text.substring(startIndex + 1, endIndex + 1).trim();
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
