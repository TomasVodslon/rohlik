package cz.root.rohlik.controllers;

import cz.root.rohlik.converters.MainConverter;
import cz.root.rohlik.entity.Product;
import cz.root.rohlik.rest.ProductApi;
import cz.root.rohlik.service.RohlikService;
import cz.root.rohlik.transfer.ListProductsTO;
import cz.root.rohlik.transfer.ProductTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductController implements ProductApi {

    @Autowired
    private RohlikService rohlikService;

    @Override
    public ResponseEntity<Void> productDelete(String name) {
        rohlikService.deleteProduct(name);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ListProductsTO> productGetAllGet() {
        return MainConverter.convertAllProductToTransfer(rohlikService.getAllProducts());
    }

    @Override
    public ResponseEntity<Void> productPost(ProductTO productTO) {
        rohlikService.createProduct(MainConverter.convertProductToDomain(productTO));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
