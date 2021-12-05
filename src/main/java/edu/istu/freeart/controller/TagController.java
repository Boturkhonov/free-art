package edu.istu.freeart.controller;

import edu.istu.freeart.entity.Tag;
import edu.istu.freeart.repo.TagRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/tags")
public class TagController {
    private final TagRepository tagRepository;

    public TagController(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @GetMapping()
    public List<Tag> getTags() {
        return tagRepository.findAll();
    }
}
