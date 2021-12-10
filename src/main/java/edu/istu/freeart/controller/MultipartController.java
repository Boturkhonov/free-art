package edu.istu.freeart.controller;

import edu.istu.freeart.entity.Image;
import edu.istu.freeart.entity.Tag;
import edu.istu.freeart.entity.User;
import edu.istu.freeart.model.FormWrapper;
import edu.istu.freeart.repo.ImageRepository;
import edu.istu.freeart.repo.TagRepository;
import edu.istu.freeart.repo.UserRepository;
import edu.istu.freeart.util.FileIOService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
public class MultipartController {

    @Value("${edu.istu.free-art.default-avatar-name}")
    private String defaultAvatarName;

    @Value("${edu.istu.free-art.avatar-folder}")
    private String avatarFolder;

    @Value("${edu.istu.free-art.image-folder}")
    private String imageFolder;

    private final FileIOService fileIOService;

    private final UserRepository userRepository;

    private final ImageRepository imageRepository;

    private final TagRepository tagRepository;

    public MultipartController(FileIOService fileIOService,
            UserRepository userRepository,
            ImageRepository imageRepository,
            TagRepository tagRepository) {
        this.fileIOService = fileIOService;
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
        this.tagRepository = tagRepository;
    }

    @RequestMapping(path = "/api/users/avatar", method = RequestMethod.POST, consumes = { "multipart/form-data" })
    public ResponseEntity<?> updateUserAvatar(@ModelAttribute FormWrapper formWrapper, Principal principal)
            throws IOException {
        final String avatarUrl = fileIOService.saveUploadedFile(formWrapper.getImage(), avatarFolder);
        final User user = userRepository.findByLogin(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        if (!user.getAvatarUrl().equals(defaultAvatarName)) {
            fileIOService.deleteFile(user.getAvatarUrl(), avatarFolder);
        }
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);
        return ResponseEntity.ok(avatarUrl);
    }

    @RequestMapping(path = "/api/images", method = RequestMethod.POST, consumes = { "multipart/form-data" })
    public ResponseEntity<?> uploadImage(@ModelAttribute FormWrapper formWrapper, Principal principal)
            throws IOException {

        final String imageUrl = fileIOService.saveUploadedFile(formWrapper.getImage(), imageFolder);
        final User user = userRepository.findByLogin(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));

        final String checksum = fileIOService.getFileChecksum(imageUrl, imageFolder);
        Optional<Image> optional = imageRepository.findByHash(checksum);
        if (optional.isPresent()) {
            fileIOService.deleteFile(imageUrl, imageFolder);
            return ResponseEntity.badRequest().build();
        }
        final Image image = new Image();

        image.setUrl(imageUrl);
        image.setCreator(user);
        image.setOwner(user);
        image.setDescription(formWrapper.getDescription());
        image.setTitle(formWrapper.getTitle());
        image.setUploadDate(LocalDateTime.now(ZoneOffset.UTC));
        image.setHash(checksum);
        image.setIsActivated(false);
        final Set<Tag> tags = new HashSet<>(tagRepository.findAllById(Arrays.asList(formWrapper.getTags())));
        image.setTags(tags);

        imageRepository.save(image);

        return ResponseEntity.ok("ok");
    }

    @DeleteMapping("/api/images/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable Long id) throws IOException {
        Optional<Image> image = imageRepository.findById(id);
        if (image.isPresent()) {
            fileIOService.deleteFile(image.get().getUrl(), imageFolder);
            imageRepository.delete(image.get());
        }
        return ResponseEntity.ok().build();
    }

}
