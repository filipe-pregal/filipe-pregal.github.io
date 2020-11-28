package pt.unl.fct.di.www.eat.data;

public class Cart {

    String name, drink, dessert;
    Double price, time;

    public Cart(){

    }

    public Cart(String name, String drink, String dessert, Double price, Double time){
        this.name = name;
        this.drink = drink;
        this.dessert = dessert;
        this.price = price;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDrink() {
        return drink;
    }

    public void setDrink(String drink) {
        this.drink = drink;
    }

    public String getDessert() {
        return dessert;
    }

    public void setDessert(String dessert) {
        this.dessert = dessert;
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

    @Override
    public String toString(){
        return "name: " + name + " drink: " + drink + " dessert: " + dessert + " price: " + price + " time: " + time;
    }
}
