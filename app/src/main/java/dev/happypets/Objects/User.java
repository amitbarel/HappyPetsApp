package dev.happypets.Objects;

public class User {
    private String name;
    private String email;
    private String password;
    private String petName;
    private String petType;
    private String petPhotoUrl;

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String name, String email, String password, String petName, String petType, String petPhotoUrl) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.petName = petName;
        this.petType = petType;
        this.petPhotoUrl = petPhotoUrl;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getPetName() {
        return petName;
    }

    public User setPetName(String petName) {
        this.petName = petName;
        return this;
    }

    public String getPetType() {
        return petType;
    }

    public User setPetType(String petType) {
        this.petType = petType;
        return this;
    }

    public String getPetPhotoUrl() {
        return petPhotoUrl;
    }

    public User setPetPhotoUrl(String petPhotoUrl) {
        this.petPhotoUrl = petPhotoUrl;
        return this;
    }
}
