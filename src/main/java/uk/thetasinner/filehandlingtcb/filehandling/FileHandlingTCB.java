package uk.thetasinner.filehandlingtcb.filehandling;

import uk.thetasinner.filehandlingtcb.file.FileHandle;
import uk.thetasinner.filehandlingtcb.file.FileOperation;
import uk.thetasinner.filehandlingtcb.tcb.AccessDecisionFacility;
import uk.thetasinner.filehandlingtcb.tcb.SubjectId;

import java.lang.reflect.Proxy;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileHandlingTCB implements IFileHandling {
  private List<FileHandle> openFiles = new ArrayList<>();

  private FileHandlingTCB() {
    // Do not construct outside this class.
  }

  public static IFileHandling getInstance() {
    return (IFileHandling) Proxy.newProxyInstance(
            IFileHandling.class.getClassLoader(),
            new Class[] { IFileHandling.class },
            new FileHandlingProxy(new FileHandlingTCB()));
  }

  @Override
  public FileHandle createFile(Path path, SubjectId subjectId) throws IllegalAccessException {
    if (isOpen(new FileHandle(path))) {
      return null;
    }

    if (AccessDecisionFacility.disallowPerformOperationOnObject(FileOperation.CREATE_FILE, path, subjectId)) {
      return null;
    }

    FileHandle fileHandle = new FileHandle(path);
    openFiles.add(fileHandle);
    return fileHandle;
  }

  @Override
  public FileHandle openForRead(Path path, SubjectId subjectId) throws IllegalAccessException {
    if (isOpen(new FileHandle(path))) {
      return null;
    }

    if (AccessDecisionFacility.disallowPerformOperationOnObject(FileOperation.OPEN_FILE_FOR_READ, path, subjectId)) {
      return null;
    }

    FileHandle fileHandle = new FileHandle(path);
    this.openFiles.add(fileHandle);
    return fileHandle;
  }

  @Override
  public FileHandle openForAppend(Path path, SubjectId subjectId) throws IllegalAccessException {
    if (isOpen(new FileHandle(path))) {
      return null;
    }

    if (AccessDecisionFacility.disallowPerformOperationOnObject(FileOperation.OPEN_FILE_FOR_APPEND, path, subjectId)) {
      return null;
    }

    FileHandle fileHandle = new FileHandle(path);
    this.openFiles.add(fileHandle);
    return fileHandle;
  }

  @Override
  public void closeFile(FileHandle fileHandle) {
    this.openFiles = this.openFiles.stream().filter(fh -> !fh.equals(fileHandle)).collect(Collectors.toList());
  }

  @Override
  public boolean deleteFile(FileHandle fileHandle, SubjectId subjectId) throws IllegalAccessException {
    if (!isOpen(fileHandle)) {
      return false;
    }

    if (AccessDecisionFacility.disallowPerformOperationOnObject(FileOperation.DELETE_FILE, fileHandle.getPath(), subjectId)) {
      return false;
    }

    this.closeFile(fileHandle);
    return true;
  }

  public boolean isOpen(FileHandle fileHandle) {
    return this.openFiles.stream().anyMatch(fh -> fh.getPath().compareTo(fileHandle.getPath()) == 0);
  }
}
