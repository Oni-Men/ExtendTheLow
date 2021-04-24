package onim.en.etl.font;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FontGenerateWorker {

  private static ExecutorService executor = Executors.newFixedThreadPool(20);
  private static List<FontGenerateData> serviceResultList = Collections.synchronizedList(new LinkedList<>());

  public static void addGenerateTask(Callable<FontGenerateData> callable) {
    executor.execute(() -> {
      try {
        FontGenerateData result = callable.call();
        serviceResultList.add(result);
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  public static FontGenerateData getNextResult() {
    if (serviceResultList.isEmpty())
      return null;
    return serviceResultList.remove(0);
  }

}
