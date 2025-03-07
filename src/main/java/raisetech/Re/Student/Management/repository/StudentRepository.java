package raisetech.Re.Student.Management.repository;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import raisetech.Re.Student.Management.data.CourseStatus;
import raisetech.Re.Student.Management.data.Student;
import raisetech.Re.Student.Management.data.StudentCourse;

/**
 * 受講生テーブルと受講生コース情報テーブルと紐づくRepositoryです。
 */
@Mapper
public interface StudentRepository {

  /**
   * 受講生の全件検索を行います。
   *
   * @return 受講生一覧（全件）
   */
  @Select("SELECT * FROM students")
  List<Student> search();

  /**
   * 受講生の検索を行います。
   *
   * @param id 受講生ID
   * @return 受講生
   */
  @Select("SELECT * FROM students WHERE id = #{id}")
  Student searchStudent(String id);

  /**
   * 受講生のコース情報の全件検索を行います。
   *
   * @return 受講生のコース情報（全件）
   */
  @Select("SELECT * FROM students_courses")
  List<StudentCourse> searchStudentCourseList();

  /**
   * 受講生IDに紐づく受講生コース情報を検索します。
   *
   * @param studentId 受講生ID
   * @return 受講生IDに紐づく受講生コース情報
   */
  @Select("SELECT * FROM students_courses WHERE id = #{studentId}")
  List<StudentCourse> searchStudentCourse(String studentId);

  /**
   * コースの申し込み状況を、コースIDを用いて検索します。
   *
   * @param courseId
   * @return　コース申込状況
   */
  @Select("SELECT * FROM course_status WHERE course_id  = #{courseId}")
  CourseStatus searchCourseStatus(int courseId);

  /**
   * 受講生を新規登録します。 IDに関しては自動採番を行う。
   *
   * @param student 受講生
   */
  @Insert(
      "INSERT INTO students(name, kana_name, nickname, email, area, age, sex, remark, is_deleted) "
          + "VALUES(#{name}, #{kanaName}, #{nickname}, #{email}, #{area}, #{age}, #{sex}, #{remark}, false)")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void registerStudent(Student student);

  /**
   * 受講生コース情報を新規登録します。IDに関しては自動採番を行う。
   *
   * @param studentCourse 受講生コース情報
   */
  @Insert("INSERT INTO students_courses(student_id, course_name, course_start_at, course_end_at) "
      + "VALUES(#{studentId}, #{courseName}, #{courseStartAt}, #{courseEndAt})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void registerStudentCourse(StudentCourse studentCourse);

  /**
   * コース申込状況を新規登録します。status_key_idに関しては自動採番を行う。
   *
   * @param courseStatus コース申込状況
   */

  @Insert("INSERT INTO course_status(status_id, status, course_id) "
      + "VALUES(1, '仮申込', #{courseId})")
  @Options(useGeneratedKeys = true, keyProperty = "statusKeyId")
  void registerCourseStatus(CourseStatus courseStatus);

  /**
   * 受講生を更新します。
   *
   * @param student 受講生
   */
  @Update("UPDATE students SET name = #{name}, kana_name = #{kanaName}, nickname = #{nickname}, "
      + "email = #{email}, area = #{area}, age = #{age}, sex = #{sex}, remark = #{remark}, is_deleted = #{isDeleted} WHERE id = #{id}")
  void updateStudent(Student student);

  /**
   * 受講生コース情報のコース名を更新します。
   *
   * @param studentCourse 受講生コース情報
   */
  @Update("UPDATE students_courses SET course_name =#{courseName} WHERE id= #{id}")
  void updateStudentCourse(StudentCourse studentCourse);

  /**
   * コース申込状況を更新します。
   *
   * @param courseStatus　コース申込状況
   */
  @Update("UPDATE course_status SET status_id =#{statusId}, status=#{status} WHERE course_id= #{courseId}")
  void updateCourseStatus(CourseStatus courseStatus);

  /**
   * 受講生情報を複数の条件で検索します。
   *
   * @param id
   * @param name
   * @param sex
   * @param courseName
   * @param courseId
   * @return　受講生詳細
   */
  @Select("<script>" +
      "SELECT DISTINCT s.* " +
      "FROM students s " +
      "JOIN students_courses sc ON s.id = sc.student_id " +
      "WHERE 1=1 " +
      "<if test='id != null and id != \"\"'>AND s.id = #{id} </if>" +
      "<if test='name != null and name != \"\"'>AND s.name = #{name} </if>" +
      "<if test='sex != null and sex != \"\"'>AND s.sex = #{sex} </if>" +
      "<if test='courseName != null and courseName != \"\"'>AND sc.course_name = #{courseName} </if>"
      +
      "<if test='courseId != null and courseId != \"\"'>AND sc.course_id = #{courseId} </if>" +
      "</script>")
  List<Student> searchByCriteria(@Param("id") String id,
      @Param("name") String name,
      @Param("sex") String sex,
      @Param("courseName") String courseName,
      @Param("courseId") String courseId);
}
