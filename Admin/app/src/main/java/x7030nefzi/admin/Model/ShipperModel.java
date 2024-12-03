package x7030nefzi.admin.Model;

public class ShipperModel {
    private String Name;
    private String Password ;
    private String Phone ;
    private Double fees;

    public ShipperModel() {
    }

    public ShipperModel(String name, String password, String phone, Double fees) {
        Name = name;
        Password = password;
        Phone = phone;
        this.fees = fees;
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

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public Double getFees() {
        return fees;
    }

    public void setFees(Double fees) {
        this.fees = fees;
    }
}
