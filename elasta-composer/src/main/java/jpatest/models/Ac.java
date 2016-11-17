package jpatest.models;

import javax.persistence.*;

/**
 * Created by Jango on 10/2/2016.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Ac extends Employee {
    @OneToOne(cascade = CascadeType.ALL)
    private Area area;

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    @Override
    public String toString() {
        return "Ac{" +
            "area=" + area +
            '}';
    }
}
