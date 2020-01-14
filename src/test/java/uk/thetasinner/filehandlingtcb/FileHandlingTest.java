package uk.thetasinner.filehandlingtcb;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.thetasinner.filehandlingtcb.file.FileHandle;
import uk.thetasinner.filehandlingtcb.filehandling.FileHandlingTCB;
import uk.thetasinner.filehandlingtcb.filehandling.IFileHandling;
import uk.thetasinner.filehandlingtcb.tcb.SubjectId;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileHandlingTest {
//  @AfterEach
//  public void tearDown() {
//
//  }

  @Test
  public void securityLevelZeroCannotCreateFile() {
    IFileHandling fileHandling = FileHandlingTCB.getInstance();

    assertThrows(IllegalAccessException.class, () -> {
      fileHandling.createFile(Paths.get("cannot-create-file.txt"), new SubjectId("A000"));
    });
  }

  @ParameterizedTest
  @ValueSource(strings = { "B001", "200D", "300C" })
  public void securityLevelOneAndAboveCanCreateFile(String key) throws IllegalAccessException {
    IFileHandling fileHandling = FileHandlingTCB.getInstance();

    FileHandle fileHandle = fileHandling.createFile(Paths.get("can-create-file-" + key + ".txt"), new SubjectId(key));
    assertNotNull(fileHandle);
    assertTrue(fileHandling.isOpen(fileHandle));

    fileHandling.closeFile(fileHandle);
  }

  @ParameterizedTest
  @ValueSource(strings = { "A000", "B001", "200D", "300C" })
  public void anybodyCanReadAndCloseFile(String key) throws IllegalAccessException {
    IFileHandling fileHandling = FileHandlingTCB.getInstance();

    FileHandle fileHandle = fileHandling.openForRead(Paths.get("~/.ssh/id_rsa_" + key + ".pub"), new SubjectId(key));
    assertNotNull(fileHandle);
    assertTrue(fileHandling.isOpen(fileHandle));

    fileHandling.closeFile(fileHandle);
  }

  @ParameterizedTest
  @ValueSource(strings = { "A000", "B001" })
  public void securityLevelOneAndBelowCannotAppendToFile(String key) {
    IFileHandling fileHandling = FileHandlingTCB.getInstance();

    assertThrows(IllegalAccessException.class, () -> {
      fileHandling.openForAppend(Paths.get("cannot-append-file-" + key + ".txt"), new SubjectId(key));
    });
  }

  @ParameterizedTest
  @ValueSource(strings = { "200D", "300C" })
  public void securityLevelTwoAndAboveCanAppendToFile(String key) throws IllegalAccessException {
    IFileHandling fileHandling = FileHandlingTCB.getInstance();

    FileHandle fileHandle = fileHandling.openForAppend(Paths.get("can-append-file-" + key + ".txt"), new SubjectId(key));
    assertNotNull(fileHandle);
    assertTrue(fileHandling.isOpen(fileHandle));

    fileHandling.closeFile(fileHandle);
  }

  @ParameterizedTest
  @ValueSource(strings = { "A000", "B001", "200D" })
  public void securityLevelTwoAndBelowCannotDeleteFile(String key) {
    IFileHandling fileHandling = FileHandlingTCB.getInstance();

    assertThrows(IllegalAccessException.class, () -> {
      Path path = Paths.get("cannot-delete-file-" + key + ".txt");

      FileHandle fileHandle = fileHandling.openForRead(path, new SubjectId(key));
      fileHandling.deleteFile(fileHandle, new SubjectId(key));
    });
  }

  @Test
  public void securityLevelThreeCanDeleteFile() throws IllegalAccessException {
    IFileHandling fileHandling = FileHandlingTCB.getInstance();

    var key = "300C";

    Path path = Paths.get("can-delete-file-" + key + ".txt");

    FileHandle fileHandle = fileHandling.openForRead(path, new SubjectId(key));
    fileHandling.deleteFile(fileHandle, new SubjectId(key));
  }

  @Test
  public void attemptDeleteWithUnknownSubjectKeyFails() throws IllegalAccessException {
    IFileHandling fileHandling = FileHandlingTCB.getInstance();

    var key = "234908234K";

    Path path = Paths.get("cannot-delete-with-unknown-subject-key-file-" + key + ".txt");

    FileHandle fileHandle = fileHandling.openForRead(path, new SubjectId(key));

    IllegalAccessException accessException = assertThrows(IllegalAccessException.class, () -> {
      fileHandling.deleteFile(fileHandle, new SubjectId(key));
    });

    assertEquals(accessException.getMessage(), "Subject is not known");
  }

  @Test
  public void cannotCreateOpenFile() throws IllegalAccessException {
    var fileHandling = FileHandlingTCB.getInstance();

    Path path = Paths.get("create-open-file.txt");
    SubjectId subjectId = new SubjectId("300C");
    FileHandle fileHandle = fileHandling.openForRead(path, subjectId);
    assertNotNull(fileHandle);

    assertNull(fileHandling.createFile(path, subjectId));
  }

  @Test
  public void cannotReadOpenFile() throws IllegalAccessException {
    var fileHandling = FileHandlingTCB.getInstance();

    Path path = Paths.get("read-open-file.txt");
    SubjectId subjectId = new SubjectId("300C");
    FileHandle fileHandle = fileHandling.openForRead(path, subjectId);
    assertNotNull(fileHandle);

    assertNull(fileHandling.openForRead(path, subjectId));
  }

  @Test
  public void cannotAppendOpenFile() throws IllegalAccessException {
    var fileHandling = FileHandlingTCB.getInstance();

    Path path = Paths.get("append-open-file.txt");
    SubjectId subjectId = new SubjectId("300C");
    FileHandle fileHandle = fileHandling.openForRead(path, subjectId);
    assertNotNull(fileHandle);

    assertNull(fileHandling.openForAppend(path, subjectId));
  }

  @Test
  public void cannotDeleteFileWhichIsNotOpen() throws IllegalAccessException {
    var fileHandling = FileHandlingTCB.getInstance();

    Path path = Paths.get("append-open-file.txt");
    SubjectId subjectId = new SubjectId("300C");

    // Construct without opening.
    FileHandle fileHandle = new FileHandle(path);

    assertFalse(fileHandling.deleteFile(fileHandle, subjectId));
  }

  @ParameterizedTest
  @ValueSource(strings = { "B001", "200D" })
  public void securityLevelOneAndTwoCannotCreateFileInEtc(String key) throws IllegalAccessException {
    IFileHandling fileHandling = FileHandlingTCB.getInstance();

    FileHandle fileHandle = fileHandling.createFile(Paths.get("/etc/cannot-create-here-" + key + ".conf"), new SubjectId(key));
    assertNull(fileHandle);
  }

  @Test
  public void securityLevelThreeCanCreateFileInEtc() throws IllegalAccessException {
    IFileHandling fileHandling = FileHandlingTCB.getInstance();

    var key = "300C";

    FileHandle fileHandle = fileHandling.createFile(Paths.get("/etc/can-create-here-" + key + ".conf"), new SubjectId(key));
    assertNotNull(fileHandle);
    assertTrue(fileHandling.isOpen(fileHandle));
  }

  @ParameterizedTest
  @ValueSource(strings = { "200D" })
  public void securityLevelTwoCannotAppendToFileInCerts(String key) throws IllegalAccessException {
    IFileHandling fileHandling = FileHandlingTCB.getInstance();

    FileHandle fileHandle = fileHandling.openForAppend(Paths.get("/certs/cannot-append-to-" + key + ".conf"), new SubjectId(key));
    assertNull(fileHandle);
  }

  @Test
  public void securityLevelThreeCanAppendToFilesInCerts() throws IllegalAccessException {
    IFileHandling fileHandling = FileHandlingTCB.getInstance();

    var key = "300C";

    FileHandle fileHandle = fileHandling.createFile(Paths.get("/certs/cannot-append-to-" + key + ".conf"), new SubjectId(key));
    assertNotNull(fileHandle);
    assertTrue(fileHandling.isOpen(fileHandle));
  }
}
