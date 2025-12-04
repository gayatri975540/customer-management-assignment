package com.example.customer.controller;
import com.example.customer.dto.CustomerRequest;
import com.example.customer.dto.CustomerResponse;
import com.example.customer.service.CustomerServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerServiceImpl service;

    public CustomerController(CustomerServiceImpl service){ this.service = service; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse create(@Valid @RequestBody CustomerRequest req){
        return service.create(req);
    }

    @GetMapping("/{id}")
    public CustomerResponse getById(@PathVariable String id){
        return service.getById(id);
    }

    @GetMapping
    public CustomerResponse getByQuery(@RequestParam(required = false) String name,
                                       @RequestParam(required = false) String email){
        if (name != null) return service.getByName(name);
        if (email != null) return service.getByEmail(email);
        throw new RuntimeException("Provide name or email");
    }

    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable String id, @Valid @RequestBody CustomerRequest req){
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id){
        service.delete(id);
    }

    @GetMapping("/all")
    public List<CustomerResponse> listAll(){
        return service.listAll();
    }
}
