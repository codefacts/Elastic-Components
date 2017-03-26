package test.model;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * Created by sohan on 3/22/2017.
 */
@Entity
@Table
@Data
@Builder
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int eid;
    private String ename;
    private double salary;
    private String deg;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "E_DEPARTMENT_ID", referencedColumnName = "ID")
    private Department department;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "E_DEPARTMENT2_ID", referencedColumnName = "ID")
    Department department2;
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Department> departments;

    public Employee(int eid, String ename, double salary, String deg, Department department, Department department2, List<Department> departments) {
        this.eid = eid;
        this.ename = ename;
        this.salary = salary;
        this.deg = deg;
        this.department = department;
        this.department2 = department2;
        this.departments = departments;
    }

    public Employee() {
        super();
    }

    public int getEid() {
        return eid;
    }

    public void setEid(int eid) {
        this.eid = eid;
    }

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getDeg() {
        return deg;
    }

    public void setDeg(String deg) {
        this.deg = deg;
    }

    @Override
    public String toString() {
        return "Employee [eid=" + eid + ", ename=" + ename + ", salary="
            + salary + ", deg=" + deg + "]";
    }
}
