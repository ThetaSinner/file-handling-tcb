package uk.thetasinner.filehandlingtcb.filehandling;

import uk.thetasinner.filehandlingtcb.file.FileHandle;
import uk.thetasinner.filehandlingtcb.tcb.RestrictedOperation;
import uk.thetasinner.filehandlingtcb.tcb.SubjectId;

import java.nio.file.Path;

public interface IFileHandling {
  @RestrictedOperation(requiredAccessLevel = 1)
  FileHandle createFile(Path path, SubjectId subjectId) throws IllegalAccessException;

  FileHandle openForRead(Path path, SubjectId subjectId) throws IllegalAccessException;

  @RestrictedOperation(requiredAccessLevel = 2)
  FileHandle openForAppend(Path path, SubjectId subjectId) throws IllegalAccessException;

  void closeFile(FileHandle fileHandle);

  @RestrictedOperation(requiredAccessLevel = 3)
  boolean deleteFile(FileHandle fileHandle, SubjectId subjectId) throws IllegalAccessException;

  boolean isOpen(FileHandle fileHandle);
}
