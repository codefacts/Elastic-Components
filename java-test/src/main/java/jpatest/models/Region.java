package jpatest.models;

import javax.persistence.*;

/**
 * Created by Jango on 10/2/2016.
 */
@Entity
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

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

    @Override
    public String toString() {
        return "Region{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
    }
}
