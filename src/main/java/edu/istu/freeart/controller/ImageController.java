package edu.istu.freeart.controller;

import edu.istu.freeart.entity.Auction;
import edu.istu.freeart.entity.Image;
import edu.istu.freeart.repo.AuctionRepository;
import edu.istu.freeart.repo.ImageRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/images")
public class ImageController {

    private final ImageRepository imageRepository;

    private final AuctionRepository auctionRepository;

    public ImageController(ImageRepository imageRepository, AuctionRepository auctionRepository) {
        this.imageRepository = imageRepository;
        this.auctionRepository = auctionRepository;
    }

    @GetMapping
    public List<Image> getImages() {
        final List<Image> images = imageRepository.findAllByIsActivated(true);
        final List<Auction> auctions = auctionRepository.findAll();
        final List<Image> imagesInAuction = auctions.stream().map(Auction::getImage).collect(Collectors.toList());
        images.removeIf(imagesInAuction::contains);
        images.sort((o1, o2) -> o2.getUploadDate().compareTo(o1.getUploadDate()));
        return images;
    }

    @GetMapping("{id}")
    public Image getImage(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean hasAdminRole = authentication.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ADMIN"));
        if (hasAdminRole) {
            return imageRepository.findById(id).orElse(null);
        }
        return imageRepository.findByIdAndIsActivated(id, true).orElse(null);
    }

    @GetMapping("/moderation")
    public List<Image> getImagesInModeration() {
        return imageRepository.findAllByIsActivated(false);
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateImage(@RequestBody Map<String, Long> request) {
        Optional<Image> image = imageRepository.findById(request.get("id"));
        image.ifPresent(value -> {
            value.setIsActivated(true);
            imageRepository.save(value);
        });
        return ResponseEntity.ok().build();
    }
}
