package org.example.distributedsoftwareserver.Entity.DTO;

import lombok.*;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderDTO {
    private Long userID;
    private Long goodId;
    private Integer orderQuantity;
}
