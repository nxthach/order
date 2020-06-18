package com.nab.service.client;

import com.nab.service.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="ProductClient", url = "http://localhost:8081/api")
public interface ProductClient {

    @GetMapping("/products/{id}")
    ResponseEntity<ProductDTO> getProduct(@PathVariable(value="id") Long id);
}
