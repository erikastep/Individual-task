package org.example.shelter;

import org.example.model.AdoptionRecord;
import org.example.model.AdoptionStatus;
import org.example.model.Animal;

import java.util.ArrayList;
import java.util.List;

public class Shelter<T extends Animal> {
    private final List<T> animals = new ArrayList<>();
    private final List<AdoptionRecord> adoptionHistory = new ArrayList<>();

    public void addAnimal(T animal) {
        animals.add(animal);
    }

    public List<T> getAllAnimals() {
        return animals;
    }

    public List<T> findBySpecies(String species) {
        List<T> result = new ArrayList<>();
        for (T animal : animals) {
            if (animal.getSpecies().equalsIgnoreCase(species)) {
                result.add(animal);
            }
        }
        return result;
    }

    public List<T> findAvailableAnimals() {
        List<T> result = new ArrayList<>();
        for (T animal : animals) {
            if (animal.getAdoptionStatus() == AdoptionStatus.AVAILABLE) {
                result.add(animal);
            }
        }
        return result;
    }

    public void markAsAdopted(String id, String adopterName) {
        for (T animal : animals) {
            if (animal.getId().toString().equals(id)) {
                animal.markAsAdopted();
                adoptionHistory.add(new AdoptionRecord(animal, adopterName));
                System.out.println(animal.getName() + " has been marked as adopted.");
                return;
            }
        }
        System.out.println("No animal found with that ID.");
    }

    public List<T> sortByAge() {
        List<T> sorted = new ArrayList<>(animals);
        for (int i = 0; i < sorted.size() - 1; i++) {
            for (int j = i + 1; j < sorted.size(); j++) {
                if (sorted.get(i).getAge() > sorted.get(j).getAge()) {
                    T temp = sorted.get(i);
                    sorted.set(i, sorted.get(j));
                    sorted.set(j, temp);
                }
            }
        }
        return sorted;
    }

    public List<T> sortByName() {
        List<T> sorted = new ArrayList<>(animals);
        for (int i = 0; i < sorted.size() - 1; i++) {
            for (int j = i + 1; j < sorted.size(); j++) {
                if (sorted.get(i).getName().compareToIgnoreCase(sorted.get(j).getName()) > 0) {
                    T temp = sorted.get(i);
                    sorted.set(i, sorted.get(j));
                    sorted.set(j, temp);
                }
            }
        }
        return sorted;
    }

    public List<AdoptionRecord> getAdoptionHistory() {
        return adoptionHistory;
    }
}
