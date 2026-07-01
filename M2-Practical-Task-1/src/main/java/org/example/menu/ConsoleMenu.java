package org.example.menu;

import org.example.model.Animal;
import org.example.shelter.Shelter;

import java.util.Scanner;

public class ConsoleMenu {
    private final Shelter<Animal> shelter;
    private final Scanner scanner =  new Scanner(System.in);
    public ConsoleMenu(Shelter<Animal> shelter) {
        this.shelter = shelter;
    }

    public void start(){
        int choice = -1;

        while (choice != 0) {
            printMenu();
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                System.out.print("Enter species (Dog/Cat/Bird): ");
                String species = scanner.nextLine();
                System.out.print("Enter name: ");
                String name = scanner.nextLine();
                System.out.print("Enter age: ");
                int age = scanner.nextInt();
                scanner.nextLine();

                org.example.model.Animal animal;
                if (species.equalsIgnoreCase("Dog")) {
                    animal = new org.example.model.Dog(new org.example.model.AnimalId(), name, age);
                } else if (species.equalsIgnoreCase("Cat")) {
                    animal = new org.example.model.Cat(new org.example.model.AnimalId(), name, age);
                } else if (species.equalsIgnoreCase("Bird")) {
                    animal = new org.example.model.Bird(new org.example.model.AnimalId(), name, age);
                } else {
                    System.out.println("Unknown species. Animal not added.");
                    continue;
                }
                shelter.addAnimal(animal);
                System.out.println(name + " has been added to the shelter.");

            } else if (choice == 2) {
                System.out.println("All animals:");
                for (org.example.model.Animal a : shelter.getAllAnimals()) {
                    System.out.println(a);
                }

            } else if (choice == 3) {
                System.out.print("Enter species to search (Dog/Cat/Bird): ");
                String species = scanner.nextLine();
                System.out.println("Animals of species " + species + ":");
                for (org.example.model.Animal a : shelter.findBySpecies(species)) {
                    System.out.println(a);
                }

            } else if (choice == 4) {
                System.out.println("Available animals:");
                for (org.example.model.Animal a : shelter.findAvailableAnimals()) {
                    System.out.println(a);
                }

            } else if (choice == 5) {
                System.out.print("Enter animal ID to mark as adopted: ");
                String id = scanner.nextLine();
                shelter.markAsAdopted(id);

            } else if (choice == 0) {
                System.out.println("Goodbye!");

            } else {
                System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void printMenu(){
        System.out.println("""
                1. Add animal
                2. List all animals
                3. Find animals by species
                4. List available animals
                5. Mark animal as adopted
                0. Exit
                """);
    }
}
