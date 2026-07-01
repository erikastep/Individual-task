package org.example.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public sealed abstract class Animal permits Dog,Cat,Bird {
    private final AnimalId id;
    private final String name;
    private final int age;
    private AdoptionStatus adoptionStatus;

    protected Animal(AnimalId id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.adoptionStatus = AdoptionStatus.AVAILABLE;
    }

    public void markAsAdopted(){
        this.adoptionStatus = AdoptionStatus.ADOPTED;
    }

    public abstract String getSpecies();
}
