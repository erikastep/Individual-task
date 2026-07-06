package lv.bootcamp.shelter.service;

import lombok.extern.slf4j.Slf4j;
import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ImportResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CsvImportService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public ImportResult importAnimals(Path inputPath) {
        log.info("Starting import from {}", inputPath);

        List<Animal> allAnimals = new ArrayList<>();
        List<Integer> invalidRowNumbers = new ArrayList<>();

        // Step 1
        List<String> lines;
        try {
            lines = Files.readAllLines(inputPath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.warn("Could not read file {}", inputPath);
            return new ImportResult(allAnimals, 0, invalidRowNumbers);
        }

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            int rowNumber = i + 1;

            if (line.isBlank()) {
                continue;
            }

            Animal animal = parseRow(line, rowNumber);
            if (animal == null) {
                invalidRowNumbers.add(rowNumber);
            } else {
                allAnimals.add(animal);
            }
        }

        log.info("Imported {} animals, skipped {} rows", allAnimals.size(), invalidRowNumbers.size());
        return new ImportResult(allAnimals, invalidRowNumbers.size(), invalidRowNumbers);
    }

    private Animal parseRow(String line, int rowNumber) {
        String[] parts = line.split(",", -1);

        if (parts.length != 5) {
            log.warn("Row {} skipped: expected 5 columns but found {}", rowNumber, parts.length);
            return null;
        }

        String name = parts[0].trim();
        String species = parts[1].trim();
        String ageText = parts[2].trim();
        String vaccinatedText = parts[3].trim();
        String dateText = parts[4].trim();

        if (name.isEmpty() || species.isEmpty()) {
            log.warn("Row {} skipped: name and species are required", rowNumber);
            return null;
        }

        Integer age = null;
        if (!ageText.isEmpty()) {
            try {
                age = Integer.parseInt(ageText);
            } catch (NumberFormatException e) {
                log.warn("Row {} skipped: age '{}' is not a number", rowNumber, ageText);
                return null;
            }
            if (age <= 0) {
                log.warn("Row {} skipped: age '{}' must be positive", rowNumber, ageText);
                return null;
            }
        }

        if (!vaccinatedText.equalsIgnoreCase("true") && !vaccinatedText.equalsIgnoreCase("false")) {
            log.warn("Row {} skipped: vaccinated '{}' must be true or false", rowNumber, vaccinatedText);
            return null;
        }
        boolean vaccinated = Boolean.parseBoolean(vaccinatedText);

        LocalDate intakeDate;
        try {
            intakeDate = LocalDate.parse(dateText, DATE_FORMAT);
        } catch (DateTimeParseException e) {
            log.warn("Row {} skipped: date '{}' is not valid (expected dd.MM.yyyy)", rowNumber, dateText);
            return null;
        }

        return new Animal(name, species, age, vaccinated, intakeDate);
    }
}
