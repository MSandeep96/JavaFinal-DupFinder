package DupFinder.utils;

import java.util.LinkedList;
import java.util.Queue;

public class SafeQueue {

  Queue<String> queue;

  public SafeQueue() {
    queue = new LinkedList<String>();
  }

  public void add(String filePath) {
    queue.add(filePath);
  }

  public synchronized String get() {
    return queue.poll();
  }
}
