package jpatest.models;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Jango on 10/2/2016.
 */
@Entity
public class Tablet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private double price;
    @Temporal(TemporalType.TIMESTAMP)
    private Date buyDate;

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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(Date buyDate) {
        this.buyDate = buyDate;
    }

    @Override
    public String toString() {
        return "Tablet{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", price=" + price +
            ", buyDate=" + buyDate +
            '}';
    }
}
