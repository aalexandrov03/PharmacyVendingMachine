package com.elsys.machine.Controllers.Utils;

import com.elsys.machine.Models.Medicine;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Prescription {
    private boolean valid;
    private Set<Medicine> medicines;
}
