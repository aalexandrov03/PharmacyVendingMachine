package com.elsys.machine.Models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class Configuration {
    private boolean status;
    private String update_date;
    private List<Mapping> mapping;
    private RouterSettings settings;
    private String server_address;
}
