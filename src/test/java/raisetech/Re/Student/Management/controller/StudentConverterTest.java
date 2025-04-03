package raisetech.Re.Student.Management.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import raisetech.Re.Student.Management.data.Student;
import raisetech.Re.Student.Management.data.StudentCourse;
import raisetech.Re.Student.Management.domain.StudentDetail;

class StudentConverterTest {

  private StudentConverter sut;

  @BeforeEach
  void setUp() {
    sut = new StudentConverter();
  }

  @Test
  void 受講生と受講生コース情報のリストを渡したとき受講生詳細のリストができること() {
    Student student = new Student(
        "1", "山田太郎", "ヤマダタロウ", "タロ", "taro@example.com",
        "東京", 25, "男性", "", false);

    StudentCourse studentCourse = new StudentCourse(
        "1","1","JAVAコース",LocalDateTime.now(),LocalDateTime.now().plusYears(1));

    List<Student> studentList = Arrays.asList(student);
    List<StudentCourse> studentCourseList = Arrays.asList(studentCourse);

    List<StudentDetail> studentDetailList = sut.convertStudentDetails(studentList, studentCourseList);

    assertThat(studentDetailList.get(0).getStudent()).isEqualTo(student);
    assertThat(studentDetailList.get(0).getStudentCourseList()).isEqualTo(studentCourseList);
  }

  @Test
  void 受講生と受講生コース情報のリストを渡したときに紐づかない受講生コース情報は除外されること() {
    Student student = new Student(
        "1", "山田太郎", "ヤマダタロウ", "タロ", "taro@example.com",
        "東京", 25, "男性", "", false);

    StudentCourse studentCourse = new StudentCourse(
        "1","2","JAVAコース",LocalDateTime.now(),LocalDateTime.now().plusYears(1));

    List<Student> studentList = Arrays.asList(student);
    List<StudentCourse> studentCourseList = Arrays.asList(studentCourse);

    List<StudentDetail> studentDetailList = sut.convertStudentDetails(studentList, studentCourseList);

    assertThat(studentDetailList.get(0).getStudent()).isEqualTo(student);
    assertThat(studentDetailList.get(0).getStudentCourseList()).isEmpty();
  }
}