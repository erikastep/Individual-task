package lv.bootcamp.shelter.service;

import lombok.extern.slf4j.Slf4j;
import lv.bootcamp.shelter.model.Animal;
import lv.bootcamp.shelter.service.data.ShelterReportData;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ReportExportService {

    public void writeReport(Path outputPath, ShelterReportData reportData) {
        // Step 4
        try (BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
            writer.write("Shelter Upload Report");
            writer.newLine();
            writer.write("Generated: " + LocalDate.now());
            writer.newLine();
            writer.newLine();

            writer.write("Total imported: " + reportData.importResult().allAnimals().size());
            writer.newLine();
            writer.write("Total skipped: " + reportData.importResult().skippedRows());
            writer.newLine();
            writer.write("Skipped row numbers: " + reportData.importResult().invalidRowNumbers().stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(", ")));
            writer.newLine();
            writer.newLine();

            writer.write("Unique species: " + String.join(", ", reportData.uniqueSpecies()));
            writer.newLine();
            writer.newLine();

            writer.write("Per-species breakdown:");
            writer.newLine();
            for (String species : reportData.uniqueSpecies()) {
                int total = reportData.animalsBySpecies().get(species).size();
                long vaccinated = reportData.vaccinatedCountBySpecies().getOrDefault(species, 0L);
                Animal oldest = reportData.oldestBySpecies().get(species);
                String oldestText = oldest == null
                        ? "unknown"
                        : oldest.getName() + " (" + oldest.getAge() + ")";

                writer.write("- " + species + ": total " + total
                        + ", vaccinated " + vaccinated
                        + ", oldest " + oldestText);
                writer.newLine();
            }
            writer.newLine();

            List<String> needingVet = reportData.animalsNeedingVetInput();
            writer.write("Needs vet input (unknown age): " + String.join(", ", needingVet));
            writer.newLine();

            log.info("Report written to {}", outputPath);
        } catch (IOException e) {
            log.warn("Could not write report to {}", outputPath);
        }
    }
}
