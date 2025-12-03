package com.example.customer.repository;
import com.example.customer.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<CustomerEntity,String> {
    Optional<CustomerEntity> findByName(String name);
    Optional<CustomerEntity> findByEmail(String email);
}
