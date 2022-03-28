package com.elsys.machine.Models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
public class Configuration {
    private boolean status;
    private String update_date;
    private List<Mapping> mapping;
    private RouterSettings settings;
    private String server_address;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Configuration that = (Configuration) o;
        return status == that.status
                && Objects.equals(update_date, that.update_date)
                && mapping.containsAll(that.getMapping())
                && Objects.equals(settings, that.settings)
                && Objects.equals(server_address, that.server_address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, update_date, mapping, settings, server_address);
    }
}
