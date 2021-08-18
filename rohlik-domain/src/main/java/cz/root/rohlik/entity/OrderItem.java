package cz.root.rohlik.entity;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity(name="ORDER_ITEM_TABLE")
public class OrderItem {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "orderId")
    private Order orderId;
    private String productName;
    private int productQuantity;

    public void setOrderId(Order order) {
        this.orderId = order;
    }

    public Order getOrderId() {
        return orderId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }
}
