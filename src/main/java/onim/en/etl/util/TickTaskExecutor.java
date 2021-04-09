package onim.en.etl.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Maps;

public class TickTaskExecutor {

  private static List<Runnable> taskList = new LinkedList<>();
  private static HashMap<Integer, TickTask> scheduledTasks = Maps.newHashMap();

  public static void addTask(Runnable task) {
    taskList.add(task);
  }

  public static TickTask scheduleTask(Runnable run, long delay, long interval) {
    TickTask task = new TickTask(scheduledTasks.size(), delay, interval, run);
    scheduledTasks.put(task.id, task);
    return task;
  }

  public static Runnable getNextTask() {
    if (taskList.isEmpty()) {
      return null;
    }

    return taskList.remove(0);
  }

  public static void advanceScheduledTasks() {
    scheduledTasks.values().forEach(task -> {
      task.tick();
    });
  }

  public static void cancel(int id) {
    scheduledTasks.remove(id);
  }

  public static class TickTask {

    public final int id;

    private long delay;
    private long interval;

    private final Runnable task;
    private int tick = 0;

    private TickTask(int id, long delay, long interval, Runnable run) {
      this.id = id;
      this.delay = delay;
      this.interval = Math.max(1, interval);
      this.task = run;
    }

    protected void tick() {
      this.tick++;

      if (delay != 0 && tick < delay) {
        return;
      }

      delay = 0;

      if (this.tick >= this.interval) {
        this.tick = 0;
        this.task.run();
      }
    }

    public void cancel() {
      TickTaskExecutor.cancel(this.id);
    }
  }
}
