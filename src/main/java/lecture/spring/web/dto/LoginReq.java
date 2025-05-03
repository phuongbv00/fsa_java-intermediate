package lecture.spring.web.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginReq {
    private String email;
    private String password;
}
