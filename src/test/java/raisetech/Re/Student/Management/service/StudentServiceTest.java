package raisetech.Re.Student.Management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.Re.Student.Management.controller.StudentConverter;
import raisetech.Re.Student.Management.data.CourseStatus;
import raisetech.Re.Student.Management.data.Student;
import raisetech.Re.Student.Management.data.StudentCourse;
import raisetech.Re.Student.Management.domain.StudentDetail;
import raisetech.Re.Student.Management.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

  @Mock
  private StudentRepository repository;

  @Mock
  private StudentConverter converter;

  @InjectMocks
  private StudentService sut;

  @BeforeEach
  void before() {
    sut = new StudentService(repository, converter);
  }

  @Test
  void 受講生詳細の一覧検索_リポジトリとコンバーターの処理が適切に呼び出せていること() {
    List<Student> studentList = new ArrayList<>();
    List<StudentCourse> studentCourseList = new ArrayList<>();
    List<StudentDetail> expectedDetails = new ArrayList<>();

    when(repository.search()).thenReturn(studentList);
    when(repository.searchStudentCourseList()).thenReturn(studentCourseList);
    when(converter.convertStudentDetails(studentList, studentCourseList)).thenReturn(expectedDetails);

    List<StudentDetail> actualDetails = sut.searchStudentList();

    verify(repository,times(1)).search();
    verify(repository,times(1)).searchStudentCourseList();
    verify(converter,times(1)).convertStudentDetails(studentList, studentCourseList);

    assertThat(actualDetails).isEqualTo(expectedDetails);
  }

  @Test
  void 受講生詳細検索の処理が適切に呼び出せていること() {
    String id = "123";
    Student student = new Student(id, "江波公史", "エナミコウジ", "エナミ",
        "test@example.com", "奈良県", 0, "男性", "", false);

    List<StudentCourse> courseList = new ArrayList<>();

    when(repository.searchStudent(id)).thenReturn(student);
    when(repository.searchStudentCourse(id)).thenReturn(courseList);

    StudentDetail expected = new StudentDetail(student, courseList);

    StudentDetail actual = sut.searchStudent(id);

    verify(repository, times(1)).searchStudent(id);
    verify(repository, times(1)).searchStudentCourse(id);
    Assertions.assertEquals(expected,actual);
    Assertions.assertTrue(actual.getStudentCourseList().isEmpty());
  }

  @Test
  void 受講生コース申込状況検索の処理が適切に呼び出せていること() {
    int courseId = 123;
    CourseStatus courseStatus = new CourseStatus(123, "仮申込", courseId,1);

    when(repository.searchCourseStatus(courseId)).thenReturn(courseStatus);

    CourseStatus actual = sut.searchCourseStatus(courseId);

    verify(repository,times(1)).searchCourseStatus(courseId);
    Assertions.assertEquals(actual,courseStatus);
  }

  @Test
  void 受講生詳細をいくつかの条件での検索処理が適切に呼び出せていること() {
    String id = "1";
    String name = "山田太郎";
    String sex = "男性";
    String courseName = "Javaコース";
    String courseId =  "1";

    Student student = new Student("1", "山田太郎", "ヤマダタロウ", "タロ", "taro@example.com", "東京", 25, "男性", "", false);
    List<Student> studentList = List.of(student);

    StudentCourse studentCourse = new StudentCourse("1","1", "Javaコース", LocalDateTime.now(),LocalDateTime.now().plusYears(1));
    List<StudentCourse> studentCourseList = List.of(studentCourse);

    StudentDetail studentDetail = new StudentDetail(student,studentCourseList);
    List<StudentDetail> expected = List.of(studentDetail);

    when(repository.searchByCriteria(id, name, sex, courseName,courseId)).thenReturn(studentList);
    when(repository.searchStudentCourseList()).thenReturn(studentCourseList);
    when(converter.convertStudentDetails(studentList, studentCourseList)).thenReturn(expected);

    List<StudentDetail> actual = sut.searchMultiStudentList(id, name, sex, courseName,courseId);

    assertThat(actual).isEqualTo(expected);

    verify(repository,times(1)).searchByCriteria(id,name,sex,courseName,courseId);
  }

  @Test
  void 受講生詳細の登録処理がリポジトリーから適切に呼び出せていること() {
    String id = "123";
    Student student = new Student(id,"江波公史", "エナミコウジ", "エナミ",
        "test@example.com", "奈良県", 0, "男性", "", false);

    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    List<StudentCourse> studentCourseList = new ArrayList<>();
    studentDetail.setStudentCourseList(studentCourseList);

    sut.registerStudent(studentDetail);

    verify(repository, times(1)).registerStudent(student);
    verify(repository, times(0)).registerStudentCourse(any(StudentCourse.class));
  }

  @Test
  void コース申込状況の登録処理をリポジトリーから適切に呼び出せていること() {
    int testCourseId = 100;
    CourseStatus courseStatus = new CourseStatus();
    courseStatus.setCourseId(testCourseId);

    sut.registerCourseStatus(testCourseId);

    verify(repository, times(1)).registerCourseStatus(any(CourseStatus.class));
  }

  @Test
  void 受講生詳細の更新処理をリポジトリーから適切に呼び出せていること() {
    String id = "123";
    Student student = new Student(id,"江波公史", "エナミコウジ", "エナミ",
        "test@example.com", "奈良県", 0, "男性", "", false);

    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    List<StudentCourse> studentCourseList = new ArrayList<>();
    studentDetail.setStudentCourseList(studentCourseList);

    sut.updateStudent(studentDetail);

    verify(repository, times(1)).updateStudent(student);
    verify(repository, times(0)).updateStudentCourse(any(StudentCourse.class));
   }

  @Test
  void コース申込状況更新処理をリポジトリーから呼び出せていること() {
    int testCourseId = 100;
    int testId = 100;
    CourseStatus courseStatus = new CourseStatus(1,"仮申込",testCourseId,testId);
    courseStatus.setStatusId(2);
    courseStatus.setStatus("本申込");

    sut.updateCourseStatus(courseStatus);

    verify(repository, times(1)).updateCourseStatus(courseStatus);
  }
  }
