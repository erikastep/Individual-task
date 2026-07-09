package lv.bootcamp.shelter.service;

import lv.bootcamp.shelter.audit.AuditLogger;
import lv.bootcamp.shelter.audit.RejectionReason;
import lv.bootcamp.shelter.client.NotificationClient;
import lv.bootcamp.shelter.model.*;
import lv.bootcamp.shelter.repository.AdopterRepository;
import lv.bootcamp.shelter.repository.AnimalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Write tests for AdoptionEligibilityService.
 * The class and mocks are set up — the rest is yours.
 */
@ExtendWith(MockitoExtension.class)
class AdoptionEligibilityServiceTest {

    @Mock
    private AdopterRepository adopterRepository;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private NotificationClient notificationClient;

    @Mock
    private AuditLogger auditLogger;

    @InjectMocks
    private AdoptionEligibilityService service;


    // test fixtures
    private Adopter validAdopter() {
        return new Adopter(1L, "Anna", "anna@mail.com", 30, 0, 0, false, false);
    }

    private Animal availableAnimal() {
        return new Animal(2L, "Rex", AnimalType.DOG, "Lab", 3, "Friendly", AnimalStatus.AVAILABLE);
    }


    // my tests
    // adopter not found
    @Test
    void evaluateAdoption_rejectsWhenAdopterNotFound() {
        when(adopterRepository.findById(1L)).thenReturn(Optional.empty());

        AdoptionResult result = service.evaluateAdoption(1L, 2L);

        assertThat(result.approved()).isFalse();
        assertThat(result.reason()).isEqualTo(RejectionReasons.ADOPTER_NOT_FOUND);
        verifyNoInteractions(animalRepository, auditLogger, notificationClient);
    }

    // animal not found
    @Test
    void evaluateAdoption_rejectsWhenAnimalNotFound() {
        Adopter adopter = validAdopter();
        when(adopterRepository.findById(1L)).thenReturn(Optional.of(adopter));
        when(animalRepository.findById(2L)).thenReturn(Optional.empty());

        AdoptionResult result = service.evaluateAdoption(1L, 2L);

        assertThat(result.approved()).isFalse();
        assertThat(result.reason()).isEqualTo(RejectionReasons.ANIMAL_NOT_FOUND);
        verifyNoInteractions(auditLogger, notificationClient);
    }

    // animal not available
    @Test
    void evaluateAdoption_rejectsWhenAnimalNotAvailable() {
        Adopter adopter = validAdopter();
        Animal animal = availableAnimal();
        animal.setStatus(AnimalStatus.RESERVED);
        when(adopterRepository.findById(1L)).thenReturn(Optional.of(adopter));
        when(animalRepository.findById(2L)).thenReturn(Optional.of(animal));

        AdoptionResult result = service.evaluateAdoption(1L, 2L);

        assertThat(result.approved()).isFalse();
        assertThat(result.reason()).isEqualTo(RejectionReasons.ANIMAL_NOT_AVAILABLE);
        verifyNoInteractions(auditLogger, notificationClient);
    }

    // adopter underage
    @Test
    void evaluateAdoption_rejectsUnderageAdopter() {
        Adopter adopter = validAdopter();
        adopter.setAge(17);
        Animal animal = availableAnimal();
        when(adopterRepository.findById(1L)).thenReturn(Optional.of(adopter));
        when(animalRepository.findById(2L)).thenReturn(Optional.of(animal));

        AdoptionResult result = service.evaluateAdoption(1L, 2L);

        assertThat(result.approved()).isFalse();
        assertThat(result.reason()).isEqualTo(RejectionReasons.UNDERAGE);
        verify(auditLogger).logRejection(1L, 2L, RejectionReason.UNDERAGE);
        verifyNoInteractions(notificationClient);
    }

    // pet limit reached (normal - 3)
    @Test
    void evaluateAdoption_rejectsWhenPetLimitReached() {
        Adopter adopter = validAdopter();
        adopter.setCurrentPetCount(3);
        when(adopterRepository.findById(1L)).thenReturn(Optional.of(adopter));
        when(animalRepository.findById(2L)).thenReturn(Optional.of(availableAnimal()));

        AdoptionResult result = service.evaluateAdoption(1L, 2L);

        assertThat(result.approved()).isFalse();
        assertThat(result.reason()).isEqualTo(RejectionReasons.PET_LIMIT_REACHED);
        verify(auditLogger).logRejection(1L, 2L, RejectionReason.PET_LIMIT_REACHED);
        verifyNoInteractions(notificationClient);
    }

    // per limit reached (large - 5)
    @Test
    void evaluateAdoption_rejectsWhenLargePropertyPetLimitReached() {
        Adopter adopter = validAdopter();
        adopter.setLargeProperty(true);
        adopter.setCurrentPetCount(5);
        when(adopterRepository.findById(1L)).thenReturn(Optional.of(adopter));
        when(animalRepository.findById(2L)).thenReturn(Optional.of(availableAnimal()));

        AdoptionResult result = service.evaluateAdoption(1L, 2L);

        assertThat(result.approved()).isFalse();
        assertThat(result.reason()).isEqualTo(RejectionReasons.PET_LIMIT_REACHED);
        verify(auditLogger).logRejection(1L, 2L, RejectionReason.PET_LIMIT_REACHED);
        verifyNoInteractions(notificationClient);
    }

