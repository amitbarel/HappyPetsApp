package dev.happypets.Objects;

public class Pet {
    private String name;
    private AnimalType type;
    private String photoUrl;

    // No-argument constructor
    public Pet() {
        // Required for Firebase deserialization
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public Pet setName(String name) {
        this.name = name;
        return this;
    }

    public AnimalType getType() {
        return type;
    }

    public Pet setType(AnimalType type) {
        this.type = type;
        return this;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public Pet setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
        return this;
    }
}
