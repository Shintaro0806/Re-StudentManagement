package raisetech.Re.Student.Management.data;

import jakarta.validation.constraints.NotNull;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CourseStatus {

  @NotNull
  private int statusId;

  @NotNull
  private String status;

  @NotNull
  private int courseId;

  @NotNull
  private int statusKeyId;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CourseStatus that = (CourseStatus) o;
    return Objects.equals(statusId, that.statusId) &&
        Objects.equals(status, that.status) &&
        Objects.equals(courseId, that.courseId) &&
        Objects.equals(statusKeyId, that.statusKeyId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(statusId, status, courseId, statusKeyId);
  }
}
