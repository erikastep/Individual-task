package org.example.shelter;

import org.example.model.Animal;

import java.util.ArrayList;
import java.util.List;

public class Shelter <T extends Animal>{
    private final List<T> animals = new ArrayList<>();

    public void addAnimal(T animal){
        animals.add(animal);
    }

    public List<T> getAllAnimals(){
        return animals;
    }

    public List<T> findBySpecies(String species){
        List<T> result = new ArrayList<>();
        for (T animal : animals) {
            if (animal.getSpecies().equalsIgnoreCase(species)) {
                result.add(animal);
            }
        }
        return result;
    }

    public List<T> findAvailableAnimals(){
        List<T> result = new ArrayList<>();
        for (T animal : animals) {
            if (animal.getAdoptionStatus() == org.example.model.AdoptionStatus.AVAILABLE) {
                result.add(animal);
            }
        }
        return result;
    }

    public void markAsAdopted(String id){
        for (T animal : animals) {
            if (animal.getId().toString().equals(id)) {
                animal.markAsAdopted();
                System.out.println(animal.getName() + " has been marked as adopted.");
                return;
            }
        }
        System.out.println("No animal found with that ID.");
    }
}
