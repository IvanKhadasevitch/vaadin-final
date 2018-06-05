package com.generation_p.hotel_demo.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
@Entity
@Table(name = "FACILITY")

@Setter @Getter
public class Facility extends AbstractEntity implements Serializable, Cloneable {
    @Column(name = "NAME", unique = true, nullable = false, length = 255)
    private String facilityName;

    @ManyToMany(mappedBy = "facilities")
    @Access(value = AccessType.PROPERTY)
    private List<Hotel> hotels = new ArrayList<>();

//    @ManyToMany(mappedBy = "facilities")
//    @Access(value = AccessType.PROPERTY)
//    private List<Person> owners = new ArrayList<>();

    public Facility() {}

    public Facility(Long id, String facilityName) {
        this.setId(id);
        this.facilityName = facilityName;
    }

    public boolean isPersisted() {
        return getId() != null;
    }

    @Override
    public Facility clone() throws CloneNotSupportedException {
        return (Facility) super.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Facility)) return false;

        Facility that = (Facility) o;

        return getFacilityName().equals(that.getFacilityName());
    }

    @Override
    public int hashCode() {
        return getFacilityName().hashCode();
    }

    @Override
    public String toString() {
        return "Facility{" +
                "facilityName='" + facilityName + '\'' +
                '}';
    }
}
