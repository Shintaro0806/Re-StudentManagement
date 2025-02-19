package raisetech.Re.Student.Management.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
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
    Student actual = sut.searchStudent("1");

    assertThat(actual).isNotNull();
    assertThat(actual.getId()).isEqualTo("1");
    assertThat(actual.getName()).isEqualTo("山田太郎");
    assertThat(actual.getKanaName()).isEqualTo("ヤマダタロウ");
    assertThat(actual.getNickname()).isEqualTo("タロ");
    assertThat(actual.getEmail()).isEqualTo("taro@example.com");
    assertThat(actual.getArea()).isEqualTo("東京");
    assertThat(actual.getAge()).isEqualTo(25);
    assertThat(actual.getSex()).isEqualTo("男性");
  }

  @Test
  void 受講生のコース情報の全件検索ができること() {
    List<StudentCourse> actual = sut.searchStudentCourseList();

    assertThat(actual.size()).isEqualTo(10);
  }

  @Test
  void 受講生のコース情報をIDで検索ができること() {
    List<StudentCourse> actual = sut.searchStudentCourse("1");

    assertThat(actual.size()).isEqualTo(3);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    StudentCourse firstCourse = actual.get(0);
    assertThat(firstCourse.getStudentId()).isEqualTo("1");
    assertThat(firstCourse.getCourseName()).isEqualTo("Javaコース");
    assertThat(firstCourse.getCourseStartAt().format(formatter)).isEqualTo("2023-04-01T09:00:00");
    assertThat(firstCourse.getCourseEndAt().format(formatter)).isEqualTo("2023-07-01T15:00:00");

    StudentCourse secondCourse = actual.get(1);
    assertThat(secondCourse.getStudentId()).isEqualTo("1");
    assertThat(secondCourse.getCourseName()).isEqualTo("AWSコース");
    assertThat(secondCourse.getCourseStartAt().format(formatter)).isEqualTo("2023-05-01T10:00:00");
    assertThat(secondCourse.getCourseEndAt().format(formatter)).isEqualTo("2023-08-01T16:00:00");

    StudentCourse thirdCourse = actual.get(2);
    assertThat(thirdCourse.getStudentId()).isEqualTo("1");
    assertThat(thirdCourse.getCourseName()).isEqualTo("Web制作コース");
    assertThat(thirdCourse.getCourseStartAt().format(formatter)).isEqualTo("2024-01-01T13:00:00");
    assertThat(thirdCourse.getCourseEndAt().format(formatter)).isEqualTo("2024-05-01T19:00:00");
  }

  @Test
  void 受講生の登録が行えること() {
    Student student = new Student();
    student.setName("江波浩二");
    student.setKanaName("エナミコウジ");
    student.setNickname("エナミ");
    student.setEmail("test@example.com");
    student.setArea("奈良県");
    student.setAge(36);
    student.setSex("男性");
    student.setRemark("");
    student.setDeleted(false);

    sut.registerStudent(student);

    List<Student> actual = sut.search();
    assertThat(actual.size()).isEqualTo(6);
  }

  @Test
  void 受講生コース情報の登録が行えること() {
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setStudentId("2");
    studentCourse.setCourseName("Spring Bootコース");
    studentCourse.setCourseStartAt(LocalDateTime.now());
    studentCourse.setCourseEndAt(LocalDateTime.now().plusYears(1));

    sut.registerStudentCourse(studentCourse);

    List<StudentCourse> actual = sut.searchStudentCourseList();
    assertThat(actual.size()).isEqualTo(11);
  }

  @Test
  void 受講生の更新が行えること() {
    Student actual = sut.searchStudent("1");
    actual.setRemark("更新しました。");

    sut.updateStudent(actual);

    actual = sut.searchStudent("1");
    assertThat(actual.getRemark()).isEqualTo("更新しました。");
  }

  @Test
  void 受講生コース情報のコース名の更新が行えること() {
    List<StudentCourse> actual = sut.searchStudentCourse("1");
    StudentCourse studentCourse1 = actual.get(0);
    studentCourse1.setCourseName("Javaアドバンスコース");

    sut.updateStudentCourse(studentCourse1);

    actual = sut.searchStudentCourse("1");
    studentCourse1 = actual.get(0);
    assertThat(studentCourse1.getCourseName()).isEqualTo("Javaアドバンスコース");
  }
}
