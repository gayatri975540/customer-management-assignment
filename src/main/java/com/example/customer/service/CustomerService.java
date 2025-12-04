package com.example.customer.service;

import com.example.customer.dto.CustomerRequest;
import com.example.customer.dto.CustomerResponse;

import java.util.List;

public interface CustomerService {

    CustomerResponse create(CustomerRequest req);

    CustomerResponse getById(String id);

    CustomerResponse getByName(String name);

    CustomerResponse getByEmail(String email);

    CustomerResponse update(String id, CustomerRequest req);

    void delete(String id);

    List<CustomerResponse> listAll();
}
