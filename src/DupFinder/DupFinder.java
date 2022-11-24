package DupFinder;

import DupFinder.interfaces.UICallback;
import DupFinder.ui.SearchScreen;

public class DupFinder implements UICallback {

  public static void main(String[] args) {
    new DupFinder();
  }

  public DupFinder() {
    new SearchScreen(this);
  }

  @Override
  public void startSearch(String startPath) {
    System.out.println("Start search at " + startPath);
  }

}
