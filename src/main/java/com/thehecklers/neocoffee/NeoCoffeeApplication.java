package com.thehecklers.neocoffee;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.neo4j.ogm.annotation.Relationship.INCOMING;
import static org.neo4j.ogm.annotation.Relationship.OUTGOING;

@EnableNeo4jRepositories
@SpringBootApplication
public class NeoCoffeeApplication {

    public static void main(String[] args) {
        SpringApplication.run(NeoCoffeeApplication.class, args);
    }

}

@Component
class DataLoader {
    private final CoffeeDrinkRepo cdRepo;
    private final CoffeeShopRepo csRepo;

    public DataLoader(CoffeeDrinkRepo cdRepo, CoffeeShopRepo csRepo) {
        this.cdRepo = cdRepo;
        this.csRepo = csRepo;
    }

    @PostConstruct
    private void load() {
        // Coffee drinks
        cdRepo.deleteAll();

        CoffeeDrink americano = cdRepo.save(new CoffeeDrink(UUID.randomUUID().toString(), "Americano"));
        CoffeeDrink espresso = cdRepo.save(new CoffeeDrink(UUID.randomUUID().toString(), "Espresso"));
        CoffeeDrink flatWhite = cdRepo.save(new CoffeeDrink(UUID.randomUUID().toString(), "Flat white"));
        CoffeeDrink latte = cdRepo.save(new CoffeeDrink(UUID.randomUUID().toString(), "Latte"));

        // Coffee shops
        csRepo.deleteAll();

        CoffeeShop maevas = csRepo.save(new CoffeeShop(UUID.randomUUID().toString(), "Maeva's Coffee"));
        CoffeeShop germania = csRepo.save(new CoffeeShop(UUID.randomUUID().toString(), "Germania Brew Haus"));
        CoffeeShop stlbreadco = csRepo.save(new CoffeeShop(UUID.randomUUID().toString(), "St. Louis Bread Company"));

        // Associate!
        maevas.setDrinks(List.of(americano, espresso, flatWhite, latte));
        csRepo.save(maevas);

        germania.setDrinks(List.of(americano, espresso, flatWhite, latte));
        csRepo.save(germania);

        stlbreadco.setDrinks(List.of(americano, espresso, latte));
        csRepo.save(stlbreadco);

        System.out.println("--- Drinks ---");
        cdRepo.findAll().forEach(System.out::println);

        System.out.println();

        System.out.println("--- Shops ---");
        csRepo.findAll().forEach(System.out::println);
    }
}

interface CoffeeDrinkRepo extends Neo4jRepository<CoffeeDrink, Long> {}

interface CoffeeShopRepo extends Neo4jRepository<CoffeeShop, Long> {}

@NodeEntity
class CoffeeDrink {
    @Id
    @GeneratedValue
    private Long neoId;
    private String id;
    private String description;

    //@Relationship(type = "OFFERS", direction = INCOMING)
    private Iterable<CoffeeShop> destinations = new ArrayList<>();

    public CoffeeDrink() {
    }

    public CoffeeDrink(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public CoffeeDrink(String id, String description, Iterable<CoffeeShop> destinations) {
        this.id = id;
        this.description = description;
        this.destinations = destinations;
    }

    public Long getNeoId() {
        return neoId;
    }

/*  Neo4j generates this, see above
    public void setNeoId(Long neoId) {
        this.neoId = neoId;
    }
*/

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Iterable<CoffeeShop> getDestinations() {
        return destinations;
    }

    public void setDestinations(Iterable<CoffeeShop> destinations) {
        this.destinations = destinations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoffeeDrink that = (CoffeeDrink) o;
        return neoId.equals(that.neoId) &&
                id.equals(that.id) &&
                description.equals(that.description) &&
                Objects.equals(destinations, that.destinations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(neoId, id, description, destinations);
    }

    @Override
    public String toString() {
        return "CoffeeDrink{" +
                "neoId=" + neoId +
                ", id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", destinations=" + destinations.toString() +
                '}';
    }
}

@NodeEntity
class CoffeeShop {
    @Id
    @GeneratedValue
    private Long neoId;
    private String id;
    private String name;

    @Relationship(type = "OFFERS", direction = OUTGOING)
    private Iterable<CoffeeDrink> drinks = new ArrayList<>();

    public CoffeeShop() {
    }

    public CoffeeShop(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public CoffeeShop(String id, String name, Iterable<CoffeeDrink> drinks) {
        this.id = id;
        this.name = name;
        this.drinks = drinks;
    }

    public Long getNeoId() {
        return neoId;
    }

/*
    public void setNeoId(Long neoId) {
        this.neoId = neoId;
    }
*/

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Iterable<CoffeeDrink> getDrinks() {
        return drinks;
    }

    public void setDrinks(Iterable<CoffeeDrink> drinks) {
        this.drinks = drinks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoffeeShop that = (CoffeeShop) o;
        return neoId.equals(that.neoId) &&
                id.equals(that.id) &&
                name.equals(that.name) &&
                Objects.equals(drinks, that.drinks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(neoId, id, name, drinks);
    }

    @Override
    public String toString() {
        return "CoffeeShop{" +
                "neoId=" + neoId +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", drinks=" + drinks.toString() +
                '}';
    }
}