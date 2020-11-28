package pt.unl.fct.di.www.eat.data;

public class Option {

    String name;
    boolean isAvailable;

    public Option() {

    }

    public Option(boolean isAvailable, String name) {
        this.isAvailable = isAvailable;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(boolean available) {
        this.isAvailable = available;
    }
}
