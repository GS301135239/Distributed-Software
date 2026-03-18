package org.example.distributedsoftwareserver.Entity.DTO;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"userPhone", "password"})
public class LoginDTO {
    private String userPhone;
    private String password;
}
