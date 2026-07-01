package org.example.model;

import java.time.LocalDate;

public class AdoptionRecord {
    private final Animal animal;
    private final String adopterName;
    private final LocalDate adoptionDate;

    public AdoptionRecord(Animal animal, String adopterName) {
        this.animal = animal;
        this.adopterName = adopterName;
        this.adoptionDate = LocalDate.now();
    }

    @Override
    public String toString() {
        return animal.getName() + " | Adopted by: " + adopterName + " | Date: " + adoptionDate;
    }
}
