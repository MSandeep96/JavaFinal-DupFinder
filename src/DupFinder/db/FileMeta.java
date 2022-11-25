package DupFinder.db;

import java.sql.Date;

public class FileMeta {
  public String filename;
  public String md5;
  public long lastModified;

  public FileMeta(String filename, String md5, long lastModified) {
    this.filename = filename;
    this.md5 = md5;
    this.lastModified = lastModified;
  }

  public FileMeta() {
  };
}
