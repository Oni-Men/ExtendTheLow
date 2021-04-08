package onim.en.etl.util;

import java.util.LinkedList;
import java.util.List;

public class TickTaskExecutor {

  private static List<Runnable> taskList = new LinkedList<>();

  public static void addTask(Runnable task) {
    taskList.add(task);
  }

  public static Runnable getNextTask() {
    if (taskList.isEmpty()) {
      return null;
    }

    return taskList.remove(0);
  }
}
