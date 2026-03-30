package org.example.distributedsoftwareserver.Entity.VO;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckGoodVO implements Serializable {
    private String goodName;
    private String goodDescription;
    private Double goodPrice;
    private Integer goodInventory;
}
