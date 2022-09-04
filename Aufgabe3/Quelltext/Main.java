import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        for(Path path : getPaths()) {
            List<String> lines = getLines(path);
            System.out.println(path.getFileName() + ": " + lines.size());
            byte[][] puzzle = new byte[9][9];
            for(byte row = 0; row < 9; row++) {
                String line = lines.get(row);
                line = line.replaceAll("[^0-9]", "");
                for(byte column = 0; column < 9; column++) {
                    puzzle[row][column] = (byte) Character.getNumericValue(line.charAt(column));
                }
            }
            byte[][] permutation = new byte[9][9];
            for(byte row = 10; row < 19; row++) {
                String line = lines.get(row);
                line = line.replaceAll("[^0-9]", "");
                for(byte column = 0; column < 9; column++) {
                    permutation[row-10][column] = (byte) Character.getNumericValue(line.charAt(column));
                }
            }
            System.out.println("Puzzle:");
            for(byte row = 0; row < 9; row++) {
                for(byte column = 0; column < 9; column++) {
                    System.out.print(puzzle[row][column]+" ");
                }
                System.out.println();
            }
            System.out.println("Permutation:");
            for(byte row = 0; row < 9; row++) {
                for(byte column = 0; column < 9; column++) {
                    System.out.print(permutation[row][column]+" ");
                }
                System.out.println();
            }
        }

        byte[][] original = new byte[][]{ //original
                {5, 0, 0, 0, 8, 0, 0, 4, 9},
                {0, 0, 0, 5, 0, 0, 0, 3, 0},
                {0, 6, 7, 3, 0, 0, 0, 0, 1},
                {1, 5, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 2, 0, 8, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 1, 8},
                {7, 0, 0, 0, 0, 4, 1, 5, 0},
                {0, 3, 0, 0, 0, 2, 0, 0, 0},
                {4, 9, 0, 0, 5, 0, 0, 0, 3}
        };
        byte[][] permutation = new byte[][]{ //permutation
                {0, 5, 1, 0, 9, 0, 6, 0, 0},
                {0, 4, 0, 6, 0, 0, 0, 0, 0},
                {0, 0, 2, 4, 0, 0, 0, 7, 8},
                {0, 0, 0, 0, 0, 0, 2, 6, 0},
                {0, 2, 9, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 3, 0, 9, 0, 0, 0},
                {0, 6, 0, 0, 0, 5, 8, 0, 0},
                {2, 0, 0, 0, 0, 3, 0, 4, 0},
                {0, 0, 4, 0, 6, 0, 5, 1, 0}
        };

        // A = {1 1 1 1 2 2 3 3 3 3 4 4 4 5 5 5 5 5 6 7 7 8 8 8 9 9}
        // B = {1 1 2 2 2 2 3 3 4 4 4 4 5 5 5 6 6 6 6 6 7 8 8 9 9 9}

        exchangeHashes(original, permutation)
                .forEach((key, value) -> System.out.println(key+1 + ": " + Arrays.toString(value)));
        for (boolean[] booleans :  hash(original)) {
            System.out.println(Arrays.toString(booleans));
        }
        for (boolean[] booleans :  hash(permutation)) {
            System.out.println(Arrays.toString(booleans));
        }

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
        for(byte k = 0; k < 9; k++) {
            boolean[] v = new boolean[9];
            for(byte i = 0; i < 9; i++) {
                if(valuesOriginal[i] == valuesPermutation[k]) v[i] = true;
            }
            exchanges.put(k, v);
        }
        return exchanges;
    }

    private static byte[] countValuesOfMap(byte[][] map) {
        byte[] count = new byte[9];
        for(byte[] constrain : map) {
            for(byte b : constrain) {
                if(b != 0) count[b-1]++;
            }
        }
        return count;
    }

    private static boolean[][] hash(byte[][] grid) {
        boolean[][] hash = new boolean[2][81];
        for(byte cons = 0; cons < 2; cons++) {
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