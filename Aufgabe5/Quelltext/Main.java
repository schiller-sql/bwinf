import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * Begriffserklärung für die Dokumentation der Main und der weiteren Methoden:
 * <li>Darstellung und Implementierung des Parcours:
 * <ul>
 *     <li>Die Darstellung der Parcours erfolgt als gerichteter Graph in Matrix-form, dabei ist zu beachten,
 * dass in der Matrix-form die Nummer 0 in der Matrix zu der 1 im Parcours korrespondiert, usw.
 * <li>Die konkrete Implementierung dieses gerichten Graphen besteht aus einem Array aus {@link BitSet}s,
 * das Array und die BitSets haben beide die Länge gleich der Anzahl an Knoten.
 * Jeder BitSet im Array repräsentiert einen Knoten, der Index des BitSet im Array, ist welcher Knoten das ist.
 * Jeder Index im BitSet ist eine mögliche gerichtete Kante ausgehend von der Kante des Indexes Arrays des BitSets,
 * wenn dort eine gerichtete Kante existiert, dann ist der Bit auf 1 gesetzt, ansonsten ist er auf 0.
 * <li>Definition einer Zeitleiste (im Quellcode Timeline) der erreichbaren Knoten:
 * <li> Eine Zeitleiste beschreibt den Verlauf der Menge von allen möglich erreichbaren Knoten
 * nach einer Anzahl an Schritten von einem Startknoten.
 * <li>Die Menge der Knoten beim Zeitpunkt 0 ist also nur der Startknoten.
 * Die Menge der Knoten beim Zeitpunkt n (n > 0) sind alle Knoten die in n Schritten vom Startknoten erreichbar sind.
 * <li>Der Zeitpunkt n (n > 0) kann auch definiert werden als die Menge der Knoten,
 * die direkt (in einem Schritt) von den Knoten in der Menge von Zeitpunkt n - 1 erreichbar sind.
 * </ul>
 * <li>Implementierung von Zeitleisten der erreichbaren Knoten:
 * <ul>
 * <li>Jeder Zeitpunkt in einer Zeitleiste von erreichbaren Knoten wird als BitSet implementiert.
 * Beim BitSet korrespondiert der Index des Bit zu einem Knoten im Graph,
 * das BitSet hat also die gleiche länge wie die Anzahl an Knoten. Ist der Bit eines Knoten auf 1 gesetzt,
 * ist dieser Knoten zu diesem Zeitpunkt erreichbar.
 * <li>Zeitleisten werden als Liste von BitSets implementiert, das erste BitSet ist der Zeitpunkt 0.
 * </ul>
 */
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
     * Findet eine Route, von dem nullten und ersten Knoten zu einem gemeinsamen Knoten in einer gleichen Anzahl von Schritten.
     *
     * Es werden sich dabei erst zwei Zeitleisten der erreichbaren Knoten von 0. und 1.
     * generiert von der Methode {@link Main#generatesTimelines(BitSet[])}.
     * Diese Zeitleisten gehen bis zu einem Zeitpunkt, wo beide Zeitleisten in ihren Mengen
     * min. einen gleichen Knoten enthalten. Gibt es diese Zeitleiste nicht,
     * gibt generatesTimelines null zurück und diese Methode auch. Falls die Zeitleiste gültig ist,
     * wird mit der Methode {@link Main#firstSameTargetOfTimelines(List[])}
     * ein möglicher gemeinsamer Knoten anhand der Zeitleiste zurückgegeben.
     * Anschließend wird mit der Methode {@link Main#findSingleRouteInTimeline(List, int, BitSet[])}
     * ein Weg zu diesem Ziel Knoten anhand der Zeitleiste generiert.
     *
     * @param graph Der Graph zu dem eine Route vom 0. und 1. Knoten zu einem gemeinsamen Knoten gebildet werden soll.
     * @return Eine Route zu einem Knoten der vom 0. und 1. Knoten in der gleichen Anzahl von Schritten erreichbar ist.
     */
    private static int[][] sameTargetRoute(BitSet[] graph) {
        List<BitSet>[] timelines = generatesTimelines(graph);
        if (timelines == null) return null;
        int target = firstSameTargetOfTimelines(timelines);
        int[][] routes = new int[2][];
        for (int i = 0; i < 2; i++) {
            routes[i] = findSingleRouteInTimeline(timelines[i], target, graph);
        }
        return routes;
    }

    /**
     * Vergleicht den jeweils letzten Schritt der Schrittfolgen miteinander
     * und gibt den gemeinsamen gemerkten Knoten zurück.
     *
     * @param timelines Zwei langes Array mit zwei Zeitleisten.
     * @return Der Index im Graphen des gemeinsamen Knotens.
     */
    private static int firstSameTargetOfTimelines(List<BitSet>[] timelines) {
        BitSet targets = (BitSet) timelines[0].get(timelines[0].size() - 1).clone();
        targets.and(timelines[1].get(timelines[1].size() - 1));
        return targets.nextSetBit(0);
    }

    /*
     * Findet von einer Zeitleiste der erreichbaren Knoten eine Route,
     * die zu einem Zielknoten führt, welcher am Ende der Zeitleiste erreicht wird.
     * <p>
     * Die Route besteht aus einem int Array welches an jedem Index von 0 angibt an welchem Knoten man sein sollte.
     * Das Array der Route ist dementsprechend gleichlang wie die Zeitleiste.
     * <p>
     * Erstellt als Erstes ein leeres int Array der Länge der Zeitleiste.
     * Zuerst bleibt das übergebene Ziel der Zielknoten.
     * Dann wird von oben jeder Zeitpunkt, bis auf der Letzte, der Zeitleiste durchgegangen.
     * Als Erstes wird am in der Route korrespondierenden Index das jetzige Ziel eingetragen.
     * Dann wird in der Menge der Knoten, die im vorherigen Zeitpunkt sind,
     * gesucht nach einem Knoten der zum Ziel führt. Dieser Knoten wird dann als neuer Zielknoten anvisiert.
     * Da der erste Zeitpunkt nicht durchgegangen wurde und dementsprechend auch nicht der erste Index der Route,
     * aber das Ziel, kann am ersten Index der Route der Wert des Zieles stehen, da dieser ja zum Ziel davor führt.
     * Dieser Wert ist gleich dem Startknoten der Zeitleiste.
     */

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

    /*
     * Generiert zwei Zeitleisten der erreichbaren Knoten von Knoten 0. und 1.,
     * wo beide Zeitleisten am Ende auf einen gleichen Knoten kommen. Existieren diese Zeitleisten nicht,
     * wird null zurückgegeben.
     * <p>
     * Dafür wird als erster Zeitpunkt in beiden Zeitleisten jeweils nur die Startknoten (also 0. und 1.) eingetragen.
     * In einer Schleife wird dann mit der Methode {@link Main#neighbourNodes(BitSet, BitSet[])}
     * der nächste Zeitpunkt für beide Zeitleisten bestimmt und eingetragen.
     * Bevor dem nächsten Zeitpunkt Schleifendurchgang wird überprüft,
     * ob in einer der beiden Zeitleisten kein Knoten mehr erreichbar ist. Ist dies der Fall, wird null zurückgegeben.
     * Außerdem wird überprüft, ob die Zeitleisten sich irgendwann wiederholen,
     * mit der Methode {@link Main#timelineRepeats(List[])}, dann wird auch null zurückgeben.
     * Als Nächstes wird überprüft, ob beide Zeitleisten min. einen selben Knoten enthalten,
     * dann werden die Zeitleisten zurückgegeben.
     */
     /**
     * Ermittelt für die Startknoten null und eins jeweils die Schrittfolge zum Zielknoten.
     * Sollte es keine Lösung geben, wird null zurückgegeben.
     *
     * @param graph Der Graph, von dem die Zeitleisten der erreichbaren Knoten generiert werden sollen.
     * @return Die generierten Zeitleisten der erreichbaren Knoten von 0. und 1.,
     * oder falls diese nicht existieren null.
     */
    private static List<BitSet>[] generatesTimelines(BitSet[] graph) {
        //TODO: Wenn ein Teil der Timeline in einen Loop läuft, kann dieser auch erkannt und abgebrochen werden
        // Lohnt sich das? Oder wäre der Algorithmus zum finden der Loops rechenaufwändiger, als im Loop zu laufen?
        BitSet sashaFirst = new BitSet(graph.length);
        BitSet mikaFirst = new BitSet(graph.length);
        sashaFirst.set(0);
        mikaFirst.set(1);
        @SuppressWarnings("Generic array creation")
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
                    .isEmpty() || timelineRepeats(timelines)) {
                return null;
            }
        } while (!timelines[0].get(timelines[0].size() - 1).intersects(timelines[1].get(timelines[1].size() - 1)));
        return timelines;
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
     * TODO: Javadoc muss entsprechend der Methodenänderungen erneuert werden
     * Ermittelt, ob zwei Zeitleisten der erreichbaren Knoten sich zu einem gleichen Zeitpunkt mit ihrem jeweils ersten Element wiederholen.
     * Es wird also festgestellt, ob die beiden Zeitleisten irgendwann schon mal an den genau gleichen Knoten waren,
     * wie zum neuesten/letzten Zeitpunkt. Ist dies der Fall, bedeutet das,
     * dass die Zeitleisten sich immer wieder wiederholen werden.
     * Wenn bisher noch kein Zeitpunkt gefunden wurde in beiden Zeitleisten,
     * wo beide auf einem gleichen Knoten sind, ist dieser Zeitpunkt unmöglich und kann nie gefunden werden.
     * <p>
     * Es wird als Erstes für die erste Zeitleiste durchgelaufen,
     * ob irgendwo eine Wiederholung des ersten Elementes auftritt. Geschieht dies, wird sich ein Index gemerkt
     * und an dieser Stelle an der anderen Zeitleiste dasselbe überprüft. Ansonsten wird false zurückgegeben.
     *
     * @param timelines Zwei Zeitleisten der erreichbaren Knoten mit gleicher Länge in einem Array,
     *                  die überprüft werden sollen.
     * @return True, wenn die Zeitleisten sich beide zu einem gleichen Zeitpunkt
     * mit dem neuen/letzten Zeitpunkt wiederholen.
     */
    private static boolean timelineRepeats(List<BitSet>[] timelines) {
        BitSet current = timelines[0].get(timelines[0].size() -1);
        BitSet repetitions = new BitSet(timelines[0].size());
        List<BitSet> timeline = new ArrayList<>(timelines[0].subList(0, timelines[0].size() -1));
        for(int i = 0; i < timeline.size(); i++) {
            if(timeline.get(i).equals(current)) {
                repetitions.set(i);
            }
        }
        //https://docs.oracle.com/javase/8/docs/api/java/util/BitSet.html#nextSetBit-int-
        for (int i = repetitions.nextSetBit(0); i >= 0; i = repetitions.nextSetBit(i+1)) {
            if(timelines[1].get(timelines[1].size() -1).equals(timelines[1].get(i))) {
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