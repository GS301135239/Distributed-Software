package org.example.distributedsoftwareserver.Mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.distributedsoftwareserver.Entity.Model.Order;

import java.util.List;

@Mapper
public interface OrdersMapper {
    void insertOrder(Order order);
    Order selectOrdersByOrderID(Long orderID);
    List<Order> selectOrdersByUserID(Long userID);
}
