import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Lösung der Aufgabe 5.
 */
public class Main {
    public static final String TEXT_GREEN = "\u001B[32m";
    public static final String TEXT_BLUE = "\u001B[34m";
    public static final String TEXT_RESET = "\u001B[0m";

    /**
     * Ein Auftrag
     *
     * @param entranceTime Der Eingangszeitpunkt des Auftrages in Minuten von t0
     * @param duration     Die Dauer des Auftrages in Minuten
     */
    private record Task(int entranceTime, int duration) {
    }

    /**
     * Eine Klasse die entscheidet in welcher Reihenfolge die eingehenden Aufträge verwaltet werden sollen.
     */
    abstract private static class TaskPriorityDelegate {
        /**
         * @return Der Name dieses Verfahrens, in welcher Reihenfolge die eingehenden Aufträge verwaltet werden sollen.
         */
        public abstract String getName();

        /**
         * Wo in der aktuellen Liste der schon eingegangen Aufträge ein neuer Auftrag eingehen soll.
         * Das hat aber keinen direkten Einfluss darauf, wann dieser neue Auftrag angenommen werden soll,
         * denn pickTask entscheidet dass.
         * Durch eine Sortierung der Liste durch sortTaskIntoCurrentTaskList
         * kann aber pickTask oft effizienter vorgehen.
         * <p>
         * Die implementierte Methode soll also an einer stellte in der taskQueue den newTask einsortieren.
         *
         * @param taskQueue Die aktuelle Liste der eingegangenen Aufträge
         * @param newTask   Der neue Auftrag, der in die Liste, der eingegangen Aufträge eingenommen werden soll.
         */
        public abstract void sortTaskIntoCurrentTaskList(List<Task> taskQueue, Task newTask);

        /**
         * Wählt den nächsten Auftrag zum Bearbeiten von der Liste der schon eingegangen Aufträge aus,
         * entfernt ihn aus dieser Liste und gibt ihn zurück.
         * Durch Sortieren dieser Liste in sortTaskIntoCurrentTaskList ist ein effizienteres Auswählen möglich.
         * <p>
         * Standardmäßig nimmt, wählt die Methode den ersten Auftrag in der Liste aus, da davon ausgegangen wird,
         * dass meistens die Liste in sortTaskIntoCurrentTaskList sortiert wird.
         *
         * @param taskQueue Die Liste der eingegangenen Aufträge.
         * @return Der Auftrag, der als nächstes bearbeitet werden soll.
         */
        public Task pickTask(List<Task> taskQueue) {
            return taskQueue.remove(0);
        }
    }

    /**
     * Eine Implementation von {@link TaskPriorityDelegate}, die immer den Auftrag der als neuestes eingegangen ist,
     * als letztes bearbeitet.
     */
    static class FiFoTaskPriorityDelegate extends TaskPriorityDelegate {
        @Override
        public String getName() {
            return "Auftrag Reihenfolge priorisieren";
        }

        /**
         * Sortiert immer den neuen Auftrag immer ganz hinten. Da wenn der Auftrag in pickTask ausgewählt wird,
         * immer der erste genommen wird, wird damit der älteste ausgewählt.
         */
        @Override
        public void sortTaskIntoCurrentTaskList(List<Task> taskQueue, Task newTask) {
            taskQueue.add(newTask);
        }
    }

    /**
     * Eine Implementation von {@link TaskPriorityDelegate}, der immer den Auftrag als nächstes bearbeitet,
     * der am kürzesten dauert.
     */
    static class ShortestDurationTaskPriorityDelegate extends TaskPriorityDelegate {
        @Override
        public String getName() {
            return "Kürzester Auftrag priorisieren";
        }

        /**
         * Sortiert den neuen Auftrag in die Liste, der eingegangen Aufträge, ein, nach absteigender Dauer.
         * Da wenn der Auftrag in pickTask ausgewählt wird, immer der kürzeste genommen wird.
         */
        @Override
        public void sortTaskIntoCurrentTaskList(List<Task> taskQueue, Task newTask) {
            for (int i = 0; i < taskQueue.size(); i++) {
                if (taskQueue.get(i).duration > newTask.duration) {
                    taskQueue.add(i, newTask);
                    return;
                }
            }
            taskQueue.add(newTask);
        }
    }

    /**
     * Alle Verfahren zur Auswahl des nächst zu bearbeitenden Auftrages.
     */
    private static final TaskPriorityDelegate[] taskPriorityDelegates = new TaskPriorityDelegate[]{
            new FiFoTaskPriorityDelegate(),
            new ShortestDurationTaskPriorityDelegate(),
    };

