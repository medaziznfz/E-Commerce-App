package x7030.nefzi.tjinitaw.Model;

public class UserModel {
    private String Name;
    private String Password ;
    private String Phone ;
    private String Address;
    String ResetKey;


    public UserModel() {
    }

    public UserModel(String name, String password, String phone, String address, String resetKey) {
        Name = name;
        Password = password;
        Phone = phone;
        Address = address;
        ResetKey = resetKey;
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

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getResetKey() {
        return ResetKey;
    }

    public void setResetKey(String resetKey) {
        ResetKey = resetKey;
    }
}
