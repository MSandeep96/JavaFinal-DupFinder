package DupFinder.workers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import DupFinder.DupFinder;
import DupFinder.utils.SafeQueue;

public class WorkerPool implements Runnable {

  private BlockingQueue<String> fileQueue;
  private ExecutorService executor;

  public WorkerPool(BlockingQueue<String> fileQueue, int numThreads) {
    this.fileQueue = fileQueue;
    this.executor = Executors.newFixedThreadPool(numThreads);
  }

  public void startReading() {
    new Thread(this).start();
  }

  @Override
  public void run() {
    while (true) {
      try {
        String file = fileQueue.take();
        if (file.equals(DupFinder.SAFE_WORD)) {
          break;
        }
        executor.execute(new Worker(file));
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

}
