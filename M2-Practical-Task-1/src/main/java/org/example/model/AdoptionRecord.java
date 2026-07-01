package org.example.model;

import lombok.Getter;
import java.time.LocalDate;

@Getter
public class AdoptionRecord {
    private final Animal animal;
    private final String adopterName;
    private final LocalDate adoptionDate;

    public AdoptionRecord(Animal animal, String adopterName) {
        this.animal = animal;
        this.adopterName = adopterName;
        this.adoptionDate = LocalDate.now();
    }

    public Animal getAnimal() { return animal; }
    public String getAdopterName() { return adopterName; }
    public LocalDate getAdoptionDate() { return adoptionDate; }

    @Override
    public String toString() {
        return animal.getName() + " | Adopted by: " + adopterName + " | Date: " + adoptionDate;
    }
}
