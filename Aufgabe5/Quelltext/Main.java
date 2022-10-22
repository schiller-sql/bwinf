import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Main {
    /**
     * Knoten von: index im array
     * <p>
     * Knoten zu: index im BitSet
     */
    private static final List<BitSet[]> graphs = new ArrayList<>();
    private static final List<String> filenames = new ArrayList<>(5);

    public static void main(String[] args) {
        List<Path> paths = getPaths();
        for (Path path : paths) {
            filenames.add(path.getFileName().toString());

            List<String> lines = getLines(path);
            int countOfNodes = Integer.parseInt(lines.get(0).split(" ")[0]);
            //TODO: check if this variable is needed
            int countOfArrows = Integer.parseInt(lines.get(0).split(" ")[1]);
            lines.remove(0);
            //initialisation of the graph
            BitSet[] graph = new BitSet[countOfNodes];
            //declaration of the graph
            for (int k = 0; k < countOfNodes; k++) {
                graph[k] = new BitSet(countOfNodes);
            }
            //writing input
            for (String line : lines) {
                int outputField = Integer.parseInt(line.split(" ")[0]);
                int targetField = Integer.parseInt(line.split(" ")[1]);
                graph[outputField - 1].set(targetField - 1);
            }
            graphs.add(graph);
        }
        for (int i = 0; i < graphs.size(); i++) {
            BitSet[] graph = graphs.get(i);
            //solving the parcours
            int[][] routes = solve(graph);
            //evaluating the result
            System.out.println("Ergebnis für " + filenames.get(i));
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
        List<BitSet>[] timelines = generateTimeline(graph);
        if (timelines == null) return null;
        //TODO: make sure that this works
        BitSet targets = (BitSet) timelines[0].get(timelines[0].size() - 1).clone();
        targets.and(timelines[1].get(timelines[1].size() - 1));
        int target = targets.nextSetBit(0);
        int steps = timelines[0].size() - 1;
        int[][] routes = new int[2][steps + 1]; // [0][x] for sasha and [1][x] for mika
        //calculate the pathway for mika and sasha
        // and store them in the routes array
        /*
        TODO: pseudocode verbessern, unklar wie die int[]'s (hier: routes[2][steps+1]) aufgebaut werden?!
        Pseudocode:
        -merke targets-bitset(s.o.) als 'last'
        -xLoop i=steps-1:
        -für jeden set aus timeline-eintrag bei index i
        -prüfe, ob nachbar von target
        -merke alle matches als 'last'
        -sobald ein nachbar 1 oder 2 ist, wurde der jeweilige weg gefunden
        -rechne i-1
        -xLoop
         */
        return routes;

    }

    /**
     * @param graph
     * @return
     */
    private static List<BitSet>[] generateTimeline(BitSet[] graph) {
        BitSet sashaFirst = new BitSet(graph.length);
        BitSet mikaFirst = new BitSet(graph.length);
        sashaFirst.set(0);
        mikaFirst.set(1);
        @SuppressWarnings("unchecked")
        List<BitSet>[] timelines = new ArrayList[2];
        timelines[0] = new ArrayList<>(List.of(sashaFirst));
        timelines[1] = new ArrayList<>(List.of(mikaFirst));
        do {
            //build both timelines stepwise
            // until a solution is found or the parcours is invalid
            timelines[0].add(neighbourNodes(timelines[0].get(timelines[0].size()-1), graph));
            timelines[1].add(neighbourNodes(timelines[1].get(timelines[1].size()-1), graph));
            if (timelines[0].get(timelines[0].size() - 1).isEmpty() || timelines[1]
                    .get(timelines[1].size() - 1)
                    .isEmpty() || !timelineRepeats(timelines)) {
                return null;
            }
        } while (!timelines[0].get(timelines[0].size() - 1).intersects(timelines[1].get(timelines[1].size() - 1)));
        return timelines;
    }

    /**
     * @param nodes All current Nodes
     * @param graph The Graph
     * @return A BitSet containing all the neighbors of the nodes
     */
    private static BitSet neighbourNodes(BitSet nodes, BitSet[] graph) {
        BitSet neighbours = new BitSet();
        for (int i = 0; i < graph.length; i++) {
            if(nodes.get(i)) {
                neighbours.or(graph[i]);
            }
        }
        return neighbours;
    }

    /**
     * @param timelines
     * @return
     */
    private static boolean timelineRepeats(List<BitSet>[] timelines) {
        BitSet last0 = timelines[0].get(timelines[0].size() - 1);
        int i = 0;
        int repeat = -1;
        for (BitSet set :
                timelines[0]) {
            if(set != last0) {
                if(set.equals(last0)) {
                    repeat = i;
                    break;
                }
            }
            i++;
        }
        if(repeat != -1) {
            BitSet last1 = timelines[1].get(timelines[1].size() - 1);
            BitSet repeating = timelines[1].get(repeat);
            return !last1.equals(repeating);
        }
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
                    .sorted()
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
