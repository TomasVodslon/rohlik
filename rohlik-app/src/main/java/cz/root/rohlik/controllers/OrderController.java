package cz.root.rohlik.controllers;

import cz.root.rohlik.converters.MainConverter;
import cz.root.rohlik.rest.OrderApi;
import cz.root.rohlik.service.RohlikService;
import cz.root.rohlik.transfer.OrderItemTO;
import cz.root.rohlik.transfer.OrderTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController implements OrderApi {

    @Autowired
    private RohlikService orderService;

    @Override
    public ResponseEntity<String> createOrder(OrderTO order) {

        // keep validate at another transaction
        for (OrderItemTO orderitem : order.getOrderitems()) {
            orderService.validateNumberOfQuantity(MainConverter.convertOrderItemToDomain(orderitem));
        }

        Long newOrderId = orderService.createOrder(MainConverter.convertOrderToDomain(order));
        return new ResponseEntity<>(String.valueOf(newOrderId), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteOrder(Long id) {
        orderService.deleteOrder(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
