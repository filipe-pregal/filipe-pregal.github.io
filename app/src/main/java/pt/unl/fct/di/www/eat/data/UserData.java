package pt.unl.fct.di.www.eat.data;

public class UserData {
    String Email, Name, Password, Role;

    public UserData(){

    }

    public UserData(String Email, String Name, String Password){
        this.Email = Email;
        this.Name = Name;
        this.Password = Password;
        this.Role = "USER";
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
