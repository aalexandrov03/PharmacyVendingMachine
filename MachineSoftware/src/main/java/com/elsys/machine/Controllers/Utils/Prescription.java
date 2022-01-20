package com.elsys.machine.Controllers.Utils;

import com.elsys.machine.DB_Entities.Medicine;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class Prescription {
    private int id;
    private boolean valid;
    private List<Medicine> medicines;
}
