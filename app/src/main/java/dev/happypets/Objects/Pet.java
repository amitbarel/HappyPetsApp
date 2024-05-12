package dev.happypets.Objects;

public class Pet {

    private String name;
    private AnimalType type;

    public Pet(String name, AnimalType type) {
        this.name = name;
        this.type = type;
    }

    public Pet() {
    }

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
}
