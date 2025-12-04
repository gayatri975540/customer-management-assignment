package com.example.customer;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import com.example.customer.dto.CustomerRequest;
import com.example.customer.dto.CustomerResponse;
import com.example.customer.entity.CustomerEntity;
import com.example.customer.repository.CustomerRepository;
import com.example.customer.service.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CustomerServiceImplTest {

    @Mock
    private CustomerRepository repo;

    @InjectMocks
    private CustomerServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private CustomerEntity createCustomerEntity(String id, String name, String email, Double spend, LocalDateTime lastPurchase) {
        CustomerEntity e = new CustomerEntity();
        e.setId(id);
        e.setName(name);
        e.setEmail(email);
        e.setAnnualSpend(spend);
        e.setLastPurchaseDate(lastPurchase);
        return e;
    }

    @Test
    void testCreateCustomer() {
        CustomerRequest req = new CustomerRequest();
        req.setName("John Doe");
        req.setEmail("john.doe@example.com");
        req.setAnnualSpend(5000.0);
        req.setLastPurchaseDate(LocalDateTime.now().minusMonths(3));

        CustomerEntity savedEntity = createCustomerEntity("1", req.getName(), req.getEmail(), req.getAnnualSpend(), req.getLastPurchaseDate());
        when(repo.save(any(CustomerEntity.class))).thenReturn(savedEntity);

        CustomerResponse response = service.create(req);

        assertNotNull(response);
        assertEquals("John Doe", response.getName());
        assertEquals("Gold", response.getTier());
        verify(repo, times(1)).save(any(CustomerEntity.class));
    }

    @Test
    void testGetById() {
        CustomerEntity entity = createCustomerEntity("1", "Alice", "alice@example.com", 12000.0, LocalDateTime.now().minusMonths(2));
        when(repo.findById("1")).thenReturn(Optional.of(entity));

        CustomerResponse response = service.getById("1");

        assertEquals("Platinum", response.getTier());
        assertEquals("Alice", response.getName());
        verify(repo, times(1)).findById("1");
    }

    @Test
    void testGetByIdNotFound() {
        when(repo.findById("999")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getById("999"));
        assertEquals("Customer not found", ex.getMessage());
    }

    @Test
    void testUpdateCustomer() {
        CustomerEntity existing = createCustomerEntity("1", "Bob", "bob@example.com", 500.0, LocalDateTime.now().minusMonths(14));
        CustomerRequest req = new CustomerRequest();
        req.setName("Bob Updated");
        req.setEmail("bob.updated@example.com");
        req.setAnnualSpend(1500.0);
        req.setLastPurchaseDate(LocalDateTime.now().minusMonths(5));

        when(repo.findById("1")).thenReturn(Optional.of(existing));
        when(repo.save(any(CustomerEntity.class))).thenReturn(existing);

        CustomerResponse response = service.update("1", req);

        assertEquals("Bob Updated", response.getName());
        assertEquals("Gold", response.getTier());
        verify(repo).save(existing);
    }

    @Test
    void testDeleteCustomer() {
        doNothing().when(repo).deleteById("1");
        service.delete("1");
        verify(repo, times(1)).deleteById("1");
    }

    @Test
    void testListAll() {
        CustomerEntity c1 = createCustomerEntity("1", "Alice", "alice@example.com", 200.0, LocalDateTime.now().minusMonths(14));
        CustomerEntity c2 = createCustomerEntity("2", "Bob", "bob@example.com", 12000.0, LocalDateTime.now().minusMonths(2));

        when(repo.findAll()).thenReturn(Arrays.asList(c1, c2));

        List<CustomerResponse> list = service.listAll();

        assertEquals(2, list.size());
        assertEquals("Silver", list.get(0).getTier());
        assertEquals("Platinum", list.get(1).getTier());
    }
    @Test
    void testCalculateTierWithNullSpend() {
        CustomerRequest req = new CustomerRequest();
        req.setName("Null Spend");
        req.setEmail("null@example.com");
        req.setAnnualSpend(null);
        req.setLastPurchaseDate(LocalDateTime.now());

        CustomerEntity savedEntity = new CustomerEntity();
        savedEntity.setId("1");
        savedEntity.setName(req.getName());
        savedEntity.setEmail(req.getEmail());
        savedEntity.setAnnualSpend(req.getAnnualSpend());
        savedEntity.setLastPurchaseDate(req.getLastPurchaseDate());

        when(repo.save(any(CustomerEntity.class))).thenReturn(savedEntity);

        CustomerResponse response = service.create(req);

        assertEquals("Silver", response.getTier());
        assertEquals("Null Spend", response.getName());
    }


    @Test
    void testGetByNameNotFound() {
        when(repo.findByName("unknown")).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getByName("unknown"));
        assertEquals("Customer not found", ex.getMessage());
    }

    @Test
    void testGetByEmailNotFound() {
        when(repo.findByEmail("unknown@example.com")).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getByEmail("unknown@example.com"));
        assertEquals("Customer not found", ex.getMessage());
    }

    @Test
    void testTierGoldForOldHighSpend() {
        CustomerEntity e = createCustomerEntity("1", "Rich Old", "rich@example.com", 15000.0, LocalDateTime.now().minusMonths(8));
        when(repo.findById("1")).thenReturn(Optional.of(e));
        CustomerResponse r = service.getById("1");
        assertEquals("Gold", r.getTier());
    }

    @Test
    void testTierSilverForOldMediumSpend() {
        CustomerEntity e = createCustomerEntity("1", "Medium Old", "med@example.com", 5000.0, LocalDateTime.now().minusMonths(13));
        when(repo.findById("1")).thenReturn(Optional.of(e));
        CustomerResponse r = service.getById("1");
        assertEquals("Silver", r.getTier());
    }
}
