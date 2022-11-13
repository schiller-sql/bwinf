import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

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
     * Die Methode "getPositions" sucht für eine durch den Parameter gegebene Eingabedatei das erste Wort im Buch.
     * Dabei wird jede Zeile des Buchs einzeln auf das Anfangswort durchsucht
     * und bei einem Fund wird sowohl die Zeile als auch die "Spalte" (der Index in der Zeile, an der das Wort beginnt) als Array an die Liste von Positionen hinzugefügt.
     * Diese Liste wird am Ende der Methode zurückgegeben.
     *
     * @param stoerung Die Eingabedatei, nach der das Buch durchsucht werden soll
     * @return Eine Liste von Positionen, an denen dasselbe Wort steht wie das erste Wort der Eingabedatei
     *
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
     * Die Methode "checkPositions" untersucht für jede durch den Parameter "positions" gegebene Position,
     * ob auch der Rest der Eingabedatei, die durch den Parameter "stoerung" gegeben ist,
     * durch die nach der Position folgenden Worte vervollständigt werden kann.
     * Dabei wird ersteinmal eine Liste aus Worten erstellt, das mit dem durch die Position gegebenen Wort startet.
     * Hat diese nicht mindestens die Länge der Eingabedatei, wird auch die nächste Zeile hinzugefügt.
     * Alle Elemente in der Liste werden danach erstmal von Sonderzeichen bereinigt, da diese auch nicht in der Eingabedatei vorgegeben sind.
     * Dann wird Wort für Wort mit denen der Eingabedatei verglichen, wobei Unterstriche (also Lücken in der Eingabedatei) nicht verglichen werden,
     * sondern jedes Wort akzeptieren.
     * Sobald an einer Stelle keine Übereinstellung gefunden wird, wird der Suchvorgang für die eine Position beendet.
     * Alle Stellen, die die Eingabedatei "lösen" können, werden an eine Liste angefügt, die nach Ende der Methode zurückgegeben wird.
     *
     * @param positions Eine Liste von Positionen, die überprüft werden soll
     * @param stoerung Die Eingabedatei, nach der die möglichen Positionen überprüft werden sollen
     * @return Eine Liste von Positionen, die die Eingabedatei "lösen" können
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
     * Die Methode "getText" sucht für die durch die Zeile und den Index in der Zeile gegebene Stelle den nächsten Teilsatz.
     * Dabei wird das nächste Satzzeichen gesucht und der Text zwischen der gegebenen Stelle und dem Satzzeichen zurückgegeben.
     *
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
     * Die Methode "formatText" ersetzt alle im Buch vorkommende Sonderzeichen mit einem Doppelkreuz ("#").
     *
     * @param text Der zu bearbeitende Text
     * @return Der bearbeitete Text
     */
    private static String formatText(StringBuilder text) {
        return text.toString().replaceAll("[.!?,»«'\"()\\[\\]]", "#");
    }

    /**
     * Die Methode "evaluatingFiles" liest die Eingabedateien sowie das Buch ein.
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
     * Die Methode "getPaths" gibt alle im Verzeichnis der Eingabedateien gefundene Text Dateien (".txt") als Pfade zurück.
     *
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
     * Die Methode "getLines" gibt die Datei unter dem gegebenen Pfad als Liste aus Zeilen zurück.
     *
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
