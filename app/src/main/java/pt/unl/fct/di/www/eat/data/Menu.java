package pt.unl.fct.di.www.eat.data;

public class Menu {

    boolean isAvailable;
    Double price;
    String name;
    String tag;
    Double time;
    String image_url;

    public Menu() {

    }

    public Menu(boolean isAvailable, Double price, String name, String tag, Double time, String image_url) {
        this.isAvailable = isAvailable;
        this.price = price;
        this.name = name;
        this.tag = tag;
        this.time = time;
        this.image_url = image_url;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean available) {
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

    @Override
    public String toString() {
        return "isAvailable: " + isAvailable + " price: " + price + " name: " + name + " tag: " + tag + " time: " + time;
    }
}
