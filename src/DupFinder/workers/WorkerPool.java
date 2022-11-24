package DupFinder.workers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import DupFinder.DupFinder;
import DupFinder.interfaces.FileDataCallback;

public class WorkerPool implements Runnable {

  private BlockingQueue<String> fileQueue;
  private ExecutorService executor;
  private FileDataCallback fileDataCallback;

  public WorkerPool(BlockingQueue<String> fileQueue, int numThreads, FileDataCallback callback) {
    this.fileQueue = fileQueue;
    this.executor = Executors.newFixedThreadPool(numThreads);
    this.fileDataCallback = callback;
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
        executor.execute(new Worker(file, fileDataCallback));
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

}
