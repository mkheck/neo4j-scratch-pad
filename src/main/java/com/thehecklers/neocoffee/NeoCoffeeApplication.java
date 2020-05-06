package com.thehecklers.neocoffee;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.extern.java.Log;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
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

//@Log
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

        System.out.println("--- Shops ---");
        csRepo.findAll().forEach(System.out::println);

/*
        log.info("--- Drinks ---");
        cdRepo.findAll().forEach(drink -> log.info(drink.toString()));

        log.info("");

        log.info("--- Shops ---");
        csRepo.findAll().forEach(shop -> log.info(shop.toString()));
*/
    }
}

@RestController
@RequestMapping("/drinks")
@AllArgsConstructor
class CoffeeDrinkController {
    private final CoffeeDrinkRepo drinkRepo;

    @GetMapping
    Iterable<CoffeeDrink> getAll() {
        return drinkRepo.findAll();
    }
}

@RestController
@RequestMapping("/shops")
@AllArgsConstructor
class CoffeeShopController {
    private final CoffeeShopRepo shopRepo;

    @GetMapping
    Iterable<CoffeeShop> getAll() {
        return shopRepo.findAll();
    }
}

interface CoffeeDrinkRepo extends Neo4jRepository<CoffeeDrink, Long> {
}

interface CoffeeShopRepo extends Neo4jRepository<CoffeeShop, Long> {
}

@NodeEntity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@ToString(exclude = "destinations")
class CoffeeDrink {
    @Id
    @GeneratedValue
    private Long neoId;
    @NonNull
    private String id;
    @NonNull
    private String description;

    @JsonIgnoreProperties("drinks")
    @Relationship(type = "OFFERS", direction = INCOMING)
    private Iterable<CoffeeShop> destinations = new ArrayList<>();
}

@NodeEntity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
//@ToString(exclude = "drinks")
class CoffeeShop {
    @Id
    @GeneratedValue
    private Long neoId;
    @NonNull
    private String id;
    @NonNull
    private String name;

    @JsonIgnoreProperties("destinations")
    @Relationship(type = "OFFERS", direction = OUTGOING)
    private Iterable<CoffeeDrink> drinks = new ArrayList<>();
}
