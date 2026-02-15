package ru.kuzya.orderservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kuzya.orderservice.entity.Person;
import ru.kuzya.orderservice.service.PersonService;

@RestController
@RequestMapping(value = "api/v1/person")
@RequiredArgsConstructor
@Slf4j
public class PersonController {

    private final PersonService personService;

    // GET: http://localhost:8080/api/v1/person/get?id=1
    @GetMapping("/get")
    public ResponseEntity<Person> getPerson(@RequestParam Long id) {
        Person person = personService.getPerson(id);
        if (person != null) {
            log.info("Person found: {}", person);
            return new ResponseEntity<>(person, HttpStatus.OK);
        } else {
            log.warn("Person with id {} not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // GET: http://localhost:8080/api/v1/person/get?id=1
    @GetMapping("/load")
    public ResponseEntity<Person> loadPerson(@RequestParam Long id) {

        Person person = personService.loadedPerson(id);
        if (person != null) {
            log.info("Person found: {}", person);
            return new ResponseEntity<>(person, HttpStatus.OK);
        } else {
            log.warn("Person with id {} not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // GET: http://localhost:8080/api/v1/person/get?id=1
    @GetMapping("/find")
    public ResponseEntity<Person> findPerson(@RequestParam Long id) {
        Person person = personService.findPerson(id);
        if (person != null) {
            log.info("Person found: {}", person);
            return new ResponseEntity<>(person, HttpStatus.OK);
        } else {
            log.warn("Person with id {} not found", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // POST: http://localhost:8080/api/v1/person/save
    @PostMapping("/save")
    public ResponseEntity<Person> savePerson(@RequestBody Person person) {
        try {
            personService.savePerson(person);
            log.info("Person saved: {}", person);
            return new ResponseEntity<>(person, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error saving person: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DELETE: http://localhost:8080/api/v1/person/delete/1
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        try {
            personService.deletePerson(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.error("Error deleting person: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Пример для тестирования POST (можно через Postman)
    @PostMapping("/test")
    public ResponseEntity<Person> testSave() {
        Person person = Person.builder()
                .name("Tom")
                .surname("Goga")
                .age(55)
                .email("45@rt.tu")
                .build();
        return savePerson(person);
    }
}