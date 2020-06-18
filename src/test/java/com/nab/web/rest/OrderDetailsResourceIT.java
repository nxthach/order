package com.nab.web.rest;

import com.nab.OrderApp;
import com.nab.domain.OrderDetails;
import com.nab.repository.OrderDetailsRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link OrderDetailsResource} REST controller.
 */
@SpringBootTest(classes = OrderApp.class)

@AutoConfigureMockMvc
@WithMockUser
public class OrderDetailsResourceIT {

    private static final Long DEFAULT_PRODUCT_ID = 1L;
    private static final Long UPDATED_PRODUCT_ID = 2L;

    private static final BigDecimal DEFAULT_UNIT_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_UNIT_PRICE = new BigDecimal(2);

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderDetailsMockMvc;

    private OrderDetails orderDetails;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderDetails createEntity(EntityManager em) {
        OrderDetails orderDetails = new OrderDetails()
            .productId(DEFAULT_PRODUCT_ID)
            .unitPrice(DEFAULT_UNIT_PRICE)
            .quantity(DEFAULT_QUANTITY);
        return orderDetails;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderDetails createUpdatedEntity(EntityManager em) {
        OrderDetails orderDetails = new OrderDetails()
            .productId(UPDATED_PRODUCT_ID)
            .unitPrice(UPDATED_UNIT_PRICE)
            .quantity(UPDATED_QUANTITY);
        return orderDetails;
    }

    @BeforeEach
    public void initTest() {
        orderDetails = createEntity(em);
    }

    @Test
    @Transactional
    public void createOrderDetails() throws Exception {
        int databaseSizeBeforeCreate = orderDetailsRepository.findAll().size();

        // Create the OrderDetails
        restOrderDetailsMockMvc.perform(post("/api/order-details")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(orderDetails)))
            .andExpect(status().isCreated());

        // Validate the OrderDetails in the database
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        assertThat(orderDetailsList).hasSize(databaseSizeBeforeCreate + 1);
        OrderDetails testOrderDetails = orderDetailsList.get(orderDetailsList.size() - 1);
        assertThat(testOrderDetails.getProductId()).isEqualTo(DEFAULT_PRODUCT_ID);
        assertThat(testOrderDetails.getUnitPrice()).isEqualTo(DEFAULT_UNIT_PRICE);
        assertThat(testOrderDetails.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
    }

    @Test
    @Transactional
    public void createOrderDetailsWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = orderDetailsRepository.findAll().size();

        // Create the OrderDetails with an existing ID
        orderDetails.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderDetailsMockMvc.perform(post("/api/order-details")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(orderDetails)))
            .andExpect(status().isBadRequest());

        // Validate the OrderDetails in the database
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        assertThat(orderDetailsList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllOrderDetails() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get all the orderDetailsList
        restOrderDetailsMockMvc.perform(get("/api/order-details?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(orderDetails.getId().intValue())))
            .andExpect(jsonPath("$.[*].productId").value(hasItem(DEFAULT_PRODUCT_ID)))
            .andExpect(jsonPath("$.[*].unitPrice").value(hasItem(DEFAULT_UNIT_PRICE.intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)));
    }
    
    @Test
    @Transactional
    public void getOrderDetails() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        // Get the orderDetails
        restOrderDetailsMockMvc.perform(get("/api/order-details/{id}", orderDetails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(orderDetails.getId().intValue()))
            .andExpect(jsonPath("$.productId").value(DEFAULT_PRODUCT_ID))
            .andExpect(jsonPath("$.unitPrice").value(DEFAULT_UNIT_PRICE.intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY));
    }

    @Test
    @Transactional
    public void getNonExistingOrderDetails() throws Exception {
        // Get the orderDetails
        restOrderDetailsMockMvc.perform(get("/api/order-details/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOrderDetails() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        int databaseSizeBeforeUpdate = orderDetailsRepository.findAll().size();

        // Update the orderDetails
        OrderDetails updatedOrderDetails = orderDetailsRepository.findById(orderDetails.getId()).get();
        // Disconnect from session so that the updates on updatedOrderDetails are not directly saved in db
        em.detach(updatedOrderDetails);
        updatedOrderDetails
            .productId(UPDATED_PRODUCT_ID)
            .unitPrice(UPDATED_UNIT_PRICE)
            .quantity(UPDATED_QUANTITY);

        restOrderDetailsMockMvc.perform(put("/api/order-details")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedOrderDetails)))
            .andExpect(status().isOk());

        // Validate the OrderDetails in the database
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        assertThat(orderDetailsList).hasSize(databaseSizeBeforeUpdate);
        OrderDetails testOrderDetails = orderDetailsList.get(orderDetailsList.size() - 1);
        assertThat(testOrderDetails.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testOrderDetails.getUnitPrice()).isEqualTo(UPDATED_UNIT_PRICE);
        assertThat(testOrderDetails.getQuantity()).isEqualTo(UPDATED_QUANTITY);
    }

    @Test
    @Transactional
    public void updateNonExistingOrderDetails() throws Exception {
        int databaseSizeBeforeUpdate = orderDetailsRepository.findAll().size();

        // Create the OrderDetails

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderDetailsMockMvc.perform(put("/api/order-details")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(orderDetails)))
            .andExpect(status().isBadRequest());

        // Validate the OrderDetails in the database
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        assertThat(orderDetailsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteOrderDetails() throws Exception {
        // Initialize the database
        orderDetailsRepository.saveAndFlush(orderDetails);

        int databaseSizeBeforeDelete = orderDetailsRepository.findAll().size();

        // Delete the orderDetails
        restOrderDetailsMockMvc.perform(delete("/api/order-details/{id}", orderDetails.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAll();
        assertThat(orderDetailsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
