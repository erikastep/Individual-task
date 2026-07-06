package lv.bootcamp.shelter.service;

import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ImportResult;
import lv.bootcamp.shelter.service.data.ShelterReportData;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ShelterAnalyticsService {

    public ShelterReportData buildReportData(ImportResult importResult) {
        List<Animal> allAnimals = importResult.allAnimals();

        // Step 2
        Set<String> uniqueSpecies = allAnimals.stream()
                .map(Animal::getSpecies)
                .collect(Collectors.toCollection(TreeSet::new));

        Map<String, List<Animal>> animalsBySpecies = allAnimals.stream()
                .collect(Collectors.groupingBy(Animal::getSpecies, TreeMap::new, Collectors.toList()));

        List<String> animalsNeedingVetInput = allAnimals.stream()
                .filter(a -> a.getAgeOptional().isEmpty())
                .map(a -> a.getName() + "(" + a.getSpecies() + ")")
                .collect(Collectors.toList());

        // Step 3
        Map<String, Long> vaccinatedCountBySpecies = allAnimals.stream()
                .filter(Animal::isVaccinated)
                .collect(Collectors.groupingBy(Animal::getSpecies, TreeMap::new, Collectors.counting()));

        Map<String, Animal> oldestBySpecies = allAnimals.stream()
                .filter(a -> a.getAgeOptional().isPresent())
                .collect(Collectors.groupingBy(
                        Animal::getSpecies,
                        TreeMap::new,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparingInt(Animal::getAge)),
                                opt -> opt.orElse(null))));

        return new ShelterReportData(
                importResult,
                uniqueSpecies,
                animalsBySpecies,
                animalsNeedingVetInput,
                vaccinatedCountBySpecies,
                oldestBySpecies);
    }
}
