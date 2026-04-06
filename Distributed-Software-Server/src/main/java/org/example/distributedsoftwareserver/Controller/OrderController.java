package org.example.distributedsoftwareserver.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.example.distributedsoftwareserver.Common.Result;
import org.example.distributedsoftwareserver.Entity.DTO.CreateOrderDTO;
import org.example.distributedsoftwareserver.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "订单管理")
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Operation(summary = "创建订单")
    @PostMapping("/createOrder")
    public Result<?> createOrder(@RequestBody CreateOrderDTO createOrderDTO, HttpServletRequest request) {
        return orderService.CreateOrder(createOrderDTO, request);
    }

    @Operation(summary = "获取用户订单列表")
    @GetMapping("/getUserOrders")
    public Result<?> getUserOrders(@RequestParam Long UserID, HttpServletRequest request) {
        return orderService.getOrdersByUserID(UserID, request);
    }

    @Operation(summary = "根据订单ID获取订单列表")
    @GetMapping("/getOrderByID")
    public Result<?> getOrderByID(@RequestParam Long OrderID, HttpServletRequest request) {
        return orderService.getOrdersByOrderID(OrderID, request);
    }
}
