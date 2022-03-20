package com.elsys.globalserver.Services.Utils;

import com.elsys.globalserver.Models.Medicine;
import com.elsys.globalserver.Models.Prescription;
import lombok.Getter;

import java.util.*;

@Getter
public class PrescriptionDTO {
    private final int id;
    private final boolean valid;
    private final String doctor;
    private final String patient;
    private final List<MedicineQuantity> medicines;

    public PrescriptionDTO(Prescription prescription) {
        this.id = prescription.getId();
        this.valid = prescription.isValid();
        this.doctor = prescription.getDoctor();
        this.patient = prescription.getPatient();
        this.medicines = convert(prescription.getMedicines());
    }

    private List<MedicineQuantity> convert(List<Medicine> medicines) {
        Map<Medicine, Integer> map = new HashMap<>();
        for (Medicine medicine : medicines) {
            if (!map.containsKey(medicine))
                map.put(medicine, Collections.frequency(medicines, medicine));
        }

        List<MedicineQuantity> list = new ArrayList<>();
        for (Medicine medicine : map.keySet()) {
            list.add(new MedicineQuantity(medicine, map.get(medicine)));
        }

        return list;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrescriptionDTO that = (PrescriptionDTO) o;
        return valid == that.valid
                && Objects.equals(doctor, that.doctor)
                && Objects.equals(patient, that.patient)
                && medicines.containsAll(that.medicines);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valid, doctor, patient, medicines);
    }
}
