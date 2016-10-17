package jpatest.core;

import jpatest.models.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.metamodel.PluralAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * Created by Jango on 10/2/2016.
 */
public class Main {
    public static final String PU = "jpatest";

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory(PU);

        EntityManager em = emf.createEntityManager();

        try {

            em.getTransaction().begin();

            test1(em);

//            em.getTransaction().commit();

        } finally {

            em.close();

            emf.close();
        }

    }

    private static void test1(EntityManager em) {
        Region region = new Region();
        region.setName("dhaka");
        em.persist(region);

        Area area = new Area();
        area.setName("dhaka-south");
        area.setRegion(region);
        em.persist(area);

        ArrayList<Location> arrayList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Location location = new Location();
            location.setName("lalbag");
            em.persist(location);
            arrayList.add(location);
        }

        House house = new House();
        house.setName("kmal-house");
        house.setArea(area);
        house.setLocations(arrayList);
        em.persist(house);

        Ac ac = new Ac();
        ac.setFirstName("anwar");
        ac.setLastName("khan");
        ac.setArea(area);
        em.persist(ac);

        Supervisor supervisor = new Supervisor();
        supervisor.setFirstName("ataturk");
        supervisor.setLastName("kobja");
        supervisor.setAc(ac);
        supervisor.setHouses(Arrays.asList(house));
        em.persist(supervisor);

        ArrayList<Tablet> list1 = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Tablet tablet = new Tablet();
            tablet.setName("android galaxy S7");
            tablet.setBuyDate(new Date());
            em.persist(tablet);
            list1.add(tablet);
        }

        Br br = new Br();
        br.setFirstName("komol");
        br.setLastName("khan");
        br.setDateOfBirth(new Date());
        br.setSupervisor(supervisor);
        br.setTablets(list1);
        br.setHouse(house);
        em.persist(br);

        for (int i = 0; i < 5; i++) {
            Contact contact = new Contact();
            contact.setName("Rahinga-" + i);
            contact.setHouse(apply(new House(), hs -> {
                hs.setId(8L);
                hs.setName("kamal-house-8");
                hs.setLocations(Arrays.asList(
                    apply(new Location(), location -> {
                        location.setId(3L);
                        location.setName("monipur-3");
                        return location;
                    }),
                    apply(new Location(), location -> {
                        location.setId(4L);
                        location.setName("tangail-4");
                        return location;
                    }),
                    apply(new Location(), location -> {
                        location.setName("commilla");
                        return location;
                    })
                ));
                hs = em.merge(hs);
                return hs;
            }));
            contact.setArea(apply(new Area(), ar -> {
                ar.setId(2L);
                ar.setName("jossore-2");
                ar = em.merge(ar);
                return ar;
            }));
            contact.setBr(apply(new Br(), br1 -> {
                br1.setId(16L);
                br1.setFirstName("kona-16");
                br1.setLastName("dhola-16");
                br1 = em.merge(br1);
                return br1;
            }));
            contact.setContactDate(new Date());
            contact.setLocation(apply(new Location(), location -> {
                location.setName("agarwala");
                location = em.merge(location);
                return location;
            }));
            contact.setBtr(true);
            contact.setSwp(true);
            contact.setRegion(apply(new Region(), rg -> {
                rg.setId(1L);
                rg.setName("hala-1");
                rg = em.merge(rg);
                return rg;
            }));


            em.merge(contact);
            em.persist(contact);
        }

        em.getTransaction().commit();

        List list = em.createQuery("select b from Br b").getResultList();
        System.out.println("rr: " + list);
    }

    private static void createArea() {

    }

    public static <T> T call(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T apply(T t, Function<T, T> function) {
        return function.apply(t);
    }
}
