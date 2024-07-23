package dev.happypets.Objects;

import java.util.ArrayList;

public class User {
    private String name;
    private String email;
    private String password;
    private ArrayList<Pet> pets;
    private ArrayList<Question> favoriteQuestions;

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User() {
    }

    public User(String name, String email, String password, Pet pet) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.pets = new ArrayList<>();
        this.pets.add(pet);
        this.favoriteQuestions = new ArrayList<>();

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

    public User setPets(ArrayList<Pet> pets) {
        this.pets = pets;
        return this;
    }

    public void addPet(Pet pet) {
        this.pets.add(pet);
    }

    public ArrayList<Pet> getPets() {
        return pets;
    }

    public ArrayList<Question> getFavoriteQuestions() {
        return favoriteQuestions;
    }

    public void addToFavoriteQuestions(Question question) {
        this.favoriteQuestions.add(question);
    }
}
