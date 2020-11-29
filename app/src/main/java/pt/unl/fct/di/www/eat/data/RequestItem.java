package pt.unl.fct.di.www.eat.data;

public class RequestItem {

    String item;
    Double quantity;

    public RequestItem(){

    }

    public RequestItem(String item, Double quantity){
        this.item = item;
        this.quantity = quantity;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public void aux(){
        this.quantity+=1.0;
    }
}
