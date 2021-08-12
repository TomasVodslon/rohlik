package cz.root.rohlik.controllers;

import cz.root.rohlik.rest.OrderApi;
import org.springframework.http.ResponseEntity;

public class OrderController implements OrderApi {

    @Override
    public ResponseEntity<Void> orderDelete() {
        return null;
    }

    @Override
    public ResponseEntity<Void> orderDelete(String id) {
        return null;
    }

    @Override
    public ResponseEntity<Void> orderPost() {
        return null;
    }
}
