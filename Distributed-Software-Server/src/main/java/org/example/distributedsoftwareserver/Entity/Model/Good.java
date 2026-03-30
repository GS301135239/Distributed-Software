package org.example.distributedsoftwareserver.Entity.Model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Good {
    private Long goodId;
    private String goodName;
    private String goodDescription;
    private Double goodPrice;
    private Integer goodInventory;
}
