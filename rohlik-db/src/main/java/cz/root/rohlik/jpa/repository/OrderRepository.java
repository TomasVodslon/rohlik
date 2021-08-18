package cz.root.rohlik.jpa.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import cz.root.rohlik.entity.Order;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long> {

    @Query(value = "select * from ORDER_TABLE a where a.status = 'REGISTER'", nativeQuery = true)
    List<Order> getAllOrderWithStatusRegister();
}
