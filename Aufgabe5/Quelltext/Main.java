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
    /**
     * Knoten von: index im array
     * <p>
     * Knoten zu: index im BitSet
     */
    private static final List<BitSet[]> graphs = new ArrayList<>();

    public static void main(String[] args) {
        for (Path path : getPaths()) {
            List<String> lines = getLines(path);
            int countOfNodes = Integer.parseInt(lines.get(0).split(" ")[0]);
            //TODO: check if this variable is needed
            int countOfArrows = Integer.parseInt(lines.get(0).split(" ")[1]);
            lines.remove(0);
            //initialisation of the graph
            BitSet[] graph = new BitSet[countOfNodes];
            //declaration of the graph
            for (int i = 0; i < countOfNodes; i++) {
                graph[i] = new BitSet(countOfNodes);
            }
            //writing input
            for (String line : lines) {
                int outputField = Integer.parseInt(line.split(" ")[0]);
                int targetField = Integer.parseInt(line.split(" ")[1]);
                graph[outputField - 1].set(targetField - 1);
            }
            graphs.add(graph);
        }
        for (BitSet[] graph : graphs) {
            //solving the parcours
            int[][] routes = solve(graph);
            //evaluating the result
            if (routes == null) System.out.println("Der Parcours hat keine Lösung!");
            else {
                System.out.println("Der Parcours hat folgende Lösung:");
                System.out.println("Zielfeld: " + routes[0][routes[0].length - 1]);
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
        int target = -1, steps = 0;
        while (
                validParcours(sashaTimeline, mikaTimeline) &&
                        target < 0 &&
                        sashaTimeline.get(sashaTimeline.size() - 1).isEmpty()
                        && mikaTimeline.get(mikaTimeline.size() - 1).isEmpty()
        ) {
            //build both timelines stepwise
            // until a solution is found or the parcours is invalid
            steps++;
        }
        if (target < 0) return null;
        else {
            int[][] routes = new int[2][steps + 1]; // [0][x] for sasha and [1][x] for mika
            //calculate the pathway for mika and sasha
            // and store them in the routes array
            return routes;
        }
    }

    List<BitSet>[] generateStepwiseTimeline(BitSet[] graph) {
        BitSet sashaFirst = new BitSet(graph.length);
        BitSet mikaFirst = new BitSet(graph.length);
        sashaFirst.set(0, true);
        mikaFirst.set(1, true);
        List<BitSet>[] timelines = new ArrayList[2];
        timelines[0] = new ArrayList<>(List.of(sashaFirst));
        timelines[1] = new ArrayList<>(List.of(mikaFirst));
        do {
            // use neighbouringNodes to get next step in timeline
            if (timelines[0].get(timelines[0].size() - 1).isEmpty() || timelines[1]
                    .get(timelines[1].size() - 1)
                    .isEmpty() || !validParcours(timelines[0], timelines[1])) {
                return null;
            }
        } while (!timelines[0].get(timelines[0].size() - 1).intersects(timelines[1].get(timelines[1].size() - 1)));
        return timelines;
    }

    private static BitSet neighbouringNodes(BitSet nodes, BitSet[] graph) {

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
            paths = walk
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".txt"))
                    .toList();
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
