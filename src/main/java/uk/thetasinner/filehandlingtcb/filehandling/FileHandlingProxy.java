package uk.thetasinner.filehandlingtcb.filehandling;

import uk.thetasinner.filehandlingtcb.tcb.AccessDecisionFacility;
import uk.thetasinner.filehandlingtcb.tcb.RestrictedOperation;
import uk.thetasinner.filehandlingtcb.tcb.SubjectId;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class FileHandlingProxy implements InvocationHandler {
  private FileHandlingTCB fileHandlingTCB;

  public FileHandlingProxy(FileHandlingTCB fileHandlingTCB) {
    this.fileHandlingTCB = fileHandlingTCB;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    RestrictedOperation restrictedOperation = method.getAnnotation(RestrictedOperation.class);
    if (restrictedOperation == null) {
      return method.invoke(fileHandlingTCB, args);
    }

    var subjectId = findSubjectId(method, args);
    if (!AccessDecisionFacility.canAccessOperation(restrictedOperation.requiredAccessLevel(), subjectId)) {
      throw new IllegalAccessException("Subject [" + subjectId.getKey() + "] is not allowed to access operation: " + method.getName());
    }

    return method.invoke(fileHandlingTCB, args);
  }

  private SubjectId findSubjectId(Method method, Object[] args) throws IllegalAccessException {
    var index = 0;
    for (Parameter parameter : method.getParameters()) {
      if (SubjectId.class.equals(parameter.getType())) {
        break;
      }
      index++;
    }

    if (index == method.getParameterCount()) {
      throw new IllegalAccessException("Expected to find SubjectId parameter but none was passed while checking access to method: " + method.getName());
    }

    return (SubjectId) args[index];
  }
}
