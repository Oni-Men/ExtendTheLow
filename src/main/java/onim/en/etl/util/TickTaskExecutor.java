package onim.en.etl.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Maps;

public class TickTaskExecutor {

  private static int taskIdCounter = 0;

  private static HashMap<Integer, TickTask> scheduledTasks = Maps.newHashMap();

  private static List<TickTask> syncTaskPool = new ArrayList<>();

  public static void addTask(Runnable task) {
    executeLater(task, 0);
  }

  public static TickTask scheduleTask(Runnable run, long delay, long interval) {
    TickTask task = new TickTask(taskIdCounter, delay, interval, run);
    syncTaskPool.add(task);
    taskIdCounter++;
    return task;
  }

  public static TickTask executeLater(Runnable run, long delay) {
    TickTask task = new TickTask(taskIdCounter, delay, run);
    syncTaskPool.add(task);
    taskIdCounter++;
    return task;
  }

  public static void advanceScheduledTasks() {
    scheduledTasks.entrySet().removeIf(e -> e.getValue().isCanceled());
    scheduledTasks.values().forEach(TickTask::tick);

    TickTask task = null;
    while (syncTaskPool.size() > 0) {
      task = syncTaskPool.remove(0);
      scheduledTasks.put(task.id, task);
    }

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
      this.interval = 0;
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

    public void setTick(int tick) {
      this.tick = tick;
    }

    public void cancel() {
      this.canceled = true;
    }

    public boolean isCanceled() {
      return this.canceled;
    }
  }
}
