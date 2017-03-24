package test;

import test.model.Department;
import test.model.Employee;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Created by sohan on 3/22/2017.
 */
public interface CreateDB {
    public static void main(String[] args) {
        EntityManagerFactory emfactory = Persistence.
            createEntityManagerFactory("jpadb");
        EntityManager entitymanager = emfactory.
            createEntityManager();
        entitymanager.getTransaction().begin();

        Employee employee = new Employee();
        employee.setEid(1201);
        employee.setEname("Gopal");
        employee.setSalary(40000);
        employee.setDeg("Technical Manager");

        Department department = Department.builder()
            .id(98798079087L)
            .name("ICT")
            .department(
                Department.builder()
                    .id(98457984)
                    .name("RGV")
                    .department(
                        Department.builder()
                            .id(94504975049L)
                            .name("MCE")
                            .build()
                    )
                    .build()
            )
            .build();

        entitymanager.persist(department);

        employee.setDepartment(department);
        entitymanager.persist(employee);

        entitymanager.getTransaction().commit();

        entitymanager.close();
        emfactory.close();
    }
}
