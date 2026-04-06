package org.example.distributedsoftwareserver.Entity.Model;

import lombok.*;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Long orderID;
    private Long userID;
    private Long goodId;
    private Integer orderQuantity;
    private Double orderTotal;
    private Timestamp orderTime;
}
