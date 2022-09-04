import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        List<byte[][][]> tasklist = new ArrayList<>();
        for (Path path : getPaths()) {
            byte[][][] task = new byte[2][9][9];
            List<String> lines = getLines(path);
            System.out.println(path.getFileName() + ": " + lines.size());
            for (byte row = 0; row < 9; row++) {
                String line = lines.get(row);
                line = line.replaceAll("[^0-9]", "");
                for (byte column = 0; column < 9; column++) {
                    task[0][row][column] = (byte) Character.getNumericValue(line.charAt(column));
                }
            }
            for (byte row = 10; row < 19; row++) {
                String line = lines.get(row);
                line = line.replaceAll("[^0-9]", "");
                for (byte column = 0; column < 9; column++) {
                    task[1][row - 10][column] = (byte) Character.getNumericValue(line.charAt(column));
                }
            }
            tasklist.add(task);
        }

        /*exchangeHashes(original, permutation)
                .forEach((key, value) -> System.out.println(key+1 + ": " + Arrays.toString(value)));
        for (boolean[] booleans :  hash(original)) {
            System.out.println(Arrays.toString(booleans));
        }
        for (boolean[] booleans :  hash(permutation)) {
            System.out.println(Arrays.toString(booleans));
        }*/
    }

    private static List<Path> getPaths() {
        List<Path> paths;
        try (Stream<Path> walk = Files.walk(Paths.get("Aufgabe3/", "Eingabedateien/"))) {
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

    private static HashMap<Byte, boolean[]> exchangeHashes(byte[][] original, byte[][] permutation) {
        byte[] valuesOriginal = countValuesOfMap(original);
        byte[] valuesPermutation = countValuesOfMap(permutation);
        HashMap<Byte, boolean[]> exchanges = new HashMap<>(9);
        for (byte k = 0; k < 9; k++) {
            boolean[] v = new boolean[9];
            for (byte i = 0; i < 9; i++) {
                if (valuesOriginal[i] == valuesPermutation[k]) v[i] = true;
            }
            exchanges.put(k, v);
        }
        return exchanges;
    }

    private static byte[] countValuesOfMap(byte[][] map) {
        byte[] count = new byte[9];
        for (byte[] constrain : map) {
            for (byte b : constrain) {
                if (b != 0) count[b - 1]++;
            }
        }
        return count;
    }

    private static boolean[][] hash(byte[][] grid) {
        boolean[][] hash = new boolean[2][81];
        for (byte cons = 0; cons < 2; cons++) {
            byte i = 0;
            for (byte row = 0; row < 9; row++) {
                for (byte column = 0; column < 9; column++) {
                    hash[cons][i] = cons == 0 ? grid[row][column] != 0 : grid[column][row] != 0;
                    i++;
                }
            }
        }
        return hash;
    }
}