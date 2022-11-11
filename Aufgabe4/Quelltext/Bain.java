import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Bain {
    public static final String TEXT_RED = "\u001B[31m";
    public static final String TEXT_BLACK = "\u001B[30m";
    public static final String TEXT_GREEN = "\u001B[32m";
    public static final String TEXT_BLUE = "\u001B[34m";
    public static final String TEXT_RESET = "\u001B[0m";
    public static final String TEXT_PURPLE = "\u001B[35m";
    public static final String TEXT_CYAN = "\u001B[36m";
    public static final String TEXT_YELLOW = "\u001B[33m";
    public static final String TEXT_WHITE = "\u001B[37m";

//    private record Task(int entranceTime, int duration) {
//    }

    private static class Task {
        final int entranceTime;
        final int duration;

        private Task(int entranceTime, int duration) {
            this.entranceTime = entranceTime;
            this.duration = duration;
        }
    }

    abstract private static class TaskPriorityDelegate {
        public abstract String getName();

        public abstract void sortTaskIntoCurrentTaskList(List<Task> currentTaskList, Task newTask);

        /**
         * Remove and return one task from List
         *
         * @param currentTaskList
         * @return
         */
        public Task pickTask(List<Task> currentTaskList) {
            return currentTaskList.remove(0);
        }
    }

    static class FiFoTaskPriorityDelegate extends TaskPriorityDelegate {
        @Override
        public String getName() {
            return "Auftrag Reihenfolge priorisieren";
        }

        @Override
        public void sortTaskIntoCurrentTaskList(List<Task> currentTaskList, Task newTask) {
            currentTaskList.add(newTask);
        }
    }

    static class ShortestDurationTaskPriorityDelegate extends TaskPriorityDelegate {
        @Override
        public String getName() {
            return "Kürzester Auftrag priorisieren";
        }

        @Override
        public void sortTaskIntoCurrentTaskList(List<Task> currentTaskList, Task newTask) {
            for (int i = 0; i < currentTaskList.size(); i++) {
                if (currentTaskList.get(i).duration > newTask.duration) {
                    currentTaskList.add(i, newTask);
                    return;
                }
            }
            currentTaskList.add(newTask);
        }
    }

    private static final TaskPriorityDelegate[] taskPriorityDelegates = new TaskPriorityDelegate[]{
//            new FiFoTaskPriorityDelegate(),
            new ShortestDurationTaskPriorityDelegate(),
    };

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

    private static List<Task> tasksFromStrings(List<String> lines) {
        return lines.stream().map((line) -> {
            int entranceTime = Integer.parseInt(line.split(" ")[0]);
            int duration = Integer.parseInt(line.split(" ")[1]);
            return new Task(entranceTime, duration);
        }).toList();
    }

    /**
     * @param tasks
     * @param taskPriorityDelegate
     * @return Gesamte gewartete Zeit für alle Aufträge
     */
    private static void simulateProcessingTasks2(List<Task> tasks, TaskPriorityDelegate taskPriorityDelegate) {
        int[] tmp = new int[tasks.size()]; // TODO: REMOVE
        int maxWaitingTime = 0;
        int allWaitingTime = 0;
        int time = 60 * 9;
        int nextBreak = time + 60 * 8;
        int firstTaskNotOnCurrentTaskList = 0;
        Task currentlyExecutingTask = null;
        int currentlyExecutingTaskProgress = 0;
        List<Task> currentTaskList = new ArrayList<>(tasks.size());
        while (firstTaskNotOnCurrentTaskList != tasks.size() || !currentTaskList.isEmpty() || currentlyExecutingTask != null) {
            assert firstTaskNotOnCurrentTaskList <= tasks.size();
            if (firstTaskNotOnCurrentTaskList != tasks.size() && tasks.get(firstTaskNotOnCurrentTaskList).entranceTime <= time) {
                taskPriorityDelegate.sortTaskIntoCurrentTaskList(currentTaskList, tasks.get(firstTaskNotOnCurrentTaskList));
                firstTaskNotOnCurrentTaskList++;
                continue;
            }

            if (currentlyExecutingTask == null && !currentTaskList.isEmpty()) {
                currentlyExecutingTask = taskPriorityDelegate.pickTask(currentTaskList);
            }

            int passedTime; // can be as high as Integer.MAX_VALUE
            if (currentlyExecutingTask == null) {
                passedTime = tasks.get(firstTaskNotOnCurrentTaskList).entranceTime - time;
            } else {
                passedTime = currentlyExecutingTask.duration - currentlyExecutingTaskProgress;
            }

            if (time + passedTime >= nextBreak) {
                passedTime = nextBreak - time;
            }

            if (currentlyExecutingTask != null) {
                assert currentlyExecutingTaskProgress + passedTime <= currentlyExecutingTask.duration;
                currentlyExecutingTaskProgress += passedTime;
                if (currentlyExecutingTask.duration == currentlyExecutingTaskProgress) {
                    int waitedTime = (time + passedTime) - currentlyExecutingTask.entranceTime;
                    allWaitingTime += waitedTime;
                    if (waitedTime > maxWaitingTime) {
                        maxWaitingTime = waitedTime;
                    }
                    tmp[tasks.indexOf(currentlyExecutingTask)] = waitedTime; // TODO: REMOVE
                    currentlyExecutingTask = null;
                    currentlyExecutingTaskProgress = 0;
                }
            }
            time += passedTime;
            if (firstTaskNotOnCurrentTaskList == tasks.size() && currentTaskList.isEmpty() && currentlyExecutingTask == null) {
                break;
            }
            if (time == nextBreak) {
                time += 60 * 16;
                nextBreak += 60 * 24;
            }
        }
        double averageTaskProcessingTime = (double) allWaitingTime / (double) tasks.size();
        averageTaskProcessingTime = ((double) Math.round(averageTaskProcessingTime * 10)) / 10;

        System.out.println("Durchschnittliche Wartezeit pro Auftrag: " + averageTaskProcessingTime + " minuten");
        System.out.println("Gesamte Wartezeit für alle Aufträge: " + allWaitingTime + " minuten");
        System.out.println("Längste Wartezeit in allen Aufträgen: " + maxWaitingTime + " minuten");
        System.out.println("Nach " + time + " minuten von t0 aus fertig");
        IntStream.of(tmp).forEach(System.out::println); // TODO: rmevmoe
    }


    private static void simulateProcessingTasks(List<Task> tasks, TaskPriorityDelegate taskPriorityDelegate) {
        int[] tmp = new int[tasks.size()]; // TODO: REMOVE
        int tmp_i = 0; // TODO: REMOVE
        int maxWaitedTime = 0;
        int allWaitingTime = 0;
        int time = 9 * 60;
        int nextBreak = 17 * 60;
        int firstTaskNotOnTaskQueue = 0;
        List<Task> taskQueue = new ArrayList<>(tasks.size());
        while (firstTaskNotOnTaskQueue != tasks.size() || !taskQueue.isEmpty()) {
            // TODO: korrigiert fehler
            if (time == nextBreak) {
                time += (9 + (24 - 17)) * 60;
                nextBreak += 24 * 60;
            }
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
                tmp[tasks.indexOf(currentlyExecutingTask)] = waitedTime; // TODO: REMOVE
                allWaitingTime += waitedTime;
                maxWaitedTime = Math.max(waitedTime, maxWaitedTime);
                tmp_i++; // TODO: REMOVE
            } else {
                int targetedTime = tasks.get(firstTaskNotOnTaskQueue).entranceTime;
                while(time != targetedTime) {
                    assert time < targetedTime;
                    if (time == nextBreak) {
                        nextBreak += 24 * 60;
                    }
                    time = Math.min(targetedTime, nextBreak);
                }
            }
        }
        System.out.println(tmp_i);
        double averageTaskProcessingTime = (double) allWaitingTime / (double) tasks.size();
        averageTaskProcessingTime = ((double) Math.round(averageTaskProcessingTime * 10)) / 10;

        System.out.println("Durchschnittliche Wartezeit pro Auftrag: " + averageTaskProcessingTime + " minuten");
        System.out.println("Gesamte Wartezeit für alle Aufträge: " + allWaitingTime + " minuten");
        System.out.println("Längste Wartezeit in allen Aufträgen: " + maxWaitedTime + " minuten");
        System.out.println("Nach " + time + " minuten von t0 aus fertig");
        IntStream.of(tmp).forEach(System.out::println); // TODO: rmevmoe
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
