package io.pilju.spi.entity;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomUserModel {

    private String companyCd;
    private String userId;
    private String userNm;
    private String userPw;
    private String deptCd;
    private String emailAddress;

//    private String username;
//    private String email;
//    private String password;
//    private String firstName;
//    private String lastName;
//    private Date birthDate;
}
