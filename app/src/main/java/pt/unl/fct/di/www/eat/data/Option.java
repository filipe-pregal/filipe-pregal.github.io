package pt.unl.fct.di.www.eat.data;

public class Option {

    String name;
    Boolean isAvailable;

    public Option(){

    }

    public Option(Boolean isAvailable, String name){
        this.isAvailable = isAvailable;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }
}
