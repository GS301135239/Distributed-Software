package org.example.distributedsoftwareserver.Entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RegisterDTO {
    private Long userID;
    private String userPhone;
    private String userName;
    private String password;
}
