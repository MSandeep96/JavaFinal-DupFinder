package DupFinder.workers;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;

import DupFinder.utils.HexUtil;

public class Worker implements Runnable {

  String filename;

  Worker(String filename) {
    this.filename = filename;
  }

  // calculates the md5 hash of the file in a buffered manner
  @Override
  public void run() {
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      try (InputStream is = Files.newInputStream(Paths.get(filename));
          DigestInputStream dis = new DigestInputStream(is, md)) {
        byte[] buffer = new byte[1024 * 100];
        while (dis.read(buffer) != -1) {
          // do nothing
        }
      }
      byte[] digest = md.digest();
      String hash = HexUtil.getHex(digest);
      System.out.println(hash);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
