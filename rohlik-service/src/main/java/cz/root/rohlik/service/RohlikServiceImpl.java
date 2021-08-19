package cz.root.rohlik.service;

import cz.root.rohlik.domain.OrderDomain;
import cz.root.rohlik.domain.OrderItemDomain;
import cz.root.rohlik.entity.Order;
import cz.root.rohlik.entity.OrderItem;
import cz.root.rohlik.entity.OrderStatusEnum;
import cz.root.rohlik.entity.Product;
import cz.root.rohlik.jpa.repository.OrderRepository;
import cz.root.rohlik.jpa.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RohlikServiceImpl implements RohlikService {

    Logger logger = LoggerFactory.getLogger(RohlikService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @PostConstruct
    private void fillDb() {
        // vytvoreni nejakeho zakladni skladu
        List<Product> list = new ArrayList<>();
        Product p = new Product("voda", 10);
        Product p2 = new Product("mleko", 10);
        Product p3 = new Product("chleba", 10);
        list.add(p);
        list.add(p2);
        list.add(p3);
        productRepository.saveAll(list);
    }

    @Override
    public Long createOrder(OrderDomain domain) {
        Order entityOrder = new Order();
        for (OrderItemDomain orderItemDomain : domain.getItemDomainList()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(entityOrder);
            orderItem.setProductName(orderItemDomain.getProductName());
            orderItem.setProductQuantity(orderItemDomain.getProductQuantity());
            entityOrder.getOrderItemList().add(orderItem);
        }
        entityOrder.setCreateAt(ZonedDateTime.now());
        entityOrder.setStatus(OrderStatusEnum.REGISTRED);
        return orderRepository.save(entityOrder).getId();
    }

    @Override
    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    @Override
    public void paymentOrder(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            order.get().setStatus(OrderStatusEnum.PAID);
        } else {
            throw new RuntimeException("Order with name " + id + " not exist");
        }
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> result = new ArrayList<>();
        productRepository.findAll().forEach(result::add);
        return result;
    }

    @Override
    public void deleteProduct(String name) {
        productRepository.deleteProductByName(name);
    }

    @Override
    public void createProduct(Product product) {
        productRepository.save(product);
    }

    @Override
    public void checkAllUnPaidOrders() {
        logger.debug("Start of checkAllUnPaidOrders jobs");
        List<Order> allOrderWithStatusRegister = orderRepository.getAllOrderWithStatusRegister();
        for (Order order : allOrderWithStatusRegister) {
            if (order.getCreateAt().isBefore(ZonedDateTime.now().minus(30, ChronoUnit.MINUTES))) {
                for (OrderItem orderItem : order.getOrderItemList()) {
                    Product productByName = productRepository.findProductByName(orderItem.getProductName());
                    productByName.setQuantity(productByName.getQuantity() + orderItem.getProductQuantity());
                }
            }
        }
    }

    @Override
    public void validateNumberOfQuantity(OrderItem orderItem) {
        Product productByName = productRepository.findProductByName(orderItem.getProductName());
        if (productByName == null) {
            throw new RuntimeException("Order with name " + orderItem.getProductName() + " not exist");
        }

        if (productByName.getQuantity() < orderItem.getProductQuantity()) {
            throw new RuntimeException("Order with name " + orderItem.getProductName() + " isn’t enough quantity miss "
                    + (orderItem.getProductQuantity() - productByName.getQuantity()));
        }
    }
}
