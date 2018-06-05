package com.generation_p.hotel_demo.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "HOTEL")
@SuppressWarnings("serial")
public class Hotel extends AbstractEntity implements Serializable, Cloneable {

	private String name = "";

	private String address = "";

	private Integer rating;

	@Column(name = "OPERATES_FROM")
	private Long operatesFrom;

	@ManyToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "CATEGORY_ID")
	private HotelCategory category;

//	@Embedded
	private FreeServices freeServices;

	private String url;

	// 	DESCRIPTION	varchar(255)  NULL=YES
	@Column(name = "DESCRIPTION", nullable = true, length = 255)
	private String description = "";

    @ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    private Set<Facility> facilities = new HashSet<>();


	@Override
	public String toString() {
		return name + " " + rating + " stars " + address;
	}

	@Override
	public Hotel clone() throws CloneNotSupportedException {
		return (Hotel) super.clone();
	}

	public Hotel() {}

    public void addFacility(Facility facility) {
        facilities.add( facility );
        facility.getHotels().add( this );
    }

    public void removeFacility(Facility facility) {
        facilities.remove( facility );
        facility.getHotels().remove( this );
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public Long getOperatesFrom() {
		return operatesFrom;
	}

	public void setOperatesFrom(Long operatesFrom) {
		this.operatesFrom = operatesFrom;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public HotelCategory getCategory() {
		return category;
	}

	public void setCategory(HotelCategory category) {
		this.category = category;
	}

	public FreeServices getFreeServices() {
		return freeServices;
	}

	public void setFreeServices(FreeServices freeServices) {
		this.freeServices = freeServices;
	}

}