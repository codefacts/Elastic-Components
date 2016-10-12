package jpatest.models;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Jango on 10/2/2016.
 */
@Entity
public class House {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Location> locations;
    @ManyToOne(cascade = CascadeType.ALL)
    private Area area;

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

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    @Override
    public String toString() {
        return "House{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", locations=" + locations +
            ", area=" + area +
            '}';
    }
}
