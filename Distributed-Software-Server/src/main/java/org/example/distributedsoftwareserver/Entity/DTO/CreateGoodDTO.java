package org.example.distributedsoftwareserver.Entity.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGoodDTO {
    private String goodName;
    private String goodDescription;
    private Double goodPrice;
    private Integer goodInventory;
}