    //exotic animal without permit
    @Test
    void evaluateAdoption_rejectsExoticAnimalWithoutPermit() {
        Adopter adopter = validAdopter();
        Animal animal = availableAnimal();
        animal.setType(AnimalType.BIRD);
        when(adopterRepository.findById(1L)).thenReturn(Optional.of(adopter));
        when(animalRepository.findById(2L)).thenReturn(Optional.of(animal));

        AdoptionResult result = service.evaluateAdoption(1L, 2L);

        assertThat(result.approved()).isFalse();
        assertThat(result.reason()).isEqualTo(RejectionReasons.EXOTIC_PERMIT_REQUIRED);
        verify(auditLogger).logRejection(1L, 2L, RejectionReason.NO_EXOTIC_PERMIT);
        verifyNoInteractions(notificationClient);
    }

    // eligible adopter
    @Test
    void evaluateAdoption_approvesEligibleAdopter() {
        Adopter adopter = validAdopter();
        Animal animal = availableAnimal();
        when(adopterRepository.findById(1L)).thenReturn(Optional.of(adopter));
        when(animalRepository.findById(2L)).thenReturn(Optional.of(animal));

        AdoptionResult result = service.evaluateAdoption(1L, 2L);

        assertThat(result.approved()).isTrue();
        assertThat(result.reason()).isEqualTo("Approved");
        assertThat(result.priorityScore()).isEqualTo(0);
        verify(auditLogger).logApproval(1L, 2L, 0);
        verify(notificationClient).sendApprovalNotification("anna@mail.com", "Rex");
    }

    // exotic animal with permit
    @Test
    void evaluateAdoption_approvesExoticAnimalWithPermit() {
        Adopter adopter = validAdopter();
        adopter.setExoticPermit(true);
        Animal animal = availableAnimal();
        animal.setType(AnimalType.BIRD);
        when(adopterRepository.findById(1L)).thenReturn(Optional.of(adopter));
        when(animalRepository.findById(2L)).thenReturn(Optional.of(animal));

        AdoptionResult result = service.evaluateAdoption(1L, 2L);

        assertThat(result.approved()).isTrue();
        assertThat(result.priorityScore()).isEqualTo(0);
        verify(auditLogger).logApproval(1L, 2L, 0);
        verify(notificationClient).sendApprovalNotification("anna@mail.com", "Rex");
    }

    @Test
    void score_isZeroForBaselineAdopter() {
        Adopter adopter = validAdopter();
        Animal animal = availableAnimal();

        int score = service.calculatePriorityScore(adopter, animal);

        assertThat(score).isEqualTo(0);
    }

    @Test
    void score_addsTenForPreviousAdoptions() {
        Adopter adopter = validAdopter();
        adopter.setPreviousAdoptions(1);

        int score = service.calculatePriorityScore(adopter, availableAnimal());

        assertThat(score).isEqualTo(10);
    }

    @Test
    void score_doesNotAddBonusAtExactlyThreeAdoptions() {
        Adopter adopter = validAdopter();
        adopter.setPreviousAdoptions(3);

        int score = service.calculatePriorityScore(adopter, availableAnimal());

        assertThat(score).isEqualTo(10);
    }

    @Test
    void score_addsBonusForMoreThanThreeAdoptions() {
        Adopter adopter = validAdopter();
        adopter.setPreviousAdoptions(4);

        int score = service.calculatePriorityScore(adopter, availableAnimal());

        assertThat(score).isEqualTo(15);
    }

    @Test
    void score_addsFifteenForLargeProperty() {
        Adopter adopter = validAdopter();
        adopter.setLargeProperty(true);

        int score = service.calculatePriorityScore(adopter, availableAnimal());

        assertThat(score).isEqualTo(15);
    }

    @Test
    void score_addsTwentyForSeniorAnimal() {
        Animal animal = availableAnimal();
        animal.setAge(8);

        int score = service.calculatePriorityScore(validAdopter(), animal);

        assertThat(score).isEqualTo(20);
    }

    @Test
    void score_doesNotAddSeniorBonusAtExactlyAgeSeven() {
        Animal animal = availableAnimal();
        animal.setAge(7);

        int score = service.calculatePriorityScore(validAdopter(), animal);

        assertThat(score).isEqualTo(0);
    }

    @Test
    void score_subtractsTwoPerCurrentPet() {
        Adopter adopter = validAdopter();
        adopter.setPreviousAdoptions(1);
        adopter.setCurrentPetCount(3);

        int score = service.calculatePriorityScore(adopter, availableAnimal());

        assertThat(score).isEqualTo(4);
    }

    @Test
    void score_neverGoesBelowZero() {
        Adopter adopter = validAdopter();
        adopter.setCurrentPetCount(5);

        int score = service.calculatePriorityScore(adopter, availableAnimal());

        assertThat(score).isEqualTo(0);
    }

    @Test
    void score_treatsNullAnimalAgeAsNotSenior() {
        Animal animal = availableAnimal();
        animal.setAge(null);

        int score = service.calculatePriorityScore(validAdopter(), animal);

        assertThat(score).isEqualTo(0);
    }
}
