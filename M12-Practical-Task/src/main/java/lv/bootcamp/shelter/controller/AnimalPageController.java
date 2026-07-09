package lv.bootcamp.shelter.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lv.bootcamp.shelter.dto.AnimalResponse;
import lv.bootcamp.shelter.form.AnimalForm;
import lv.bootcamp.shelter.model.AnimalType;
import lv.bootcamp.shelter.service.AnimalService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Serves the Thymeleaf pages (separate from AnimalApiController)
@Controller
@RequiredArgsConstructor
public class AnimalPageController {

    private final AnimalService animalService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    // Bonus H
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/animals")
    public String listAnimals(Model model) {
        model.addAttribute("animals", animalService.findAll());
        return "animals";
    }

    // Bonus A
    @GetMapping("/animals/{id}")
    public String animalDetail(@PathVariable Long id, Model model) {
        AnimalResponse animal = animalService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Animal not found"));
        model.addAttribute("animal", animal);
        return "animal";
    }

    @GetMapping("/animals/new")
    public String newAnimalForm(Model model) {
        model.addAttribute("form", new AnimalForm(null, null, null, null, null, null));
        model.addAttribute("types", AnimalType.values());
        return "animals-new";
    }

    @PostMapping("/animals")
    public String createAnimal(@Valid @ModelAttribute("form") AnimalForm form,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttrs) {
        // Bonus B
        if (result.hasErrors()) {
            model.addAttribute("types", AnimalType.values());
            return "animals-new";
        }
        animalService.createFromForm(form);
        // Bonus C
        redirectAttrs.addFlashAttribute("message", "Animal added!");
        return "redirect:/animals";
    }
}
