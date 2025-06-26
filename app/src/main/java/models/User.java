package models;

import com.google.firebase.firestore.Exclude;

import java.util.List;
import java.util.UUID;

public class User {
    private String id;
    private String email;
    private String password; // hashed
    private String phone;
    private String name;
    private String gender;
    private String avatar;
    private String role;
    private List<String> address;
    private String otpCode;

    public User() {
    }

    public User(String email, String password, String phone, String name, String gender, String avatar, List<String> address) {
        this.id = UUID.randomUUID().toString();
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.name = name;
        this.gender = gender;
        this.avatar = avatar;
        this.role = "user";
        this.address = address;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public List<String> getAddress() {
        return address;
    }

    public void setAddress(List<String> address) {
        this.address = address;
    }
}
