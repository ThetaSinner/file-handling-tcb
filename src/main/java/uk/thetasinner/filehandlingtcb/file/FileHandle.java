package uk.thetasinner.filehandlingtcb.file;

import java.nio.file.Path;

public class FileHandle {
  private final Path path;

  public FileHandle(Path path) {
    this.path = path;
  }

  public Path getPath() {
    return path;
  }
}
