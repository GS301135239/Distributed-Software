package org.example.distributedsoftwareserver.Entity.VO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterVO {
    private String userPhone;
    private String userName;
    private Integer is_Login;
}
