package com.example.customer.dto;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class CustomerResponse {
    private String id;
    private String name;
    private String email;
    private Double annualSpend;
    private LocalDateTime lastPurchaseDate;
    private String tier;
}
