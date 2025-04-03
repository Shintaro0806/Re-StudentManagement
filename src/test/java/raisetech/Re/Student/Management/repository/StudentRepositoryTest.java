package raisetech.Re.Student.Management.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import raisetech.Re.Student.Management.data.CourseStatus;
import raisetech.Re.Student.Management.data.Student;
import raisetech.Re.Student.Management.data.StudentCourse;
import raisetech.Re.Student.Management.service.Application;

@MybatisTest
@ContextConfiguration(classes = Application.class)
class StudentRepositoryTest {

  @Autowired
  private StudentRepository sut;

  @Test
  void 受講生の全件検索ができること() {
    List<Student> actual = sut.search();

    assertThat(actual.size()).isEqualTo(5);
  }

  @Test
  void 受講生をIDで検索ができること() {
    Student expected = new Student(
        "1", "山田太郎", "ヤマダタロウ", "タロ", "taro@example.com",
        "東京", 25, "男性", "", false);

    Student actual = sut.searchStudent("1");

    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
  }

  @Test
  void 受講生のコース情報の全件検索ができること() {
    List<StudentCourse> actual = sut.searchStudentCourseList();

    assertThat(actual.size()).isEqualTo(10);
  }

  @Test
  void コース申込状況をコースIDで検索できること() {
    CourseStatus actual = sut.searchCourseStatus(1);

    assertThat(actual.getStatus()).isEqualTo("仮申込");
  }

  @Test
  void 受講生の登録が行えること() {
    Student student = new Student(
        "1", "山田太郎", "ヤマダタロウ", "タロ", "taro@example.com",
        "東京", 25, "男性", "", false);

    sut.registerStudent(student);

    List<Student> actual = sut.search();
    assertThat(actual.size()).isEqualTo(6);
  }

  @Test
  void 受講生コース情報の登録が行えること() {
    StudentCourse studentCourse = new StudentCourse(
      "1","2",
      "Spring Bootコース",
        LocalDateTime.now(),
        LocalDateTime.now().plusYears(1));

    sut.registerStudentCourse(studentCourse);

    List<StudentCourse> actual = sut.searchStudentCourseList();
    assertThat(actual.size()).isEqualTo(11);
  }

  @Test
  void コース申込状況の登録が行えること() {
    CourseStatus courseStatus = new CourseStatus(1,"仮申込",100,100);

    sut.registerCourseStatus(courseStatus);

    CourseStatus actual = sut.searchCourseStatus(100);
    assertThat(actual).isEqualTo(courseStatus);
  }

  @Test
  void 受講生の更新が行えること() {
    Student actual = sut.searchStudent("1");
    actual.setRemark("更新しました。");

    sut.updateStudent(actual);

    Student updateactual = sut.searchStudent("1");
    assertThat(updateactual.getRemark()).isEqualTo("更新しました。");
  }

  @Test
  void 受講生コース情報のコース名の更新が行えること() {
    List<StudentCourse> actual = sut.searchStudentCourse("1");
    StudentCourse studentCourse1 = actual.get(0);
    studentCourse1.setCourseName("Javaアドバンスコース");

    sut.updateStudentCourse(studentCourse1);

    List<StudentCourse> updateactual = sut.searchStudentCourse("1");
    StudentCourse updatestudentCourse1 = updateactual.get(0);
    assertThat(updatestudentCourse1.getCourseName()).isEqualTo("Javaアドバンスコース");
  }

  @Test
  void コース申込状況の更新が行えること() {
    CourseStatus actual = sut.searchCourseStatus(1);
    actual.setStatus("更新しました。");

    sut.updateCourseStatus(actual);

    CourseStatus updateActual = sut.searchCourseStatus(1);
    assertThat(updateActual.getStatus()).isEqualTo("更新しました。");
  }
}
