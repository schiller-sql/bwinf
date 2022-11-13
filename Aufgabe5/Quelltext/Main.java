import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Stream;

public class Main {

    /**
     * Liest die Eingabedateien im Verzeichnis /Eingabedateien aus und gibt die Lösung für jede dieser Dateien aus.
     */
    public static void main(String[] args) {
        for (Path path : getPaths()) {
            String fileName = path.getFileName().toString();
            List<String> lines = getLines(path);
            BitSet[] graph = graphFromLines(lines);
            int[][] routes = sameTargetRoute(graph);

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

    /**
     * Generiert den Graphen aus den Zeilen einer Eingabedatei.
     *
     * @param lines Eine String Liste, wo jeder String eine Zeile im Eingabeformat ist.
     * @return Den generierten Graphen.
     */
    private static BitSet[] graphFromLines(List<String> lines) {
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

    /**
     * Findet eine Route, vom nullten und ersten Knoten,
     * zu einem gemeinsamen Knoten in einer gleichen Anzahl von Schritten.
     *
     * @param graph Der Graph, zu dem eine Route vom nullten und ersten Knoten zu einem gemeinsamen Knoten gebildet werden soll.
     * @return Eine Route zu einem Knoten der vom 0. und 1. Knoten in der gleichen Anzahl von Schritten erreichbar ist.
     */
    private static int[][] sameTargetRoute(BitSet[] graph) {
        BitSet sashaFirst = new BitSet(graph.length);
        BitSet mikaFirst = new BitSet(graph.length);
        sashaFirst.set(0);
        mikaFirst.set(1);
        List<BitSet> sashaTimeline = new ArrayList<>(List.of(sashaFirst));
        List<BitSet> mikaTimeline = new ArrayList<>(List.of(mikaFirst));
        do {
            sashaTimeline.add(neighbourNodes(sashaTimeline.get(sashaTimeline.size() - 1), graph));
            mikaTimeline.add(neighbourNodes(mikaTimeline.get(mikaTimeline.size() - 1), graph));
            if (sashaTimeline.get(sashaTimeline.size() - 1).isEmpty() || mikaTimeline
                    .get(mikaTimeline.size() - 1)
                    .isEmpty() || timelineRepeats(sashaTimeline, mikaTimeline)) {
                return null;
            }
        } while (!sashaTimeline.get(sashaTimeline.size() - 1).intersects(mikaTimeline.get(mikaTimeline.size() - 1)));

        int target = firstSameTargetOfTimelines(sashaTimeline, mikaTimeline);
        int[][] routes = new int[2][];
        routes[0] = findSingleRouteInTimeline(sashaTimeline, target, graph);
        routes[1] = findSingleRouteInTimeline(mikaTimeline, target, graph);
        return routes;
    }

    /**
     * Vergleicht den jeweils letzten Schritt der Schrittfolgen miteinander
     * und gibt den gemeinsamen besuchten Knoten zurück.
     *
     * @param sashaTimeline Sasha's Zeitleiste
     * @param mikaTimeline  Mika's Zeitleiste
     * @return Der Index im Graphen des gemeinsamen Knotens.
     */
    private static int firstSameTargetOfTimelines(List<BitSet> sashaTimeline, List<BitSet> mikaTimeline) {
        BitSet targets = (BitSet) sashaTimeline.get(sashaTimeline.size() - 1).clone();
        targets.and(mikaTimeline.get(mikaTimeline.size() - 1));
        return targets.nextSetBit(0);
    }

    /**
     * Ermittelt den Weg vom Zielpunkt zurück zum Startpunkt entgegengesetzt der Kantenrichtung des Graphen.
     *
     * @param timeline Die Zeitleiste der erreichbaren Knoten,
     *                 von der eine Route zu einem bestimmten Knoten im letzten Zeitpunkt der Zeitleiste gebaut werden soll.
     * @param target   Das Ziel am Ende der Zeitleiste der erreichbaren Knoten, zudem eine Route gebaut werden soll.
     * @param graph    Der Graph aus dem die Zeitleiste der erreichbaren Knoten (und dementsprechend auch das Ziel) stammt.
     * @return Eine Route zum Ziel in der Zeitleiste.
     */
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

    /**
     * Gibt die Knoten zurück, welche in einem Schritt entlang der Kantenrichtung,
     * von den aktuellen Knoten erreichbar sind.
     *
     * @param nodes Die aktuellen Knoten
     * @param graph Der Graph, welcher betrachtet wird
     * @return Die Menge der Knoten die in einem Schritt von den gegebenen Knoten erreichbar ist
     */
    private static BitSet neighbourNodes(BitSet nodes, BitSet[] graph) {
        BitSet neighbours = new BitSet();
        for (int i = 0; i < graph.length; i++) {
            if (nodes.get(i)) {
                neighbours.or(graph[i]);
            }
        }
        return neighbours;
    }

    /**
     * Ermittelt, ob die zwei Zeitleisten sich zu einem Zeitpunkt mit ihrem letzten Eintrag wiederholen.
     * Es wird daher festgestellt, ob beide Zeitleisten zu einem vorherigen Zeitpunkt an den genau gleichen Knoten waren,
     * wie zum aktuellen Zeitpunkt.
     *
     * @param sashaTimeline Sasha's Zeitleiste der erreichbaren Knoten
     * @param mikaTimeline  Mika's Zeitleiste der erreichbaren Knoten
     * @return True, wenn die Zeitleisten sich beide zu einem gleichen Zeitpunkt
     * mit dem neuen/letzten Zeitpunkt wiederholen.
     */
    private static boolean timelineRepeats(List<BitSet> sashaTimeline, List<BitSet> mikaTimeline) {
        BitSet current = sashaTimeline.get(sashaTimeline.size() - 1);
        BitSet repetitions = new BitSet(sashaTimeline.size());
        List<BitSet> timeline = new ArrayList<>(sashaTimeline.subList(0, sashaTimeline.size() - 1));
        for (int i = 0; i < timeline.size(); i++) {
            if (timeline.get(i).equals(current)) {
                repetitions.set(i);
            }
        }
        for (int i = repetitions.nextSetBit(0); i >= 0; i = repetitions.nextSetBit(i + 1)) {
            if (mikaTimeline.get(mikaTimeline.size() - 1).equals(mikaTimeline.get(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gibt zu jeder .txt Datei im Verzeichnis /Eingabedateien/ den zugehörigen Dateipfad zurück
     *
     * @return Die Pfade der Eingabedateien
     */
    private static List<Path> getPaths() {
        try (Stream<Path> walk = Files.walk(Paths.get("Aufgabe5/", "Eingabedateien/"))) {
            return walk
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".txt"))
                    .sorted()
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gibt die Zeilen der Datei eines Pfades zurück.
     *
     * @param path Der Dateipfad
     * @return Jede Zeile der Datei
     */
    private static List<String> getLines(Path path) {
        try (Stream<String> lines = Files.lines(path)) {
            return new ArrayList<>(lines.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}