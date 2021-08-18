package cz.root.rohlik.domain;

import java.util.ArrayList;
import java.util.List;

public class OrderDomain {

    private List<OrderItemDomain> itemDomainList = new ArrayList<>();

    public List<OrderItemDomain> getItemDomainList() {
        return itemDomainList;
    }
}
