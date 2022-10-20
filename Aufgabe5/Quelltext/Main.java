import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Stream;

public class Main {
    private static final List<BitSet[]> graphs = new ArrayList<>();

    public static void main(String[] args) {
        for (Path path : getPaths()) {
            List<String> lines = getLines(path);
            int countOfNodes = Integer.parseInt(lines.get(0).split(" ")[0]);
            //TODO: check if this variable is needed
            int countOfArrows = Integer.parseInt(lines.get(0).split(" ")[1]);
            //initialisation of the graph
            BitSet[] graph = new BitSet[countOfNodes];
            //declaration of the graph
            for (int i = 0; i < countOfNodes; i++) {
                graph[i] = new BitSet(countOfNodes);
            }
            //writing input
            for (String line : lines) {
                int output_field, target_field;
                output_field = Integer.parseInt(line.split(" ")[0]);
                target_field = Integer.parseInt(line.split(" ")[1]);
                graph[output_field].set(target_field);
            }
            graphs.add(graph);
        }
    }

    /**
     * @return A list of paths to all files with the .txt extension in the Aufgabe5/Eingabedateien/ directory.
     */
    private static List<Path> getPaths() {
        List<Path> paths;
        try (Stream<Path> walk = Files.walk(Paths.get("Aufgabe5/", "Eingabedateien/"))) {
            paths = walk.filter(Files::isRegularFile).filter(path -> path.getFileName().toString().endsWith(".txt")).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return paths;
    }

    /**
     * @param path The file from which the lines are read
     * @return A list of strings representing the lines
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