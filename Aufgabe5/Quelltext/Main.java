import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        for (Path path : getPaths()) {
            String fileName = path.getFileName().toString();
            List<String> lines = getLines(path);
            BitSet[] graph = generateGraphFromString(lines);
            int[][] routes = solveGraphForRouteOnSameField(graph);

            System.out.println("\nErgebnis für " + fileName);
            if (routes == null) {
                System.out.println("Der Parcours hat keine Lösung!");
            } else {
                System.out.println("Der Parcours hat folgende Lösung:");
                System.out.println("Zielfeld: " + (routes[0][routes[0].length - 1] + 1));
                System.out.println("Anzahl an Schritten: " + routes[0].length);
                for (int person = 0; person < 2; person++) {
                    System.out.println((person == 0 ? "Sasha" : "Mika") + "'s Weg: ");
                    for (int step = 0; step < routes[person].length; step++) {
                        System.out.print(routes[person][step] + 1 + (step != routes[person].length - 1 ? " -> " : ""));
                    }
                    System.out.println();
                }
            }
        }
    }

    private static BitSet[] generateGraphFromString(List<String> lines) {
        int countOfNodes = Integer.parseInt(lines.get(0).split(" ")[0]);
        BitSet[] graph = new BitSet[countOfNodes];
        for (int i = 0; i < countOfNodes; i++) {
            graph[i] = new BitSet(countOfNodes);
        }
        List<String> rawArrows = lines.subList(1, lines.size());
        for (String rawArrow : rawArrows) {
            int arrowBegin = Integer.parseInt(rawArrow.split(" ")[0]);
            int arrowEnd = Integer.parseInt(rawArrow.split(" ")[1]);
            graph[arrowBegin - 1].set(arrowEnd - 1);
        }
        return graph;
    }

    private static int[][] solveGraphForRouteOnSameField(BitSet[] graph) {
        List<BitSet>[] timelines = generateTimelines(graph);
        if (timelines == null) return null;
        int target = target(timelines);
        int[][] routes = new int[2][];
        //calculate the pathway for mika and sasha
        // and store them in the routes array
        for (int i = 0; i < 2; i++) {
            routes[i] = findSingleRouteInTimeline(timelines[i], target, graph);
        }
        return routes;
    }

    private static int target(List<BitSet>[] timelines) {
        BitSet targets = (BitSet) timelines[0].get(timelines[0].size() - 1).clone();
        targets.and(timelines[1].get(timelines[1].size() - 1));
        return targets.nextSetBit(0);
    }

    private static int[] findSingleRouteInTimeline(List<BitSet> timeline, int target, BitSet[] graph) {
        int[] route = new int[timeline.size()];
        steps:
        for (int currentStep = timeline.size() - 1; currentStep > 0; currentStep--) {
            route[currentStep] = target;
            BitSet currentNodes = timeline.get(currentStep - 1);
            for (int i = 0; i < graph.length; i++) {
                if (currentNodes.get(i)) {
                    BitSet arrows = graph[i];
                    if (arrows.get(target)) {
                        target = i;
                        continue steps;
                    }
                }
            }
        }
        route[0] = target;
        return route;
    }

    private static List<BitSet>[] generateTimelines(BitSet[] graph) {
        BitSet sashaFirst = new BitSet(graph.length);
        BitSet mikaFirst = new BitSet(graph.length);
        sashaFirst.set(0);
        mikaFirst.set(1);
        @SuppressWarnings("unchecked")
        List<BitSet>[] timelines = new ArrayList[2];
        timelines[0] = new ArrayList<>(List.of(sashaFirst));
        timelines[1] = new ArrayList<>(List.of(mikaFirst));
        do {
            // build both timelines stepwise
            // until a solution is found or the parcours is invalid
            timelines[0].add(neighbourNodes(timelines[0].get(timelines[0].size() - 1), graph));
            timelines[1].add(neighbourNodes(timelines[1].get(timelines[1].size() - 1), graph));
            if (timelines[0].get(timelines[0].size() - 1).isEmpty() || timelines[1]
                    .get(timelines[1].size() - 1)
                    .isEmpty() || !timelineRepeats(timelines)) {
                return null;
            }
        } while (!timelines[0].get(timelines[0].size() - 1).intersects(timelines[1].get(timelines[1].size() - 1)));
        return timelines;
    }

    private static BitSet neighbourNodes(BitSet nodes, BitSet[] graph) {
        BitSet neighbours = new BitSet();
        for (int i = 0; i < graph.length; i++) {
            if (nodes.get(i)) {
                neighbours.or(graph[i]);
            }
        }
        return neighbours;
    }

    private static boolean timelineRepeats(List<BitSet>[] timelines) {
        BitSet last0 = timelines[0].get(timelines[0].size() - 1);
        int i = 0;
        int repeat = -1;
        for (BitSet set :
                timelines[0]) {
            if (set != last0) {
                if (set.equals(last0)) {
                    repeat = i;
                    break;
                }
            }
            i++;
        }
        if (repeat != -1) {
            BitSet last1 = timelines[1].get(timelines[1].size() - 1);
            BitSet repeating = timelines[1].get(repeat);
            return !last1.equals(repeating);
        }
        return true;
    }

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