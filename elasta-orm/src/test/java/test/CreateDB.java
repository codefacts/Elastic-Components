package test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import test.model.Department;
import test.model.Employee;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Created by sohan on 3/22/2017.
 */
public interface CreateDB {
    public static void main(String[] args) throws Exception{
        EntityManagerFactory emfactory = Persistence.
            createEntityManagerFactory("jpadb");
        EntityManager entitymanager = emfactory.
            createEntityManager();
        entitymanager.getTransaction().begin();

        Employee employee = Employee.builder()
            .eid(5258)
            .deg("ENG")
            .ename("Russel")
            .salary(52000)
            .departments(ImmutableList.of(
                Department.builder()
                    .id(6538921L)
                    .name("TTSK")
                    .department(
                        Department.builder()
                            .id(267935328L)
                            .name("VTVG")
                            .build()
                    )
                    .build()
            ))
            .build();

        Department department = Department.builder()
            .id(98798079087L)
            .name("ICT")
            .employee(employee)
            .department(
                Department.builder()
                    .id(98457984)
                    .name("RGV")
                    .employee(employee)
                    .department(
                        Department.builder()
                            .id(94504975049L)
                            .name("MCE")
                            .employee(employee)
                            .build()
                    )
                    .build()
            )
            .build();

        Department department2 = Department.builder()
            .id(988286326887L)
            .name("BGGV")
            .employee(employee)
            .department(
                Department.builder()
                    .id(8283175518L)
                    .name("MKLC")
                    .employee(employee)
                    .department(
                        Department.builder()
                            .id(56165582)
                            .name("VVKM")
                            .employee(
                                Employee.builder()
                                    .eid(2389)
                                    .deg("DOC")
                                    .ename("KOMOL")
                                    .salary(8000)
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .build();

        Employee employee22 = new Employee();
        employee22.setEid(1201);
        employee22.setEname("Gopal");
        employee22.setSalary(40000);
        employee22.setDeg("Technical Manager");
        employee22.setDepartment(department);
        employee22.setDepartment2(department2);
        employee22.setDepartments(ImmutableList.of(department));

        entitymanager.persist(employee22);

        System.out.println(
            new ObjectMapper().writeValueAsString(employee22)
        );

        entitymanager.getTransaction().commit();

        entitymanager.close();
        emfactory.close();
    }
}
