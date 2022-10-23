import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 *
 */
public class Main {
    private static final List<String> book = new ArrayList<>();
    @SuppressWarnings("unchecked")
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

    /**
     * @param stoerung Die Eingabedatei, nach der das Buch durchsucht werden soll
     * @return Eine Liste von Positionen, die mit demselben Wort beginnen wie die Eingabedatei
     */
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

    /**
     * @param positions Eine Liste von Positionen, die überprüft werden soll
     * @param stoerung Die Eingabedatei, nach der die möglichen Positionen überprüft werden sollen
     * @return Eine Liste von Positionen, die die Eingabedatei lösen können
     */
    private static List<int[]> checkPositions(List<int[]> positions, List<String> stoerung) {
        List<int[]> rightPositions = new ArrayList<>();
        positionLoop:
        for (int[] position : positions) {
            StringBuilder text = new StringBuilder(book.get(position[0]).substring(position[1]).toLowerCase());
            int counter = 1;
            while (text.toString().split(" ").length < stoerung.size()) {
                if (position[0] + counter >= book.size()) {
                    break positionLoop;
                }
                text.append(" ").append(book.get(position[0] + counter).toLowerCase());
                counter++;
            }
            if (text.toString().contains("  ")) {
                continue;
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
        }
        return rightPositions;
    }

    /**
     * @param line Die Zeile, in der der Text beginnt
     * @param column Die Spalte, in der der Text beginnt
     * @return Der (Teil-)Satz, der sich an der durch die Parameter bestimmten Stelle befindet
     */
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

    /**
     * @param text Der zu bearbeitende Text
     * @return Der bearbeitete Text
     */
    private static String formatText(StringBuilder text) {
        return text.toString().replaceAll("[.!?,»«'\"()\\[\\]]", "#");
    }

    /**
     *
     */
    private static void evaluatingFiles() {
        for (Path path : getPaths()) {
            if (path.getFileName().toString().equals("Alice_im_Wunderland.txt")) {
                book.addAll(getLines(path));
            } else {
                for (String line : getLines(path)) {
                    List<String> wordsList = new ArrayList<>();
                    String[] words = line.split(" ");
                    Collections.addAll(wordsList, words);
                    stoerungen[Integer.parseInt(path.getFileName().toString().replaceAll("[^0-9]+", ""))] = wordsList;
                }
            }
        }
    }

    /**
     * @return Eine Liste der Pfade zu allen Dateien im Verzeichnis Aufgabe1/Eingabedateien/*
     * @throws RuntimeException wenn nicht auf den Pfad zugegriffen werden kann
     */
    private static List<Path> getPaths() {
        List<Path> paths;
        try (Stream<Path> walk = Files.walk(Paths.get("Aufgabe1/", "Eingabedateien/"))) {
            paths = walk.filter(Files::isRegularFile).filter(path -> path.getFileName().toString().endsWith(".txt")).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return paths;
    }

    /**
     * @param path Der Pfad, unter dem die Datei gelesen werden soll
     * @return Eine Liste der Zeilen in der Datei
     * @throws RuntimeException wenn nicht auf die Datei zugegriffen werden kann
     */
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
