package pt.unl.fct.di.www.eat.data;

import java.util.List;

public class Request {

    String payment, eat, restaurant;
    List<RequestItem> items;
    Double price, time;

    public Request(){

    }

    public Request(Double time, Double price, String payment, String eat, List<RequestItem> items, String restaurant){
        this.time = time;
        this.price = price;
        this.payment = payment;
        this.eat = eat;
        this.items = items;
        this.restaurant = restaurant;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getEat() {
        return eat;
    }

    public void setEat(String eat) {
        this.eat = eat;
    }

    public List<RequestItem> getItems() {
        return items;
    }

    public void setItems(List<RequestItem> items) {
        this.items = items;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getTime() {
        return time;
    }

    public void setTime(Double time) {
        this.time = time;
    }
}
