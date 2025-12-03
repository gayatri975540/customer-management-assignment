package com.example.customer.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "customers")
public class CustomerEntity {
    @Id
    private String id;

    @Column(nullable = false)
     private String name;

     @Column(nullable = false, unique = true)
     private String email;

     private Double annualSpend;

     private LocalDateTime lastPurchaseDate;

     @PrePersist
     public void prePersist() {
         if (this.id == null) this.id = UUID.randomUUID().toString();
     }
}

