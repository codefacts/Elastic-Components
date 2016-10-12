package jpatest.models;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Jango on 10/2/2016.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Br extends Employee {
    @ManyToOne(cascade = CascadeType.ALL)
    private Supervisor supervisor;
    @ManyToOne(cascade = CascadeType.ALL)
    private House house;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Tablet> tablets;
    @OneToMany(mappedBy = "comBr")
    private List<Command> commands;

    public Supervisor getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Supervisor supervisor) {
        this.supervisor = supervisor;
    }

    public House getHouse() {
        return house;
    }

    public void setHouse(House house) {
        this.house = house;
    }

    public List<Tablet> getTablets() {
        return tablets;
    }

    public void setTablets(List<Tablet> tablets) {
        this.tablets = tablets;
    }

    @Override
    public String toString() {
        return "Br{" +
            "supervisor=" + supervisor +
            ", house=" + house +
            ", tablets=" + tablets +
            '}';
    }
}
