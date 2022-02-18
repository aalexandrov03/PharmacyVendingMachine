package com.elsys.machine.Controllers.Utils;

import com.elsys.machine.Models.Medicine;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class PrescriptionDTO {
    private boolean isValid;
    private List<Medicine> medicines;
}
