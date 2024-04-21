package msyaipulanwar.restful.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateContactRequest {
    @NotBlank
    @JsonIgnore
    private String id;

    @NotBlank
    private String firstName;

    @Size(max = 100)
    private String lastName;

    @Size(max = 100)
    @Pattern(regexp = "^(\\+62\\s?|0)(\\d{3,4}-?){2}\\d{3,4}$")
    private String phone;

    @Size(max = 100)
    @Email
    private String email;
}
