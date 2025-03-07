package raisetech.Re.Student.Management.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.Re.Student.Management.controller.StudentConverter;
import raisetech.Re.Student.Management.data.CourseStatus;
import raisetech.Re.Student.Management.data.Student;
import raisetech.Re.Student.Management.data.StudentCourse;
import raisetech.Re.Student.Management.domain.StudentDetail;
import raisetech.Re.Student.Management.repository.StudentRepository;

/**
 * 受講生情報を取り扱うサービスです。受講生の検索や登録・更新処理を行います。
 */
@Service
public class StudentService {

  private StudentRepository repository;
  private StudentConverter converter;

  @Autowired
  public StudentService(StudentRepository repository, StudentConverter converter) {
    this.repository = repository;
    this.converter = converter;
  }

  /**
   * 受講生詳細の一覧検索です。全件検索を行うので、条件指定は行いません。
   *
   * @return 受講生詳細一覧（全件）
   */
  public List<StudentDetail> searchStudentList() {
    List<Student> studentList = repository.search();
    List<StudentCourse> studentCourseList = repository.searchStudentCourseList();
    return converter.convertStudentDetails(studentList, studentCourseList);
  }

  /**
   * 受講生詳細検索です。IDに紐づく受講生情報を取得したあと、その受講生に紐づく受講生コース情報を取得して設定します。
   *
   * @param id 受講生ID
   * @return 受講生詳細
   */
  public StudentDetail searchStudent(String id) {
    Student student = repository.searchStudent(id);
    List<StudentCourse> studentsCourse = repository.searchStudentCourse(student.getId());
    return new StudentDetail(student, studentsCourse);
  }

  /**
   * コースの申込状況をコースIDを用いて取得します。
   *
   * @param courseId
   * @return　コース申込状況
   */
  public CourseStatus searchCourseStatus(int courseId) {
    CourseStatus courseStatus = repository.searchCourseStatus(courseId);
    return courseStatus;
  }

  /**
   * 受講生詳細の登録を行います。 受講生と受講生コース情報を個別に登録し、受講生コース情報には受講生情報を紐づける値とコース開始日、コース終了日を設定します。
   *
   * @param studentDetail 受講生詳細
   * @return 登録情報を付与した受講生詳細
   */
  @Transactional
  public StudentDetail registerStudent(StudentDetail studentDetail) {
    Student student = studentDetail.getStudent();

    repository.registerStudent(student);
    studentDetail.getStudentCourseList().forEach(studentsCourse -> {
      initStudentsCourse(studentsCourse, student);

      repository.registerStudentCourse(studentsCourse);
    });
    return studentDetail;
  }

  /**
   * コース申込状況の登録を行います。コースIDを個別に登録し、申込状況にはデフォルトで申込状況IDと申込状況を設定します。
   * @param courseId
   * @return
   */
  @Transactional
  public CourseStatus registerCourseStatus(int courseId) {
    CourseStatus courseStatus = new CourseStatus();
    courseStatus.setStatusId(1);
    courseStatus.setStatus("仮申込");
    courseStatus.setCourseId(courseId);
    repository.registerCourseStatus(courseStatus);

    return courseStatus;
  }

  /**
   * 受講生コース情報を登録する際の初期情報を設定する。
   *
   * @param studentsCourse 受講生コース情報
   * @param student        受講生
   */
  private void initStudentsCourse(StudentCourse studentsCourse, Student student) {
    LocalDateTime now = LocalDateTime.now();

    studentsCourse.setStudentId(student.getId());
    studentsCourse.setCourseStartAt(now);
    studentsCourse.setCourseEndAt(now.plusYears(1));
  }

  /**
   * 受講生詳細の更新を行います。 受講生と受講生コース情報をそれぞれ更新します。
   *
   * @param studentDetail 受講生詳細
   */
  @Transactional
  public void updateStudent(StudentDetail studentDetail) {
    repository.updateStudent(studentDetail.getStudent());
    studentDetail.getStudentCourseList()
        .forEach(studentsCourse -> repository.updateStudentCourse(studentsCourse));
  }

  /**
   * コース申込状況の更新を行います。
   *
   * @param courseStatus　コース申込状況
   */
  @Transactional
  public void updateCourseStatus(CourseStatus courseStatus) {
    repository.updateCourseStatus(courseStatus);
  }

  /**
   * 受講生詳細をid,名前,性別,コース名,コースIDで検索します。また、複数の項目のand条件で検索可能です。
   *
   * @param id
   * @param name
   * @param sex
   * @param courseName
   * @param courseId
   * @return　受講生詳細
   */
  public List<StudentDetail> searchMultiStudentList(String id, String name, String sex, String courseName,String courseId) {
    List<Student> studentList = repository.searchByCriteria(id, name, sex, courseName,courseId);
    List<StudentCourse> studentCourseList = repository.searchStudentCourseList();

    return converter.convertStudentDetails(studentList, studentCourseList);
  }
}
