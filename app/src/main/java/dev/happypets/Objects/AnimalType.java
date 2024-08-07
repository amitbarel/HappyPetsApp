package dev.happypets.Objects;

public class AnimalType {
    private String kind;
    private int imageSrc;

    public AnimalType() {}
    public AnimalType(String kind, int imageSrc) {
        this.kind = kind;
        this.imageSrc = imageSrc;
    }

    public String getKind() {
        return kind;
    }

    public AnimalType setKind(String kind) {
        this.kind = kind;
        return this;
    }

    public int getImageSrc() {
        return imageSrc;
    }

    public AnimalType setImageSrc(int imageSrc) {
        this.imageSrc = imageSrc;
        return this;
    }
}
