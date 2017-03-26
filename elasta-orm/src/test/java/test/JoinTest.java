package test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import test.model.Address;
import test.model.AddressDetails;
import test.model.Department;
import test.model.Employee;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Created by sohan on 3/26/2017.
 */
public interface JoinTest {
    static void main(String[] args) throws Exception {

//        createAddress();

//        readAddress();
    }

    static void readAddress() {

        EntityManagerFactory emfactory = Persistence.
            createEntityManagerFactory("jpadb");
        EntityManager em = emfactory.createEntityManager();

        Address address = em.createQuery("select a from Address a where a.addressDetails.details = '5556' or a.id = 6565", Address.class).getSingleResult();

        System.out.println(address);

        em.close();
        emfactory.close();
    }

    static void createAddress() {
        EntityManagerFactory emfactory = Persistence.
            createEntityManagerFactory("jpadb");
        EntityManager em = emfactory.
            createEntityManager();

        em.getTransaction().begin();

        em.persist(
            Address.builder()
                .id(6565)
                .addressDetails(
                    AddressDetails.builder().id(2323).details("5556").build()
                )
                .build()
        );

        em.getTransaction().commit();

        em.close();
        emfactory.close();
    }
}
