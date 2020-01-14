package uk.thetasinner.filehandlingtcb;

import com.google.common.collect.ImmutableMap;
import uk.thetasinner.filehandlingtcb.tcb.SubjectId;

import java.util.Map;

public class AccessLevelRepository {
  private static final Map<String, Integer> subjectAccessLevelMap = ImmutableMap.of(
          "A000", 0,
          "B001", 1,
          "200D", 2,
          "300C", 3
  );

  public static int getAccessLevelForSubject(SubjectId subjectId) throws IllegalAccessException {
    if (!subjectAccessLevelMap.containsKey(subjectId.getKey())) {
      throw new IllegalAccessException("Subject is not known");
    }

    return subjectAccessLevelMap.get(subjectId.getKey());
  }
}
