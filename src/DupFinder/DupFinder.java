package DupFinder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

import DupFinder.interfaces.UICallback;
import DupFinder.ui.SearchScreen;
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
    fileQueue = new LinkedBlockingQueue<String>(200);
    searchScreen = new SearchScreen(this);
    workerPool = new WorkerPool(fileQueue, NUM_THREADS, searchScreen);
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
    searchScreen.setCurrentDirectory(startPath);
    directoryQueue = new LinkedList<String>();
    directoryQueue.add(startPath);
    new Thread(this).start();
    workerPool.startReading();
  }

  private static boolean isHiddenPath(String path) {
    String fileName = path.substring(path.lastIndexOf("/") + 1);
    if (fileName.startsWith("."))
      return true;
    return false;
  }

  // Checks for new files in the directory queue and adds them to the file queue
  @Override
  public void run() {
    while (!directoryQueue.isEmpty()) {
      String path = directoryQueue.poll();
      if (isHiddenPath(path))
        continue;
      try {
        Path pathObj = Paths.get(path);
        if (Files.isSymbolicLink(pathObj))
          continue;
        try (Stream<Path> stream = Files.list(pathObj)) {
          stream.forEach((p) -> {
            if (Files.isReadable(pathObj)) {
              if (Files.isDirectory(p)) {
                directoryQueue.add(p.toString());
              } else {
                try {
                  fileQueue.put(p.toString());
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
              }
            }
          });
        }
      } catch (Exception e) {
        System.out.println("Error reading directory" + path);
        e.printStackTrace();
      }
    }
    fileQueue.add(SAFE_WORD);
  }

}
