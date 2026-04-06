package org.example.distributedsoftwareserver.Service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.distributedsoftwareserver.Common.Result;
import org.example.distributedsoftwareserver.Entity.DTO.CreateOrderDTO;
import org.example.distributedsoftwareserver.Mapper.OrdersMapper;
import org.example.distributedsoftwareserver.Utils.SnowflakeIdWorker;
import org.example.distributedsoftwareserver.Entity.Model.Order;
import org.example.distributedsoftwareserver.Entity.Model.Good;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.kafka.core.KafkaTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@Component
public class OrderService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private GoodService goodService;

    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String SECKILL_LUA =
        "if redis.call('sismember', KEYS[2], ARGV[1]) == 1 then " +
        "return -1; " +
        "end; " +
        "local stock = tonumber(redis.call('get', KEYS[1])); " +
        "if stock and stock > 0 then " +
        "redis.call('decr', KEYS[1]); " +
        "redis.call('sadd', KEYS[2], ARGV[1]); " +
        "return 1; " +
        "end; " +
        "return 0; ";


    public Result CreateOrder(CreateOrderDTO createOrderDTO, HttpServletRequest request) {
        Long userId = createOrderDTO.getUserID();
        Long goodId = createOrderDTO.getGoodId();

        // 1. Check if User exists (Safety check for Foreign Key)
        if (userId == null) {
            return Result.error("User ID cannot be null");
        }
        // You might want to add a DB check or Cache check here if users are many
        // For now, let's assume the caller should provide a valid ID,
        // but we can add a log to help debugging.
        log.info("Processing order for userId: {}, goodId: {}", userId, goodId);

        // 2. Check Redis via Lua
        List<String> keys = List.of("seckill:stock:" + goodId, "seckill:users:" + goodId);
        Long result = stringRedisTemplate.execute(
            new DefaultRedisScript<>(SECKILL_LUA, Long.class),
            keys,
            String.valueOf(userId)
        );

        if (result == null || result == 0) {
            return Result.error("Stock is empty");
        } else if (result == -1) {
            return Result.error("User has already purchased this item");
        }

        // 3. Success from Redis -> Create order details and send to Kafka
        Good good = goodService.getGoodById(goodId);
        if (good == null) {
            return Result.error("Item does not exist");
        }

        Order order = new Order();
        order.setOrderID(snowflakeIdWorker.nextId());
        order.setUserID(userId);
        order.setGoodId(goodId);
        order.setOrderQuantity(createOrderDTO.getOrderQuantity() != null ? createOrderDTO.getOrderQuantity() : 1);
        order.setOrderTotal(good.getGoodPrice() * order.getOrderQuantity());
        // Use Beijing Time (Asia/Shanghai, UTC+8)
        order.setOrderTime(Timestamp.valueOf(LocalDateTime.now(ZoneId.of("Asia/Shanghai"))));

        try {
            String message = objectMapper.writeValueAsString(order);
            kafkaTemplate.send("seckill-orders", message);
            return Result.success("Order request submitted successfully");
        } catch (Exception e) {
            log.error("Failed to enqueue order message", e);
            return Result.error("Internal Server Error processing order");
        }
    }

    public Result getOrdersByUserID(Long userId, HttpServletRequest request) {
        if (userId == null) {
            return Result.error("User ID cannot be null");
        }
        try {
            List<Order> orders = ordersMapper.selectOrdersByUserID(userId);
            log.info("Retrieved {} orders for userId: {}", orders.size(), userId);
            return Result.success(orders);
        } catch (Exception e) {
            log.error("Failed to retrieve orders for userId: {}", userId, e);
            return Result.error("Failed to retrieve orders, please try again later");
        }
    }

    public Result getOrdersByOrderID(Long orderId, HttpServletRequest request) {
        if (orderId == null) {
            return Result.error("Order ID cannot be null");
        }
        try {
            Order order = ordersMapper.selectOrdersByOrderID(orderId);
            if (order == null) {
                log.warn("Order not found for orderId: {}", orderId);
                return Result.error("Order not found");
            }
            log.info("Retrieved order for orderId: {}", orderId);
            return Result.success(order);
        } catch (Exception e) {
            log.error("Failed to retrieve order for orderId: {}", orderId, e);
            return Result.error("Failed to retrieve order, please try again later");
        }
    }
}
