package cz.root.rohlik.service;

import cz.root.rohlik.domain.OrderDomain;
import cz.root.rohlik.entity.Product;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface RohlikService {

    Long createOrder(OrderDomain domain);

    void deleteOrder(Long orderId);

    void paymentOrder(Long id);

    List<Product> getAllProducts();

    void deleteProduct(String name);

    void createProduct(Product product);

    void checkAllUnPaidOrders();
}