    /**
     * Für alle Eingabedateien werden alle Verfahren durchgeführt
     * mit {@link Main#simulateProcessingTasks(List, TaskPriorityDelegate)}.
     */
    public static void main(String[] args) {
        for (Path path : getPaths()) {
            String fileName = path.getFileName().toString();
            List<String> lines = getLines(path);
            List<Task> tasks = tasksFromStrings(lines);

            System.out.println(TEXT_BLUE + fileName + ":" + TEXT_RESET);
            for (TaskPriorityDelegate taskPriorityDelegate :
                    taskPriorityDelegates) {
                System.out.println(TEXT_GREEN + taskPriorityDelegate.getName() + ":" + TEXT_RESET);

                simulateProcessingTasks(tasks, taskPriorityDelegate);

                System.out.println();
            }
            System.out.println();
        }
    }

    /**
     * Aus einer Liste von Strings aus der Datei wird eine Liste von Aufträgen zurückgegeben.
     */
    private static List<Task> tasksFromStrings(List<String> lines) {
        return lines.stream().map((line) -> {
            int entranceTime = Integer.parseInt(line.split(" ")[0]);
            int duration = Integer.parseInt(line.split(" ")[1]);
            return new Task(entranceTime, duration);
        }).toList();
    }

    /**
     * Für eine Liste von Aufträgen, mit einem Verfahren,
     * also in welcher Reihenfolge die Aufträge durchgeführt werden soll,
     * soll eine Simulation durchgeführt werden.
     * Dabei wird ausgegeben wie lange jeder Auftrag durchschnittlich braucht,
     * was die gesamte Wartezeit ist und was die maximale Wartezeit ist.
     *
     * @param tasks                Die Aufträge die bearbeitet werden sollen, wo das erste element, der zeitlich erste Auftrag ist.
     * @param taskPriorityDelegate In welcher Reihenfolge die Aufträge bearbeitet werden sollen.
     */
    private static void simulateProcessingTasks(List<Task> tasks, TaskPriorityDelegate taskPriorityDelegate) {
        int maxWaitedTime = 0;
        int allWaitingTime = 0;
        int time = 9 * 60;
        int nextBreak = 17 * 60;
        int firstTaskNotOnTaskQueue = 0;
        List<Task> taskQueue = new ArrayList<>(tasks.size());
        while (firstTaskNotOnTaskQueue != tasks.size() || !taskQueue.isEmpty()) {
            while (firstTaskNotOnTaskQueue != tasks.size() && tasks.get(firstTaskNotOnTaskQueue).entranceTime <= time) {
                taskPriorityDelegate.sortTaskIntoCurrentTaskList(taskQueue, tasks.get(firstTaskNotOnTaskQueue));
                firstTaskNotOnTaskQueue++;
            }
            if (!taskQueue.isEmpty()) {
                Task currentlyExecutingTask = taskPriorityDelegate.pickTask(taskQueue);
                int currentlyExecutingTaskProgress = 0;
                while (currentlyExecutingTaskProgress != currentlyExecutingTask.duration) {
                    assert currentlyExecutingTaskProgress < currentlyExecutingTask.duration;
                    assert time <= nextBreak;
                    if (time == nextBreak) {
                        time += (9 + (24 - 17)) * 60;
                        nextBreak += 24 * 60;
                    }
                    int passedTime = currentlyExecutingTask.duration - currentlyExecutingTaskProgress;
                    if (time + passedTime > nextBreak) {
                        passedTime = nextBreak - time;
                    }
                    time += passedTime;
                    currentlyExecutingTaskProgress += passedTime;
                }
                int waitedTime = time - currentlyExecutingTask.entranceTime;
                allWaitingTime += waitedTime;
                maxWaitedTime = Math.max(waitedTime, maxWaitedTime);
                if (time == nextBreak) {
                    time += (9 + (24 - 17)) * 60;
                    nextBreak += 24 * 60;
                }
            } else {
                int targetedTime = tasks.get(firstTaskNotOnTaskQueue).entranceTime;
                while (time < targetedTime) {
                    time = targetedTime;
                    if (time >= nextBreak) {
                        time = nextBreak + 9;
                        nextBreak += 24 * 60;
                    }
                }
            }
        }
        double averageTaskProcessingTime = (double) allWaitingTime / (double) tasks.size();
        averageTaskProcessingTime = ((double) Math.round(averageTaskProcessingTime * 10)) / 10;

        System.out.println("Durchschnittliche Wartezeit pro Auftrag: " + averageTaskProcessingTime + " minuten");
        System.out.println("Gesamte Wartezeit für alle Aufträge: " + allWaitingTime + " minuten");
        System.out.println("Längste Wartezeit in allen Aufträgen: " + maxWaitedTime + " minuten");
    }

    /**
     * Finde alle Pfade zu den Eingabedateien und gib sie als Liste zurück.
     *
     * @return Die Pfade der Eingabedateien
     */
    private static List<Path> getPaths() {
        try (Stream<Path> walk = Files.walk(Paths.get("Aufgabe4/", "Eingabedateien/"))) {
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
     * Gebe die Zeilen der Datei eines Pfades als Liste zurück.
     *
     * @param path Der Pfad der Datei, die man möchte.
     * @return Jede Zeile als String in einer Liste.
     */
    private static List<String> getLines(Path path) {
        try (Stream<String> lines = Files.lines(path)) {
            return lines.toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
