package org.example.distributedsoftwareserver.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.distributedsoftwareserver.Entity.Model.Order;
import org.example.distributedsoftwareserver.Mapper.GoodMapper;
import org.example.distributedsoftwareserver.Mapper.OrdersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderMessageConsumer {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private GoodMapper goodMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "seckill-orders", groupId = "seckill-group")
    public void consume(String message) {
        try {
            Order order = objectMapper.readValue(message, Order.class);

            // Optimistic lock decrement
            int updated = goodMapper.decrementInventory(order.getGoodId(), order.getOrderQuantity());
            if (updated > 0) {
                // Generate record in Order table
                ordersMapper.insertOrder(order);
                log.info("Order created successfully: {}", order.getOrderID());
            } else {
                log.warn("Failed to create order due to insufficient inventory: {}", order.getOrderID());
                // Handle failure - rollback could be sent back or Redis updated, normally seckill handles failure silently or logs
            }
        } catch (Exception e) {
            log.error("Failed to process order message: {}", message, e);
        }
    }
}


