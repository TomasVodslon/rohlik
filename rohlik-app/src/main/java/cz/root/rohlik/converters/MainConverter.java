package cz.root.rohlik.converters;

import cz.root.rohlik.domain.OrderDomain;
import cz.root.rohlik.domain.OrderItemDomain;
import cz.root.rohlik.entity.Order;
import cz.root.rohlik.entity.OrderItem;
import cz.root.rohlik.entity.Product;
import cz.root.rohlik.transfer.ListProductsTO;
import cz.root.rohlik.transfer.OrderItemTO;
import cz.root.rohlik.transfer.OrderTO;
import cz.root.rohlik.transfer.ProductTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class MainConverter {

    public static OrderDomain convertOrderToDomain(OrderTO source) {
        OrderDomain order = new OrderDomain();

        for (OrderItemTO orderItemTO : source.getOrderitems()) {
            OrderItemDomain orderDomainItem = new OrderItemDomain();
            orderDomainItem.setProductName(orderItemTO.getName());
            orderDomainItem.setProductQuantity(orderItemTO.getQuantity());
            order.getItemDomainList().add(orderDomainItem);
        }
        return order;
    }

    public static Product convertProductToDomain(ProductTO productTO) {
        return new Product(productTO.getName(), productTO.getQuantity());
    }

    public static ResponseEntity<ListProductsTO> convertAllProductToTransfer(List<Product> allProducts) {
        ListProductsTO listProductsTO = new ListProductsTO();

        for (Product product : allProducts) {
            ProductTO productTO = new ProductTO();
            productTO.setName(product.getName());
            productTO.setQuantity(product.getQuantity());
            listProductsTO.addListProductsItem(productTO);
        }
        return new ResponseEntity<>(listProductsTO, HttpStatus.OK);
    }
}
