package org.example.distributedsoftwareserver.Entity.VO;

import lombok.*;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckOrderVO {
    private Long orderID;
    private Long userID;
    private Long goodId;
    private Integer orderQuantity;
    private Double orderTotal;
    private Timestamp orderTime;
}
