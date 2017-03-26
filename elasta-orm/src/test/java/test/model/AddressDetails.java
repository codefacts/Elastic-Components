package test.model;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

/**
 * Created by sohan on 3/26/2017.
 */
@Entity
@Table
@Data
@Builder
public class AddressDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String details;

    public AddressDetails() {
    }

    public AddressDetails(int id, String details) {
        this.id = id;
        this.details = details;
    }
}
