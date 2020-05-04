package com.thehecklers.neocoffee;

import lombok.*;
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
import java.util.List;
import java.util.UUID;

@EnableNeo4jRepositories
@SpringBootApplication
public class NeoCoffeeApplication {

    public static void main(String[] args) {
        SpringApplication.run(NeoCoffeeApplication.class, args);
    }

}

@Component
@AllArgsConstructor
class DataLoader {
    private final CoffeeDrinkRepo cdRepo;
    private final CoffeeShopRepo csRepo;

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
@Data
@NoArgsConstructor
@RequiredArgsConstructor
class CoffeeDrink {
    @Id
    @GeneratedValue
    private Long neoId;

    @NonNull
    private String id;

    @NonNull
    private String description;

    @Relationship(type = "OFFERED_BY")
    private Iterable<CoffeeShop> destinations;
}

@NodeEntity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
class CoffeeShop {
    @Id
    @GeneratedValue
    private Long neoId;
    
    @NonNull
    private String id;
    
    @NonNull
    private String name;
    
    @Relationship(type = "OFFERS")
    private Iterable<CoffeeDrink> drinks;
}