package com.elsys.machine.Services.Utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class PrescriptionDTO {
    private boolean valid;
    private Set<MedicineQuantity> medicines;
}
