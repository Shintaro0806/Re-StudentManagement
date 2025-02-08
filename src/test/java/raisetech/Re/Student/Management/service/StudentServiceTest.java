package raisetech.Re.Student.Management.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.Re.Student.Management.controller.StudentConverter;
import raisetech.Re.Student.Management.data.Student;
import raisetech.Re.Student.Management.data.StudentCourse;
import raisetech.Re.Student.Management.domain.StudentDetail;
import raisetech.Re.Student.Management.repository.StudentRepository;
import raisetech.Re.Student.Management.service.StudentService;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

  @Mock
  private StudentRepository repository;

  @Mock
  private StudentConverter converter;

  private StudentService sut;

  @BeforeEach
  void before() {
    sut = new StudentService(repository, converter);
  }

  @Test
  void 受講生詳細の一覧検索_リポジトリとコンバーターの処理が適切に呼び出せていること() {

    List<Student> studentList = new ArrayList<>();
    List<StudentCourse> studentCourseList = new ArrayList<>();

    when(repository.search()).thenReturn(studentList);
    when(repository.searchStudentCourseList()).thenReturn(studentCourseList);

    sut.searchStudentList();

    verify(repository,times(1)).search();
    verify(repository,times(1)).searchStudentCourseList();
    verify(converter,times(1)).convertStudentDetails(studentList, studentCourseList);
  }

  @Test
  void 受講生詳細検索の処理が適切に呼び出せていること() {

    String id = "123";
    Student student = new Student();
    student.setId(id);
    when(repository.searchStudent(id)).thenReturn(student);
    when(repository.searchStudentCourse(id)).thenReturn(new ArrayList<>());

    StudentDetail result = new StudentDetail(student, new ArrayList<>());

    StudentDetail actual = sut.searchStudent(id);

    verify(repository,times(1)).searchStudent(id);
    verify(repository,times(1)).searchStudentCourse(id);
    Assertions.assertEquals(result.getStudent().getId(), actual.getStudent().getId());
  }

  @Test
  void 受講生詳細の登録処理がリポジトリーから適切に呼び出せていること() {

    Student student = new Student();
    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    List<StudentCourse> studentCourseList = new ArrayList<>();
    studentDetail.setStudentCourseList(studentCourseList);

    sut.registerStudent(studentDetail);

    verify(repository, times(1)).registerStudent(student);
    verify(repository, times(0)).registerStudentCourse(any(StudentCourse.class));
  }

  @Test
  void 受講生詳細の更新処理をリポジトリーから適切に呼び出せていること() {

    Student student = new Student();
    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    List<StudentCourse> studentCourseList = new ArrayList<>();
    studentDetail.setStudentCourseList(studentCourseList);

    sut.updateStudent(studentDetail);

    verify(repository, times(1)).updateStudent(student);
    verify(repository, times(0)).updateStudentCourse(any(StudentCourse.class));
  }

  }







