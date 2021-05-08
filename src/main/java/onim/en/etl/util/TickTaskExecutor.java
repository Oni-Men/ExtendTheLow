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

  public static TickTask executeLater(Runnable run, long delay) {
    TickTask task = new TickTask(scheduledTasks.size(), delay, run);
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
    scheduledTasks.entrySet().removeIf(e -> e.getValue().isCanceled());
    scheduledTasks.values().forEach(task -> {
      task.tick();
    });
  }

  public static void cancel(int id) {
    if (scheduledTasks.containsKey(id))
      scheduledTasks.get(id).cancel();
  }

  public static class TickTask {

    public final int id;

    private long delay;
    private long interval;

    private final Runnable task;
    private int tick = 0;

    private final boolean once;

    private boolean canceled = false;

    private TickTask(int id, long delay, long interval, Runnable run) {
      this.id = id;
      this.delay = delay;
      this.interval = Math.max(1, interval);
      this.task = run;
      this.once = false;
    }

    private TickTask(int id, long delay, Runnable run) {
      this.id = id;
      this.delay = delay;
      this.interval = Math.max(1, interval);
      this.task = run;
      this.once = true;
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

        if (this.once) {
          this.cancel();
        }
      }
    }

    public void setTick(int delay) {
      this.tick = delay;
    }

    public void cancel() {
      this.canceled = true;
    }

    public boolean isCanceled() {
      return this.canceled;
    }
  }
}
