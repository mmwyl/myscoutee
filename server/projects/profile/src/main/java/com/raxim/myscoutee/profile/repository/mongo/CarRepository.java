package com.raxim.myscoutee.profile.repository.mongo;

import com.raxim.myscoutee.profile.data.document.mongo.Car;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.UUID;

@RepositoryRestResource(collectionResourceRel = "cars", path = "cars")
public interface CarRepository extends MongoRepository<Car, UUID>, QuerydslPredicateExecutor<Car> {
    // Add any additional methods or custom queries if needed
}
