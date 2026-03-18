package org.example.distributedsoftwareserver.Entity.Model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    Long userID;
    String userPhone;
    String userName;
    String password;
    Integer is_Login;
}
