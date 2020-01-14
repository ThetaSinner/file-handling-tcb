package uk.thetasinner.filehandlingtcb.tcb;

import uk.thetasinner.filehandlingtcb.AccessLevelRepository;
import uk.thetasinner.filehandlingtcb.file.FileOperation;

import java.nio.file.Path;
import java.nio.file.Paths;

public class AccessDecisionFacility {
  public static boolean canAccessOperation(int requiredAccessLevel, SubjectId subjectId) throws IllegalAccessException {
    return AccessLevelRepository.getAccessLevelForSubject(subjectId) >= requiredAccessLevel;
  }

  public static boolean disallowPerformOperationOnObject(FileOperation fileOperation, Path path, SubjectId subjectId) throws IllegalAccessException {
    if (FileOperation.CREATE_FILE.equals(fileOperation) && path.startsWith(Paths.get("/etc"))) {
      return !canAccessOperation(3, subjectId);
    }

    if (FileOperation.OPEN_FILE_FOR_APPEND.equals(fileOperation) && path.startsWith(Paths.get("/certs"))) {
      return !canAccessOperation(3, subjectId);
    }

    // permit unless a rule prevents access.
    return false;
  }
}
