package cz.root.rohlik.controllers;

import cz.root.rohlik.rest.PaymentApi;
import cz.root.rohlik.service.RohlikService;
import cz.root.rohlik.transfer.PaymentTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentController implements PaymentApi {

    @Autowired
    private RohlikService orderService;

    @Override
    public ResponseEntity<Void> paymentPost(PaymentTO body) {
        orderService.paymentOrder(body.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
