package lv.bootcamp.shelter;

import lv.bootcamp.shelter.client.NotificationClient;
import lv.bootcamp.shelter.dto.AdoptionRequest;
import lv.bootcamp.shelter.dto.AnimalCreateRequest;
import lv.bootcamp.shelter.dto.AnimalResponse;
import lv.bootcamp.shelter.model.AnimalStatus;
import lv.bootcamp.shelter.model.AnimalType;
import lv.bootcamp.shelter.service.AnimalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;


@SpringBootTest
@Transactional
class AdoptionIntegrationTest {

    @Autowired
    private AnimalService animalService;

    @MockitoBean
    private NotificationClient notificationClient;

    @Test
    void adoptionFlow_shouldPersistStatusAndNotifyExternalSystem() {
        AnimalCreateRequest createReq =
                new AnimalCreateRequest("Rex", AnimalType.DOG, "Labrador", 3, "Friendly");
        AnimalResponse created = animalService.create(createReq);
        assertThat(created.status()).isEqualTo(AnimalStatus.AVAILABLE);

        AdoptionRequest adoptReq =
                new AdoptionRequest(created.id(), "Anna", "anna@example.com");
        AnimalResponse adopted = animalService.adopt(adoptReq);

        assertThat(adopted.status()).isEqualTo(AnimalStatus.ADOPTED);

        verify(notificationClient)
                .sendAdoptionNotification(created.id(), "Rex", "anna@example.com");

        AnimalResponse refetched = animalService.findById(created.id());
        assertThat(refetched.status()).isEqualTo(AnimalStatus.ADOPTED);
    }
}
