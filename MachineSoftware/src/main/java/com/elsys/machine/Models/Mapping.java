package com.elsys.machine.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Mapping {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    @Column
    private String medicineName;
    @Column(unique = true, nullable = false)
    private long slotID;

    public Mapping(String medicineName, long slotID){
        this.medicineName = medicineName;
        this.slotID = slotID;
    }
}
