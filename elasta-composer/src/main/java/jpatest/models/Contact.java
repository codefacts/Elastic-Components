package jpatest.models;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Jango on 10/2/2016.
 */
@Entity
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private boolean btr;
    private boolean swp;
    @Temporal(TemporalType.TIMESTAMP)
    private Date contactDate;

    @ManyToOne(cascade = CascadeType.ALL)
    Br br;
    @ManyToOne(cascade = CascadeType.ALL)
    House house;
    @ManyToOne(cascade = CascadeType.ALL)
    Location location;
    @ManyToOne(cascade = CascadeType.ALL)
    Area area;
    @ManyToOne(cascade = CascadeType.ALL)
    Region region;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBtr() {
        return btr;
    }

    public void setBtr(boolean btr) {
        this.btr = btr;
    }

    public boolean isSwp() {
        return swp;
    }

    public void setSwp(boolean swp) {
        this.swp = swp;
    }

    public Date getContactDate() {
        return contactDate;
    }

    public void setContactDate(Date contactDate) {
        this.contactDate = contactDate;
    }

    public Br getBr() {
        return br;
    }

    public void setBr(Br br) {
        this.br = br;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "Contact{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", btr=" + btr +
            ", swp=" + swp +
            ", contactDate=" + contactDate +
            ", br=" + br +
            ", house=" + house +
            ", location=" + location +
            ", area=" + area +
            ", region=" + region +
            '}';
    }
}
