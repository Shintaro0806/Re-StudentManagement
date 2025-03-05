package raisetech.Re.Student.Management.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CourseStatus {

  @NotNull
  private int statusId;

  @NotBlank
  private String status;

  @NotNull
  private int courseId;

  @NotNull
  private int statusKeyId;
}
