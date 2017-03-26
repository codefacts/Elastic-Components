package test.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Created by sohan on 3/25/2017.
 */
@Entity
@Table
@Data
@Builder
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;
    String name;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "D_DEPARTMENT_ID", referencedColumnName = "ID")
    Department department;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "D_EMPLOYEE_EID", referencedColumnName = "EID")
    Employee employee;

    public Department() {
    }

    public Department(long id, String name, Department department, Employee employee) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.employee = employee;
    }
}
