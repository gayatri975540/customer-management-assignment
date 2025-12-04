package com.example.customer.service;
import com.example.customer.dto.CustomerRequest;
import com.example.customer.dto.CustomerResponse;
import com.example.customer.entity.CustomerEntity;
import com.example.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repo;

    public CustomerServiceImpl(CustomerRepository repo) {
        this.repo = repo;
    }

    private String calculateTier(CustomerEntity e) {
        Double spend = e.getAnnualSpend();
        LocalDateTime last = e.getLastPurchaseDate();
        if (spend == null) return "Silver";
        if (spend < 1000) return "Silver";
        if (spend >= 1000 && spend < 10000) {
            if (last != null && last.isAfter(LocalDateTime.now().minusMonths(12))) return "Gold";
            return "Silver";
        }
        if (spend >= 10000) {
            if (last != null && last.isAfter(LocalDateTime.now().minusMonths(6))) return "Platinum";
            return "Gold";
        }
        return "Silver";
    }

    private CustomerResponse toResponse(CustomerEntity e) {
        return CustomerResponse.builder()
                .id(e.getId())
                .name(e.getName())
                .email(e.getEmail())
                .annualSpend(e.getAnnualSpend())
                .lastPurchaseDate(e.getLastPurchaseDate())
                .tier(calculateTier(e))
                .build();
    }

    private void copyRequestToEntity(CustomerRequest req, CustomerEntity e) {
        e.setName(req.getName());
        e.setEmail(req.getEmail());
        e.setAnnualSpend(req.getAnnualSpend());
        e.setLastPurchaseDate(req.getLastPurchaseDate());
    }

    @Override
    @Transactional
    public CustomerResponse create(CustomerRequest req) {
        CustomerEntity e = new CustomerEntity();
        copyRequestToEntity(req, e);
        return toResponse(repo.save(e));
    }

    @Override
    public CustomerResponse getById(String id) {
        return toResponse(repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found")));
    }

    @Override
    public CustomerResponse getByName(String name) {
        return toResponse(repo.findByName(name)
                .orElseThrow(() -> new RuntimeException("Customer not found")));
    }

    @Override
    public CustomerResponse getByEmail(String email) {
        return toResponse(repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found")));
    }

    @Override
    @Transactional
    public CustomerResponse update(String id, CustomerRequest req) {
        CustomerEntity e = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        copyRequestToEntity(req, e);
        return toResponse(repo.save(e));
    }

    @Override
    @Transactional
    public void delete(String id) {
        repo.deleteById(id);
    }

    @Override
    public List<CustomerResponse> listAll() {
        return repo.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
