package sample;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Cart implements Serializable {

    int id;
    Map<String, Integer> cart;

    public Cart(int id){
        this.id = id;
        this.cart = new HashMap<>();
    }

    public void addToCart(CartItem ci){
        this.cart.put(ci.name, ci.quantity);
    }

    public void removeFromCart(CartItem ci){
        this.cart.remove(ci.name);
    }

}
