import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;


/*
idgaf deez nuts
 Solution is to write an algorithm to generate permutations of a multiset with repetition
 http://combos.org/multiperm
 https://arxiv.org/pdf/1502.06062.pdf
 https://core.ac.uk/download/pdf/82277934.pdf
 https://math.stackexchange.com/questions/4398485/sudoku-puzzle-with-only-1-and-0-and-other-restrictions
 */
public class Main {
    public static void main(String[] args) {
        List<byte[][][]> tasklist = new ArrayList<>();
        for (Path path : getPaths()) {
            byte[][][] task = new byte[5][9][9];
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
            //TODO: compare count of written fields between original and permutation
            // if not equal, return false
            for(byte i = 2; i < 5; i++) {
                task[i] = rotate(task[i-1]);
            }
            tasklist.add(task);
        }


        for(byte[][][] maps : tasklist) {
            HashMap<Byte, boolean[]> map = exchangeHashes(maps[0], maps[1]);
            map.forEach((key, value) -> System.out.println(key + " " + Arrays.toString(value)));
            //create list of all possible block permutations of maps[0]
            //compare each index with all four rotations of the permutation and stop if equal,
            //if not equal continue with the following
            //create for each index all non-block possible row and column permutations and save them in a list
            //compare each index with all four rotations of the permutation and stop if equal,
            //if not equal return false

            //
            break; //TODO: for debugging
        }


        //random usages
        /*exchangeHashes(original, permutation)
                .forEach((key, value) -> System.out.println(key+1 + ": " + Arrays.toString(value)));
        for (boolean[] booleans :  hash(original)) {
            System.out.println(Arrays.toString(booleans));
        }
        for (boolean[] booleans :  hash(permutation)) {
            System.out.println(Arrays.toString(booleans));
        }*/
    }

    //rotiert (wie ne uhr)
    private static byte[][] rotate(byte[][] map) {
        byte[][] rotation = new byte[9][9];
        for (byte i = 0; i < 9; i++) {
            for (byte j = 0; j < 9; j++) {
                rotation[i][j] = map[9 - j - 1][i];
            }
        }
        return rotation;
    }

    //gönnt paths
    private static List<Path> getPaths() {
        List<Path> paths;
        try (Stream<Path> walk = Files.walk(Paths.get("Aufgabe3/", "Eingabedateien/"))) {
            paths = walk.filter(Files::isRegularFile).filter(path -> path.getFileName().toString().endsWith(".txt")).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return paths;
    }

    //gönnt lines
    private static List<String> getLines(Path path) {
        List<String> data;
        try (Stream<String> lines = Files.lines(path)) {
            data = new ArrayList<>(lines.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return data;
    }

    //I guess id ont need that shit, bless god
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

    //TODO: understand my own method again :-(
    // i guess it saves on index 0 of the count array the count of 1's in the given map, on index 1 the count of 2's and so on...
    // but i am absolutely not sure, damn
    private static byte[] countValuesOfMap(byte[][] map) {
        byte[] count = new byte[9];
        for (byte[] constrain : map) {
            for (byte b : constrain) {
                if (b != 0) count[b - 1]++;
            }
        }
        return count;
    }

    //TODO: understand this!!! no idea :D:D:D
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