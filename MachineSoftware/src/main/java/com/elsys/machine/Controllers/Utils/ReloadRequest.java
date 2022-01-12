package com.elsys.machine.Controllers.Utils;

import com.elsys.machine.DB_Entities.Medicine;
import com.elsys.machine.DB_Entities.Routing;

import java.util.List;

public class ReloadRequest {
    private List<Medicine> medicines;
    private List<Routing> routing;

    public ReloadRequest() {
    }

    public List<Medicine> getMedicines() {
        return medicines;
    }

    public List<Routing> getRouting() {
        return routing;
    }
}
