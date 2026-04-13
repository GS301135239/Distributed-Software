package org.example.distributedsoftwareserver.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.distributedsoftwareserver.Entity.Model.Order;
import org.example.distributedsoftwareserver.Mapper.GoodMapper;
import org.example.distributedsoftwareserver.Mapper.OrdersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class OrderMessageConsumer {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private GoodMapper goodMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    @KafkaListener(topics = "seckill-orders", groupId = "seckill-group")
    public void consume(String message) {
        try {
            Order order = objectMapper.readValue(message, Order.class);

            // 1. Idempotency check
            if (ordersMapper.selectOrdersByOrderID(order.getOrderID()) != null) {
                log.info("Duplicate order detected, skipping: {}", order.getOrderID());
                return;
            }

            // 2. MySQL stock reduction (using optimistic lock or atomic update)
            int updated = goodMapper.decrementInventory(order.getGoodId(), order.getOrderQuantity());
            if (updated > 0) {
                // 3. Insert order record
                ordersMapper.insertOrder(order);
                log.info("Order created successfully: {}", order.getOrderID());
            } else {
                log.error("Failed to create order due to insufficient inventory in DB: {}", order.getOrderID());
                throw new RuntimeException("DB Stock reduction failed");
            }
        } catch (Exception e) {
            log.error("Failed to process order message: {}", message, e);
            throw new RuntimeException(e);
        }
    }
}
