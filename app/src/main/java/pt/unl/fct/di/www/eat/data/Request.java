package pt.unl.fct.di.www.eat.data;

import java.util.List;

public class Request {

    String code, payment, eat;
    List<RequestItem> items;
    Double price, time;

    public Request(){

    }

    public Request(String code, Double time, Double price, String payment, String eat, List<RequestItem> items){
        this.code = code;
        this.time = time;
        this.price = price;
        this.payment = payment;
        this.eat = eat;
        this.items = items;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
