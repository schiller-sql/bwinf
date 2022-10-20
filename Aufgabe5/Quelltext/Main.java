import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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
        for (BitSet[] graph : graphs) {
            //solving the parcours
            int[][] routes = solve(graph);
            //evaluating the result
            if(routes == null) System.out.println("Der Parcours hat keine Lösung!");
            else {
                System.out.println("Der Parcours hat folgende Lösung:");
                System.out.println("Zielfeld: " + routes[0][routes[0].length-1]);
                System.out.println("Sasha's Weg: " + Arrays.toString(routes[0]));
                System.out.println("Mika's Weg: " + Arrays.toString(routes[1]));
            }
        }
    }

    /**
     * @param graph
     * @return
     */
    private static int[][] solve(BitSet[] graph) {
        int[][] routes = new int[2][];// [0][x] for sasha and [1][x] for mika
        List<BitSet> sasha_timeline = new ArrayList<>();
        List<BitSet> mika_timeline = new ArrayList<>();
        int target = -1;
        int steps = 0;
        while (validParcours(sasha_timeline, mika_timeline) && target < 0) {
            //build both timelines stepwise
            // until a solution is found or the parcours is invalid
            steps++;
        }
        if(target < 0) return null;
        else {
            //calculate the pathway for mika and sasha
            // and store them in the routes array
        }
        return routes;
    }

    /**
     * @param sasha_timeline
     * @param mika_timeline
     * @return
     */
    private static boolean validParcours(List<BitSet> sasha_timeline, List<BitSet> mika_timeline) {
        return true;
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