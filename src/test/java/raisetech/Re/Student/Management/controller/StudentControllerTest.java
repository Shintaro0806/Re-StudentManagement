package raisetech.Re.Student.Management.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import raisetech.Re.Student.Management.data.Student;
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

  @Test
  void 受講生詳細の一覧検索が実行できて空のリストが返ってくること() throws Exception {
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
    mockMvc.perform(get("/student/{id}",id))
        .andExpect(status().isOk());

    verify(service,times(1)).searchStudent(id);
  }

  @Test
  void 受講生登録をすると詳細情報が返ってくること()  throws Exception{
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
    mockMvc.perform(MockMvcRequestBuilders.post("/registerStudent")
        .contentType(MediaType.APPLICATION_JSON).content(requestBody)).andExpect(status().isOk());

    verify(service,times(1)).registerStudent(any());
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
        .andExpect(status().isOk());

    verify(service, times(1)).updateStudent(any());
  }

  @Test
  public void 例外をスローしてエラー処理が上手くいくこと() throws Exception {
    mockMvc.perform(get("/exception"))
        .andExpect(status().is4xxClientError())
        .andExpect(content().string("このAPIは現在利用できません。古いURLとなっています"));
  }

}
