package cz.root.rohlik;

import cz.root.rohlik.entity.Order;
import cz.root.rohlik.entity.OrderItem;
import cz.root.rohlik.entity.OrderStatusEnum;
import cz.root.rohlik.entity.Product;
import cz.root.rohlik.jpa.repository.OrderRepository;
import cz.root.rohlik.jpa.repository.ProductRepository;
import cz.root.rohlik.service.RohlikService;
import cz.root.rohlik.transfer.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RohlikApplicationIT {

    public static final String HOUSKA = "houska";
    public static final String ZBOZI_KTEREHO_JE_MALO = "zboziKterehoJeMalo";
    @LocalServerPort
    private int randomServerPort;
    private String localUri;
    private TestRestTemplate restTemplate;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private RohlikService rohlikService;

    @BeforeAll
    public void setup() {
        localUri = "http://localhost:" + randomServerPort;
        restTemplate = new TestRestTemplate();
    }

    @Test
    public void createOrder() {
        OrderTO order = createOrderTO();
        String newOrderId = restTemplate.postForObject(localUri + "/order", order, String.class, (Object) null);
        Assertions.assertNotNull(newOrderId);
        Optional<Order> orderFromDb = orderRepository.findById(Long.valueOf(newOrderId));
        Assertions.assertNotNull(orderFromDb.get());
        Optional<OrderItem> first = orderFromDb.get().getOrderItemList().stream()
                .filter(x -> "voda".equals(x.getProductName())).findFirst();
        Assertions.assertNotNull(first.get());
        Assertions.assertEquals(OrderStatusEnum.REGISTRED, orderFromDb.get().getStatus());

    }

    @Test
    public void createOrderForNotExistProduct() {
        OrderTO order = createOrderTO();
        OrderItemTO notExistProduct = new OrderItemTO();
        notExistProduct.setName("neexistuji");
        notExistProduct.setQuantity(11);
        order.getOrderitems().add(notExistProduct);
        ErrorMessageTO errorMessageTO = restTemplate
                .postForObject(localUri + "/order", order, ErrorMessageTO.class, (Object) null);
        Assertions.assertNotNull(errorMessageTO);
    }

    @Test
    public void createOrderWithnoutQuantity() {
        Product product = new Product(ZBOZI_KTEREHO_JE_MALO, 6);
        productRepository.save(product);
        OrderTO order = createOrderTO();
        OrderItemTO notExistProduct = new OrderItemTO();
        notExistProduct.setName(ZBOZI_KTEREHO_JE_MALO);
        notExistProduct.setQuantity(11);
        order.getOrderitems().add(notExistProduct);
        ErrorMessageTO errorMessageTO = restTemplate
                .postForObject(localUri + "/order", order, ErrorMessageTO.class, (Object) null);
        // TOHLE je celkove uz trochu narychlo
        Assertions.assertNotNull(errorMessageTO);
    }

    @Test
    public void createAndDeleteOrder() {
        OrderTO order = createOrderTO();
        String newOrderId = restTemplate.postForObject(localUri + "/order", order, String.class, (Object) null);
        Assertions.assertNotNull(newOrderId);
        Optional<Order> orderFromDb = orderRepository.findById(Long.valueOf(newOrderId));
        Assertions.assertNotNull(orderFromDb.get());
        restTemplate.delete(localUri + "/order?id=" + newOrderId);
        Optional<Order> secondTimeFromDb = orderRepository.findById(Long.valueOf(newOrderId));
        Assertions.assertFalse(secondTimeFromDb.isPresent());
    }

    @Test
    public void createAndPayOrder() {
        OrderTO order = createOrderTO();
        String newOrderId = restTemplate.postForObject(localUri + "/order", order, String.class, (Object) null);
        Assertions.assertNotNull(newOrderId);
        Optional<Order> orderFromDb = orderRepository.findById(Long.valueOf(newOrderId));
        Assertions.assertNotNull(orderFromDb.get());
        Assertions.assertTrue(orderFromDb.get().getStatus() == OrderStatusEnum.REGISTRED);
        PaymentTO paymentTO = new PaymentTO();
        paymentTO.setId(Long.valueOf(newOrderId));
        restTemplate.postForObject(localUri + "/payment", paymentTO, String.class, (Object) null);
        Optional<Order> afterPay = orderRepository.findById(Long.valueOf(newOrderId));
        Assertions.assertTrue(afterPay.get().getStatus() == OrderStatusEnum.PAID);
    }

    @Test
    public void createAndDeleteProduct() {
        ProductTO productTO = new ProductTO();
        productTO.setName(HOUSKA);
        productTO.setQuantity(12);
        restTemplate.postForObject(localUri + "/product", productTO, String.class, (Object) null);
        ResponseEntity<ListProductsTO> listProductsTO1 = restTemplate.getForEntity(localUri + "/product/getAll", ListProductsTO.class);
        ListProductsTO listProductsTO1Body = listProductsTO1.getBody();
        System.out.println();
        Assertions.assertTrue(listProductsTO1Body.getListProducts().stream().anyMatch(x -> HOUSKA.equals(x.getName())));
        restTemplate.delete(localUri + "/product?name=" + HOUSKA);
        ResponseEntity<ListProductsTO> listProductsTO2 = restTemplate.getForEntity(localUri + "/product/getAll", ListProductsTO.class);
        ListProductsTO listProductsTO2Body = listProductsTO2.getBody();
        Assertions.assertTrue(listProductsTO2Body.getListProducts().stream().noneMatch(x -> HOUSKA.equals(x.getName())));
    }

    @Test
    public void createOrderAndMakeItUnpaid() {
        Order order = new Order();
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(order);
        orderItem.setProductName("lzice");
        orderItem.setProductQuantity(5);
        order.getOrderItemList().add(orderItem);
        order.setCreateAt(ZonedDateTime.now().minus(31, ChronoUnit.MINUTES));
        order.setStatus(OrderStatusEnum.REGISTRED);
        orderRepository.save(order);

        rohlikService.checkAllUnPaidOrders();
    }

    private OrderTO createOrderTO() {
        OrderTO order = new OrderTO();
        List<OrderItemTO> orderItems = new ArrayList<>();
        OrderItemTO orderItemTO = new OrderItemTO();
        orderItemTO.setName("voda");
        orderItemTO.setQuantity(10);
        orderItems.add(orderItemTO);
        order.setOrderitems(orderItems);
        return order;
    }

}
