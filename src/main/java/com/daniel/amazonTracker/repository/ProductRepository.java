package com.daniel.amazonTracker.repository;

import com.daniel.amazonTracker.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ProductRepository extends MongoRepository<Product, String> {

    @Query("{url: '?0'}")
    Product findItemByUrl(String url);
}
