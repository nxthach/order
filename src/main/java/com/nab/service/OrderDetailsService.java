package com.nab.service;

import com.nab.domain.OrderDetails;
import com.nab.domain.Orders;
import com.nab.repository.OrderDetailsRepository;
import com.nab.service.client.ProductClient;
import com.nab.service.dto.OrderDetailsDTO;
import com.nab.service.dto.ProductDTO;
import com.nab.web.rest.OrderDetailsResource;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderDetailsService {

    private final Logger log = LoggerFactory.getLogger(OrderDetailsResource.class);

    private final OrderDetailsRepository orderDetailsRepository;
    private final ProductClient productClient;

    public OrderDetailsService(OrderDetailsRepository orderDetailsRepository,
        ProductClient productClient) {
        this.orderDetailsRepository = orderDetailsRepository;
        this.productClient = productClient;
    }

    public Optional<OrderDetailsDTO> findById(Long id) {
        OrderDetailsDTO orderDetailsDTO = new OrderDetailsDTO();
        Optional<OrderDetails> orderDetailsOpt = orderDetailsRepository.findById(id);

        if(orderDetailsOpt.isPresent()){
            OrderDetails orderDetails = orderDetailsOpt.get();

            Orders orders = orderDetails.getOrders();
            if(orders != null) {
                orderDetailsDTO.setCustomerName(orders.getCustomerName());
                orderDetailsDTO.setOrderDate(orders.getOrderDate());
                orderDetailsDTO.setOrderStatus(orders.getStatus());
            }

            orderDetailsDTO.setQuantity(orderDetails.getQuantity());
            orderDetailsDTO.setProductPrice(orderDetails.getUnitPrice());

            ResponseEntity<ProductDTO> productResponse = productClient.getProduct(orderDetails.getProductId());
            ProductDTO product = productResponse.getBody();

            if(product != null){
                log.debug("Got product from Product service : {}", product);
                orderDetailsDTO.setProductName(product.getName());
                orderDetailsDTO.setProductCode(product.getCode());
                orderDetailsDTO.setProductBrand(product.getBrand());
            }
        }

        return Optional.ofNullable(orderDetailsDTO);
    }
}
