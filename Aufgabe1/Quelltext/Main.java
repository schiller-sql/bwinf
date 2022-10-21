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
        checkLocations(getLocations(stoerungen[0]), stoerungen[0]).forEach(position -> Arrays.stream(position).toList().forEach(System.out::println));
        //getLocations(stoerungen[0].get(0)).forEach(position -> Arrays.stream(position).toList().forEach(System.out::println));
    }

    private static List<Integer[]> getLocations(List<String> stoerung) {
        List<Integer[]> positions = new ArrayList<>();
        for (int lineNumber = 0; lineNumber < book.size(); lineNumber++) {
            String line = book.get(lineNumber).toLowerCase();
            //System.out.println(line + "   ");
            if (line.contains(stoerung.get(0))) {
                Integer[] position = new Integer[]{lineNumber, line.indexOf(stoerung.get(0))};
                positions.add(position);
                //Arrays.stream(position).toList().forEach(System.out::println);
                //System.out.println();
            }
        }
        return positions;
    }

    private static List<Integer[]> checkLocations(List<Integer[]> positions, List<String> stoerung) {
        List<Integer[]> rightPositions = new ArrayList<>();
        positions.forEach(position -> {
            //Arrays.stream(position).toList().forEach(System.out::println);
            StringBuilder text = new StringBuilder(book.get(position[0]).substring(position[1]).toLowerCase());
            int counter = 1;
            while (text.toString().split(" ").length < stoerung.size()) {
                text.append(book.get(position[0] + counter).toLowerCase());
                counter++;
            }
            String[] lines = text.toString().split(" ");
            for (int index = 0; index < lines.length; index++) {
                lines[index] = lines[index].replaceAll("[^a-zA-Z0-9]", "");
            }
            //Arrays.stream(lines).toList().forEach(System.out::print);
            //System.out.println();
            for (int index = 0; index < stoerung.size(); index++) {
                if (Objects.equals(stoerung.get(index), "_") && !Objects.equals(stoerung.get(index), lines[index])) {

                }

            }
        });
        return rightPositions;
    }

    private static void printBook(List<String> book) {
        book.forEach(System.out::println);
    }

    private static void printStoerungen(List<String>[] stoerungen) {
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
