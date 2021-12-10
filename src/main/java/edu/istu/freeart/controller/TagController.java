package edu.istu.freeart.controller;

import edu.istu.freeart.entity.Tag;
import edu.istu.freeart.repo.TagRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/tags")
public class TagController {

    private final TagRepository tagRepository;

    public TagController(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @GetMapping
    public List<Tag> getTags() {
        return tagRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> postTag(@RequestBody Tag tag) {
        tag.setTag(tag.getTag().toLowerCase(Locale.ROOT));
        final Optional<Tag> optional = tagRepository.findByTag(tag.getTag());
        return ResponseEntity.ok(optional.orElseGet(() -> tagRepository.save(tag)));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteTag(@RequestBody Map<String, Long> request) {
        Long id = request.get("id");
        try {
            tagRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
