package org.example.menu;

import org.example.model.*;
import org.example.shelter.Shelter;
import org.example.shelter.ShelterUtils;

import java.util.Scanner;

public class ConsoleMenu {
    private final Shelter<Animal> shelter;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleMenu(Shelter<Animal> shelter) {
        this.shelter = shelter;
    }

    public void start() {
        int choice = -1;

        while (choice != 0) {
            printMenu();
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                System.out.print("Enter species (Dog/Cat/Bird/Rabbit): ");
                String species = scanner.nextLine();

                System.out.print("Enter name: ");
                String name = scanner.nextLine();
                if (name.isEmpty()) {
                    System.out.println("Name cannot be empty.");
                    continue;
                }

                System.out.print("Enter age: ");
                int age = scanner.nextInt();
                scanner.nextLine();
                if (age < 0) {
                    System.out.println("Age cannot be negative.");
                    continue;
                }

                Animal animal;
                if (species.equalsIgnoreCase("Dog")) {
                    animal = new Dog(new AnimalId(), name, age);
                } else if (species.equalsIgnoreCase("Cat")) {
                    animal = new Cat(new AnimalId(), name, age);
                } else if (species.equalsIgnoreCase("Bird")) {
                    animal = new Bird(new AnimalId(), name, age);
                } else if (species.equalsIgnoreCase("Rabbit")) {
                    animal = new Rabbit(new AnimalId(), name, age);
                } else {
                    System.out.println("Unknown species. Animal not added.");
                    continue;
                }
                shelter.addAnimal(animal);
                System.out.println(name + " has been added to the shelter.");

            } else if (choice == 2) {
                System.out.println("All animals:");
                for (Animal a : shelter.getAllAnimals()) {
                    System.out.println(a);
                }

            } else if (choice == 3) {
                System.out.print("Enter species to search (Dog/Cat/Bird/Rabbit): ");
                String species = scanner.nextLine();
                System.out.println("Animals of species " + species + ":");
                for (Animal a : shelter.findBySpecies(species)) {
                    System.out.println(a);
                }

            } else if (choice == 4) {
                System.out.println("Available animals:");
                for (Animal a : shelter.findAvailableAnimals()) {
                    System.out.println(a);
                }

            } else if (choice == 5) {
                System.out.print("Enter animal ID to mark as adopted: ");
                String id = scanner.nextLine();
                System.out.print("Enter adopter name: ");
                String adopterName = scanner.nextLine();
                shelter.markAsAdopted(id, adopterName);

            } else if (choice == 6) {
                System.out.println("Sort by: 1. Age  2. Name");
                int sortChoice = scanner.nextInt();
                scanner.nextLine();
                if (sortChoice == 1) {
                    for (Animal a : shelter.sortByAge()) {
                        System.out.println(a);
                    }
                } else {
                    for (Animal a : shelter.sortByName()) {
                        System.out.println(a);
                    }
                }

            } else if (choice == 7) {
                System.out.println("--- Shelter Statistics ---");
                System.out.println("Average age: " + ShelterUtils.averageAge(shelter.getAllAnimals()));
                Animal oldest = ShelterUtils.oldestAnimal(shelter.getAllAnimals());
                if (oldest != null) {
                    System.out.println("Oldest animal: " + oldest.getName() + " (" + oldest.getAge() + " years)");
                }
                System.out.println("Count by species:");
                for (var entry : ShelterUtils.countBySpecies(shelter.getAllAnimals()).entrySet()) {
                    System.out.println("  " + entry.getKey() + ": " + entry.getValue());
                }

            } else if (choice == 8) {
                System.out.println("--- Adoption History ---");
                if (shelter.getAdoptionHistory().isEmpty()) {
                    System.out.println("No adoptions yet.");
                } else {
                    for (var record : shelter.getAdoptionHistory()) {
                        System.out.println(record);
                    }
                }

            } else if (choice == 0) {
                System.out.println("Goodbye!");

            } else {
                System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void printMenu() {
        System.out.println("""
                1. Add animal
                2. List all animals
                3. Find animals by species
                4. List available animals
                5. Mark animal as adopted
                6. Sort animals
                7. Shelter statistics
                8. Adoption history
                0. Exit
                """);
    }
}
