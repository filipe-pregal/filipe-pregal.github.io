package pt.unl.fct.di.www.eat.data;

import android.graphics.Path;

import java.util.Map;

public class RestaurantData {

    Map<String, Option> desserts;
    Map<String, Option> drinks;
    Map<String, Menu> menu;
    String address, name, tag, email, time;

    public RestaurantData(){

    }

    public RestaurantData(Map<String, Option> desserts, String address, Map<String, Option> drinks, String name, String tag, Map<String, Menu> menu, String email, String time){
        this.desserts = desserts;
        this.address = address;
        this.drinks = drinks;
        this.name = name;
        this.tag = tag;
        this.menu = menu;
        this.email = email;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Map<String, Option> getDesserts() {
        return desserts;
    }

    public void setDesserts(Map<String, Option> desserts) {
        this.desserts = desserts;
    }

    public Map<String, Option> getDrinks() {
        return drinks;
    }

    public void setDrinks(Map<String, Option> drinks) {
        this.drinks = drinks;
    }

    public Map<String, Menu> getMenu() {
        return menu;
    }

    public void setMenu(Map<String, Menu> menu) {
        this.menu = menu;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

