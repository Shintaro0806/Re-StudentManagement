package raisetech.Re.Student.Management.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import raisetech.Re.Student.Management.data.CourseStatus;
import raisetech.Re.Student.Management.domain.StudentDetail;
import raisetech.Re.Student.Management.service.StudentService;

/**
 * 受講生の検索や登録、更新などを行うREST APIとして実行されるControllerです。
 */
@Validated
@RestController
public class StudentController {

  private StudentService service;

  @Autowired
  public StudentController(StudentService service) {
    this.service = service;
  }

  /**
   * 受講生詳細の一覧検索です。全件検索を行うので、条件指定は行いません。
   *
   * @return 受講生詳細一覧（全件）
   */
  @Operation(summary = "一覧検索", description = "受講生の一覧を検索します。")
  @GetMapping("/studentList")
  public List<StudentDetail> getStudentList() {
    return service.searchStudentList();
  }

  /**
   * 受講生詳細の検索です。IDに紐づく任意の受講生の情報を取得します。
   *
   * @param id 受講生ID
   * @return 受講生
   */
  @Operation(summary = "受講生検索", description = "受講生IDで特定の受講生情報を取得します。")
  @GetMapping("/student/{id}")
  public StudentDetail getStudent(
      @PathVariable @NotBlank @Pattern(regexp = "^\\d+$") String id) {
    return service.searchStudent(id);
  }

  /**
   *  コースの申込状況をコースIDを用いて取得します。
   *
   * @param courseId
   * @return　コース申込状況
   */
  @Operation(summary = "コース申込状況", description = "コースIDでコース申込状況を取得します。")
  @GetMapping("/coursestatus/{courseId}")
  public CourseStatus courseStatus(@PathVariable int courseId){
    return service.searchCourseStatus(courseId);
  }

  /**
   * 受講生詳細をid,名前,性別,コース名で検索します。また、複数の項目のand条件で検索可能です。
   *
   * @param id
   * @param name
   * @param sex
   * @param courseName
   * @return　受講生詳細
   */
  @GetMapping("/students")
  public List<StudentDetail> getStudents(
      @RequestParam(required = false) String id,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String sex,
      @RequestParam(required = false) String courseName,
      @RequestParam(required = false) String courseId) {
    return service.searchMultiStudentList(id, name, sex, courseName,courseId);
  }

  /**
   * 受講生詳細の登録を行います。
   *
   * @param studentDetail 受講生詳細
   * @return 実行結果
   */
  @Operation(summary = "受講生登録", description = "受講生を登録します。")
  @PostMapping("/registerStudent")
  public ResponseEntity<StudentDetail> registerStudent(
      @RequestBody @Valid StudentDetail studentDetail) {
    StudentDetail responseStudentDetail = service.registerStudent(studentDetail);
    return ResponseEntity.ok(responseStudentDetail);
  }

  /**
   * 受講生詳細の更新を行います。 キャンセルフラグの更新もここで行います（論理削除）
   *
   * @param studentDetail 受講生詳細
   * @return 実行結果
   */
  @Operation(summary = "受講生情報更新", description = "既存の受講生情報を更新します。キャンセルフラグも含めた更新を行います（論理削除）。")
  @PutMapping("/updateStudent")
  public ResponseEntity<String> updateStudent(@RequestBody @Valid StudentDetail studentDetail) {
    service.updateStudent(studentDetail);
    return ResponseEntity.ok("更新処理が成功しました。");
  }

  @Operation(summary = "エラー発生のテスト", description = "例外をスローしてエラー処理をテストするエンドポイントです。")
  @GetMapping("/exception")
  public ResponseEntity<String> throwException() throws NotFoundException {
    throw new NotFoundException("このAPIは現在利用できません。古いURLとなっています");
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
    return ResponseEntity.badRequest().body(ex.getMessage());
  }
}
