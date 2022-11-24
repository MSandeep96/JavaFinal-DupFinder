package DupFinder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import DupFinder.interfaces.UICallback;
import DupFinder.ui.SearchScreen;
import DupFinder.utils.SafeQueue;
import DupFinder.workers.WorkerPool;

public class DupFinder implements UICallback, Runnable {

  BlockingQueue<String> fileQueue;
  LinkedList<String> directoryQueue;
  private SearchScreen searchScreen;
  private WorkerPool workerPool;

  public static final String SAFE_WORD = "/quit\"";
  private static final int NUM_THREADS = 15;

  public static void main(String[] args) {
    new DupFinder();
  }

  public DupFinder() {
    fileQueue = new LinkedBlockingQueue<String>();
    searchScreen = new SearchScreen(this);
    workerPool = new WorkerPool(fileQueue, NUM_THREADS);
  }

  @Override
  public void startSearch(String startPath) {
    try {
      Path folder = Paths.get(startPath);
      if (!Files.exists(folder))
        return;
      if (!Files.isDirectory(folder))
        return;
    } catch (Exception e) {
      System.out.println("Invalid path");
      e.printStackTrace();
      return;
    }
    directoryQueue = new LinkedList<String>();
    directoryQueue.add(startPath);
    new Thread(this).start();
    workerPool.startReading();
  }

  // Checks for new files in the directory queue and adds them to the file queue
  @Override
  public void run() {
    while (!directoryQueue.isEmpty()) {
      String path = directoryQueue.poll();
      try {
        Path pathObj = Paths.get(path);
        Files.list(pathObj).forEach(p -> {
          if (Files.isDirectory(p)) {
            System.out.println("Adding directory: " + p.toString());
            directoryQueue.add(p.toString());
          } else {
            System.out.println("Found file: " + p.toString());
            fileQueue.add(p.toString());
          }
        });
      } catch (Exception e) {
        System.out.println("Error reading directory" + path);
        e.printStackTrace();
      }
    }
    fileQueue.add(SAFE_WORD);
  }

}
