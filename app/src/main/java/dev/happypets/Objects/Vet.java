package dev.happypets.Objects;

public class Vet {
    private String name;
    private String email;
    private String phone;
    private String address;
    private String password;
    private String licenseNumber;
    private String licenseDocumentUrl;

    // Default constructor required for calls to DataSnapshot.getValue(Vet.class)
    public Vet() {
    }

    public Vet(String name, String email, String phone, String address, String password, String licenseNumber, String licenseDocumentUrl) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.password = password;
        this.licenseNumber = licenseNumber;
        this.licenseDocumentUrl = licenseDocumentUrl;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public Vet setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Vet setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public Vet setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public Vet setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Vet setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public Vet setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
        return this;
    }

    public String getLicenseDocumentUrl() {
        return licenseDocumentUrl;
    }

    public Vet setLicenseDocumentUrl(String licenseDocumentUrl) {
        this.licenseDocumentUrl = licenseDocumentUrl;
        return this;
    }
}
