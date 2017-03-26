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
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "ADDRESS_DETAILS_ID", referencedColumnName = "ID")
    private AddressDetails addressDetails;

    public Address() {
    }

    public Address(int id, AddressDetails addressDetails) {
        this.id = id;
        this.addressDetails = addressDetails;
    }
}
