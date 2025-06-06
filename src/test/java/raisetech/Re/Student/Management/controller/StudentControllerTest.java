package raisetech.Re.Student.Management.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import raisetech.Re.Student.Management.data.CourseStatus;
import raisetech.Re.Student.Management.data.Student;
import raisetech.Re.Student.Management.domain.StudentDetail;
import raisetech.Re.Student.Management.service.Application;
import raisetech.Re.Student.Management.service.StudentService;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class StudentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private StudentService service;

  private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  private Student student;
  private StudentDetail studentDetail;

  @Test
  void 受講生詳細の一覧検索が実行できて空のリストが返ってくること() throws Exception {
    when(service.searchStudentList()).thenReturn(Collections.emptyList());

    mockMvc.perform(get("/studentList"))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));

    verify(service,times(1)).searchStudentList();
  }

  @Test
  void 受講生詳細の受講生で適切な値を入力したときに入力チェックに異常が発生しないこと() {
    Student student = new Student("1", "江波公史", "エナミコウジ", "エナミ",
        "test@example.com", "奈良県", 0, "男性", "", false);

    Set<ConstraintViolation<Student>> violations = validator.validate(student);

    assertThat(violations.size()).isEqualTo(0);
  }

  @Test
  void 受講生詳細の受講生でIDに数字以外を用いたときに入力チェックにかかること() {
    Student student = new Student("テストです。", "江波公史", "エナミコウジ", "エナミ",
        "test@example.com", "奈良県", 0, "男性", "", false);

    Set<ConstraintViolation<Student>> violations = validator.validate(student);

    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations).extracting("message")
        .containsOnly("数字のみ入力するようにしてください。");
  }

  @Test
  void IDに紐づく任意の受講生詳細の情報を検索して空のリストが返ってくること() throws Exception{
    String id = "123";
    when(service.searchStudent(any(String.class))).thenReturn(null);

    mockMvc.perform(get("/student/{id}",id))
        .andExpect(status().isOk());

    verify(service,times(1)).searchStudent(id);
  }

  @Test
  void コースIDに紐づく任意のコースの申込状況を取得できること() throws Exception{
    int courseId = 123;
    CourseStatus courseStatus = new CourseStatus(4,"受講終了",courseId,1);

    when(service.searchCourseStatus(courseId)).thenReturn(courseStatus);

    mockMvc.perform(get("/coursestatus/{courseId}",courseId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.courseId").value(courseId))
        .andExpect(jsonPath("$.status").value("受講終了"));

    verify(service,times(1)).searchCourseStatus(courseId);
  }

  @Test
  void 受講生詳細を複数の条件で検索できること() throws Exception{
    String id = "1";
    String name = "山田太郎";
    String sex = "男性";
    String courseName = "Javaコース";
    String courseId = "1";
    when(service.searchMultiStudentList(id,name,sex,courseName,courseId)).thenReturn(null);

    mockMvc.perform(get("/students")
            .param("id", id)
            .param("name", name)
            .param("sex", sex)
            .param("courseName", courseName)
            .param("courseId", courseId))
            .andExpect(status().isOk());

    verify(service,times(1)).searchMultiStudentList(id,name,sex,courseName,courseId);
  }

  @Test
  void 受講生登録をすると詳細情報が返ってくること()  throws Exception{
    StudentDetail studentDetail = new StudentDetail();
    Student student = new Student();
    student.setName("大野 林太郎");
    studentDetail.setStudent(student);
    String requestBody = """
        {
            "student": {
                "name": "大野 林太郎",
                "kanaName": "オオノ リンタロウ",
                "nickname": "たろ",
                "email": "taro@example.com",
                "area": "関東地方",
                "age": 20,
                "sex": "男",
                "remark": "更新テスト",
                "isDeleted": false
            },
            "studentCourseList": [
                {
                    "courseName": "JAVAコース"
                }
            ]
        }
        """;

    when(service.registerStudent(any())).thenReturn(studentDetail);

    mockMvc.perform(MockMvcRequestBuilders.post("/registerStudent")
        .contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.student.name").value("大野 林太郎"));

    verify(service,times(1)).registerStudent(any());
  }

  @Test
  void コース申込状況を登録するとコース申込状況が返ってくること() throws Exception{
    CourseStatus courseStatus = new CourseStatus();
    courseStatus.setCourseId(100);
    courseStatus.setStatus("仮申込");

    String requestBody = """
        {
          "courseId": 100
        }
        """;

    when(service.registerCourseStatus(anyInt())).thenReturn(courseStatus);

    mockMvc.perform(MockMvcRequestBuilders.post("/registerCourseStatus")
            .contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.courseId").value(100))
        .andExpect(jsonPath("$.status").value("仮申込"));

    verify(service, times(1)).registerCourseStatus(anyInt());
  }

  @Test
  void 受講生情報を更新すると成功メッセージが返ること() throws Exception {
    String requestBody = """
        {
            "student": {
                "name": "大野 林太郎",
                "kanaName": "オオノ リンタロウ",
                "nickname": "たろ",
                "email": "taro@example.com",
                "area": "関東地方",
                "age": 20,
                "sex": "男",
                "remark": "更新テスト",
                "isDeleted": false
            },
            "studentCourseList": [
                {
                    "courseName": "JAVAコース",
                    "courseStartAt": "2024-01-01T10:00:00",
                    "courseEndAt": "2024-06-01T10:00:00"
                }
            ]
        }
        """;

    mockMvc.perform(MockMvcRequestBuilders.put("/updateStudent")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(content().string("更新処理が成功しました。"));

    verify(service, times(1)).updateStudent(any());
  }

  @Test
  void コース申込状況を更新すると成功メッセージが返ること() throws Exception {
    String requestBody = """
        {
            "courseId": 2,
            "statusId": 4,
            "status": "受講終了",
            "statusKeyId": 2
        }
        """;

    mockMvc.perform(MockMvcRequestBuilders.put("/updateCourseStatus")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(content().string("更新処理が成功しました。"));

    verify(service, times(1)).updateCourseStatus(any());
  }

  @Test
  public void 例外をスローしてエラー処理が上手くいくこと() throws Exception {
    when(service.searchStudent(any())).thenThrow(new RuntimeException("このAPIは現在利用できません。古いURLとなっています"));

    mockMvc.perform(get("/exception"))
        .andExpect(status().is4xxClientError())
        .andExpect(content().string("このAPIは現在利用できません。古いURLとなっています"));
  }
}
