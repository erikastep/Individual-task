package org.example.model;

public final class Rabbit extends Animal {
    public Rabbit(AnimalId id, String name, int age) {
        super(id, name, age);
    }

    @Override
    public String getSpecies() {
        return "Rabbit";
    }
}
