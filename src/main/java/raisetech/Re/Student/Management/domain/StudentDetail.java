package raisetech.Re.Student.Management.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import raisetech.Re.Student.Management.data.Student;
import raisetech.Re.Student.Management.data.StudentCourse;

@Schema(description = "受講生詳細")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentDetail {

  @Valid
  private Student student;

  @Valid
  private List<StudentCourse> studentCourseList;

  public StudentDetail(Student student) {
    this.student = student;
    this.studentCourseList = new ArrayList<>();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    StudentDetail that = (StudentDetail) o;
    return Objects.equals(student, that.student) &&
        Objects.equals(studentCourseList, that.studentCourseList);
  }

  @Override
  public int hashCode() {
    return Objects.hash(student, studentCourseList);
  }
}
