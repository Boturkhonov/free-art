package edu.istu.freeart.controller;

import edu.istu.freeart.entity.Image;
import edu.istu.freeart.repo.ImageRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/images")
public class ImageController {

    private final ImageRepository imageRepository;

    public ImageController(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @GetMapping
    public List<Image> getImages() {
        return imageRepository.findAllByIsActivated(true);
    }

    @GetMapping("{id}")
    public Image getImage(@PathVariable Long id) {
        return imageRepository.findByIdAndIsActivated(id, true).orElse(null);
    }
}
