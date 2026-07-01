package org.example.shelter;

import org.example.model.Animal;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ShelterUtils {

    public static double averageAge(List<? extends Animal> animals) {
        if (animals.isEmpty()) return 0;
        int total = 0;
        for (Animal a : animals) {
            total += a.getAge();
        }
        return (double) total / animals.size();
    }

    public static Animal oldestAnimal(List<? extends Animal> animals) {
        if (animals.isEmpty()) return null;
        Animal oldest = animals.get(0);
        for (Animal a : animals) {
            if (a.getAge() > oldest.getAge()) {
                oldest = a;
            }
        }
        return oldest;
    }

    public static Map<String, Integer> countBySpecies(List<? extends Animal> animals) {
        Map<String, Integer> counts = new HashMap<>();
        for (Animal a : animals) {
            String species = a.getSpecies();
            if (counts.containsKey(species)) {
                counts.put(species, counts.get(species) + 1);
            } else {
                counts.put(species, 1);
            }
        }
        return counts;
    }
}
