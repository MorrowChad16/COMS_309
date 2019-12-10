package pickmeup.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PasswordChangeForm {
    
    private String oldPassword;
    private String newPassword;
    private Integer userId;
}