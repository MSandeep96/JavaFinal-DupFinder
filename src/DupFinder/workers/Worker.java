package DupFinder.workers;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.DigestInputStream;
import java.security.MessageDigest;

import DupFinder.db.DbExecutor;
import DupFinder.db.FileMeta;
import DupFinder.interfaces.FileDataCallback;
import DupFinder.utils.HexUtil;

public class Worker implements Runnable {

  String filename;
  FileDataCallback fileDataCallback;

  Worker(String filename, FileDataCallback fileDataCallback) {
    this.filename = filename;
    this.fileDataCallback = fileDataCallback;
  }

  // calculates the md5 hash of the file in a buffered manner

  @Override
  public void run() {
    try {
      DbExecutor dbExecutor = DbExecutor.getInstance();
      FileMeta meta = dbExecutor.readFileMeta(filename);
      Path filePath = Paths.get(filename);
      BasicFileAttributes attrbs = Files.readAttributes(filePath, BasicFileAttributes.class);
      if (meta != null) {
        if (meta.lastModified == attrbs.lastModifiedTime().toMillis()) {
          fileDataCallback.onFileData(filename, meta.md5);
          return;
        }
      }
      MessageDigest md = MessageDigest.getInstance("MD5");
      try (InputStream is = Files.newInputStream(Paths.get(filename));
          DigestInputStream dis = new DigestInputStream(is, md)) {
        byte[] buffer = new byte[1024 * 1000 * 10]; // 10 MB per thread = 150 MB max
        while (dis.read(buffer) != -1) {
          // do nothing
        }
      }
      byte[] digest = md.digest();
      String hash = HexUtil.getHex(digest);
      fileDataCallback.onFileData(filename, hash);
      meta = new FileMeta(filename, hash, attrbs.lastModifiedTime().toMillis());
      dbExecutor.insertFileMeta(meta);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
