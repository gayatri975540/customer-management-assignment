package com.example.customer.service;
import com.example.customer.dto.CustomerRequest;
import com.example.customer.dto.CustomerResponse;
import com.example.customer.entity.CustomerEntity;
import com.example.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository repo;

    public CustomerService(CustomerRepository repo){ this.repo = repo; }

    private String calculateTier(CustomerEntity e){
        Double spend = e.getAnnualSpend();
        LocalDateTime last = e.getLastPurchaseDate();
        if (spend == null) return "Silver";
        if (spend < 1000) return "Silver";
        if (spend >= 1000 && spend < 10000){
            if (last != null && last.isAfter(LocalDateTime.now().minus(12, ChronoUnit.MONTHS))) return "Gold";
            return "Silver";
        }
        if (spend >= 10000){
            if (last != null && last.isAfter(LocalDateTime.now().minus(6, ChronoUnit.MONTHS))) return "Platinum";
            return "Gold";
        }
        return "Silver";
    }

    private CustomerResponse toResponse(CustomerEntity e){
        CustomerResponse r = new CustomerResponse();
        r.setId(e.getId());
        r.setName(e.getName());
        r.setEmail(e.getEmail());
        r.setAnnualSpend(e.getAnnualSpend());
        r.setLastPurchaseDate(e.getLastPurchaseDate());
        r.setTier(calculateTier(e));
        return r;
    }

    private void copyRequestToEntity(CustomerRequest req, CustomerEntity e){
        e.setName(req.getName());
        e.setEmail(req.getEmail());
        e.setAnnualSpend(req.getAnnualSpend());
        e.setLastPurchaseDate(req.getLastPurchaseDate());
    }

    @Transactional
    public CustomerResponse create(CustomerRequest req){
        CustomerEntity e = new CustomerEntity();
        copyRequestToEntity(req,e);
        CustomerEntity saved = repo.save(e);
        return toResponse(saved);
    }

    public CustomerResponse getById(String id){
        CustomerEntity e = repo.findById(id).orElseThrow(() -> new RuntimeException("Customer not found"));
        return toResponse(e);
    }

    public CustomerResponse getByName(String name){
        CustomerEntity e = repo.findByName(name).orElseThrow(() -> new RuntimeException("Customer not found"));
        return toResponse(e);
    }

    public CustomerResponse getByEmail(String email){
        CustomerEntity e = repo.findByEmail(email).orElseThrow(() -> new RuntimeException("Customer not found"));
        return toResponse(e);
    }

    @Transactional
    public CustomerResponse update(String id, CustomerRequest req){
        CustomerEntity e = repo.findById(id.toString()).orElseThrow(() -> new RuntimeException("Customer not found"));
        copyRequestToEntity(req,e);
        CustomerEntity saved = repo.save(e);
        return toResponse(saved);
    }

    @Transactional
    public void delete(String id){
        repo.deleteById(id);
    }

    public List<CustomerResponse> listAll(){
        return repo.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }
}
