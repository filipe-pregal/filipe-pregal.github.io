package pt.unl.fct.di.www.eat.data;

public class Menu {

    Boolean isAvailable;
    Double price;
    String name;
    String tag;
    Double time;

    public Menu(){

    }

    public Menu(Boolean isAvailable, Double price, String name, String tag, Double time){
        this.isAvailable=isAvailable;
        this.price = price;
        this.name = name;
        this.tag = tag;
        this.time = time;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Double getTime() {
        return time;
    }

    public void setTime(Double time) {
        this.time = time;
    }
}
