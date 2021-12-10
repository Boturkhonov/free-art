package edu.istu.freeart.controller;

import edu.istu.freeart.entity.Auction;
import edu.istu.freeart.entity.Bid;
import edu.istu.freeart.entity.Image;
import edu.istu.freeart.entity.User;
import edu.istu.freeart.repo.AuctionRepository;
import edu.istu.freeart.repo.BidRepository;
import edu.istu.freeart.repo.ImageRepository;
import edu.istu.freeart.repo.UserRepository;
import edu.istu.freeart.util.JsonMaker;
import org.hibernate.ObjectNotFoundException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static edu.istu.freeart.entity.User.Property.ABOUT;
import static edu.istu.freeart.entity.User.Property.AVATAR_URL;
import static edu.istu.freeart.entity.User.Property.ID;
import static edu.istu.freeart.entity.User.Property.LOGIN;
import static edu.istu.freeart.entity.User.Property.POINTS;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserRepository userRepository;

    private final AuctionRepository auctionRepository;

    private final ImageRepository imageRepository;

    private final BidRepository bidRepository;

    public UserController(UserRepository userRepository,
            AuctionRepository auctionRepository,
            ImageRepository imageRepository,
            BidRepository bidRepository) {
        this.userRepository = userRepository;
        this.auctionRepository = auctionRepository;
        this.imageRepository = imageRepository;
        this.bidRepository = bidRepository;
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getUser(@PathVariable Long id)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        User user =
                userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, User.class.getName()));

        JsonMaker<User> jsonMaker = new JsonMaker<>();
        final JSONObject jsonObject = jsonMaker.convertToJson(user, ID, LOGIN, ABOUT, POINTS, AVATAR_URL);

        jsonObject.put("countFollowers", user.getFollowers().size());
        jsonObject.put("countFollowing", user.getFollowing().size());
        int collectionCount = imageRepository.countAllByOwnerAndIsActivated(user, true)
                - auctionRepository.countAllBySellerAndBuyer(user, null);
        jsonObject.put("collectionCount", collectionCount);
        jsonObject.put("worksCount", imageRepository.findAllByCreatorAndIsActivated(user, true).size());
        jsonObject.put("collectionCost", userRepository.getCollectionCost(user));

        return new ResponseEntity<>(jsonObject.toMap(), HttpStatus.OK);
    }

    @GetMapping("{id}/points")
    public ResponseEntity<Object> getUserPoints(@PathVariable Long id)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Optional<User> optional = userRepository.findById(id);
        if (optional.isPresent()) {
            JsonMaker<User> jsonMaker = new JsonMaker<>();
            final JSONObject jsonObject = jsonMaker.convertToJson(optional.get(), POINTS);
            return ResponseEntity.status(HttpStatus.OK).body(jsonObject.toMap());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }
    }

    @GetMapping("top")
    public ResponseEntity<Object> getTopUsers() {
        List<User> users = userRepository.findAll();
        users.sort((o1, o2) -> (int) (userRepository.getCollectionCost(o2) - userRepository.getCollectionCost(o1)));
        if (users.size() > 5) {
            users = users.subList(0, 5);
        }
        final JsonMaker<User> jsonMaker = new JsonMaker<>();
        final List<Map<String, Object>> result = new ArrayList<>();
        AtomicInteger place = new AtomicInteger();
        users.forEach(user -> {
            try {
                place.getAndIncrement();
                final JSONObject jsonObject = jsonMaker.convertToJson(user, ID, LOGIN, AVATAR_URL);
                jsonObject.put("place", place.intValue());
                jsonObject.put("collectionCost", userRepository.getCollectionCost(user));
                result.add(jsonObject.toMap());
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("{id}/followers")
    public ResponseEntity<Object> getUserFollowers(@PathVariable Long id) {
        Optional<User> optional = userRepository.findById(id);
        if (optional.isPresent()) {
            final Set<User> followers = optional.get().getFollowers();
            final List<Map<String, Object>> result = new ArrayList<>();
            fillFollowersAndFollowingResult(result, followers);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
    }

    @GetMapping("{id}/following")
    public ResponseEntity<Object> getUserFollowing(@PathVariable Long id) {
        Optional<User> optional = userRepository.findById(id);
        if (optional.isPresent()) {
            final Set<User> following = optional.get().getFollowing();
            final List<Map<String, Object>> result = new ArrayList<>();
            fillFollowersAndFollowingResult(result, following);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
    }

    @GetMapping("{id}/auctions")
    public ResponseEntity<Object> getAuctions(@PathVariable Long id) {
        final Optional<User> optional = userRepository.findById(id);
        if (!optional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }
        List<Auction> auctions = auctionRepository.findAllBySellerAndBuyer(optional.get(), null);
        auctions.sort(
                (o1, o2) -> Comparator.nullsLast(LocalDateTime::compareTo).compare(o1.getEndDate(), o2.getEndDate()));
        auctions.forEach(auction -> {
            auction.setComments(null);
            auction.setBids(null);
        });
        return ResponseEntity.status(HttpStatus.OK).body(auctions);
    }

    @GetMapping("{id}/collections")
    public ResponseEntity<Object> getCollections(@PathVariable Long id) {
        final Optional<User> optional = userRepository.findById(id);
        if (!optional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }
        List<Image> collection = getCollection(optional.get(), false);
        collection.sort((o1, o2) -> o2.getUploadDate().compareTo(o1.getUploadDate()));

        return ResponseEntity.status(HttpStatus.OK).body(collection);
    }

    @GetMapping("auctions")
    public ResponseEntity<?> getAuctions(Principal principal) {
        Optional<User> optionalUser = userRepository.findByLogin(principal.getName());
        if (!optionalUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }
        final User user = optionalUser.get();
        final List<Auction> auctions = auctionRepository.findAll();
        auctions.removeIf(auction -> {
            if (auction.getBuyer() != null) {
                return true;
            }
            final List<Bid> bids = bidRepository.findAllByAuction(auction);
            List<User> bidUsers = bids.stream().map(Bid::getUser).collect(Collectors.toList());
            return auction.getSeller().equals(user) || !bidUsers.contains(user);
        });

        return ResponseEntity.ok(auctions);
    }

    @PostMapping("about")
    public ResponseEntity<?> updateAbout(@RequestBody User request, Principal principal) {
        Optional<User> optional = userRepository.findById(request.getId());
        if (!optional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        User user = optional.get();
        if (!user.getLogin().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        user.setAbout(request.getAbout());
        userRepository.save(user);
        return ResponseEntity.ok(userRepository.save(user));
    }

    @GetMapping("{id}/moderation")
    public ResponseEntity<?> getImagesInModeration(@PathVariable Long id, Principal principal) {
        User user = userRepository.findByLogin(principal.getName())
                .orElseThrow(() -> new ObjectNotFoundException(id, User.TYPE_NAME));
        if (!user.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        final List<Image> images = imageRepository.findAllByCreatorAndIsActivated(user, false);
        images.sort((o1, o2) -> o2.getUploadDate().compareTo(o1.getUploadDate()));
        return ResponseEntity.ok(images);
    }

    @GetMapping("/following")
    public ResponseEntity<?> getFollowingNews(Principal principal) {
        final User user = userRepository.findByLogin(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        List<Image> collections = new ArrayList<>();
        user.getFollowing().forEach(following -> {
            collections.addAll(getCollection(following, true));
        });
        collections.sort((o1, o2) -> o2.getUploadDate().compareTo(o1.getUploadDate()));
        return ResponseEntity.ok(collections);
    }

    @PostMapping("/follow")
    public ResponseEntity<?> follow(@RequestBody Map<String, Object> request, Principal principal) {
        long userId = (int) request.get("id");
        final User user =
                userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException(userId, User.TYPE_NAME));
        final User follower = userRepository.findByLogin(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        if (!follower.equals(user)) {
            follower.getFollowing().add(user);
            userRepository.save(follower);
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/follow")
    public ResponseEntity<?> unfollow(@RequestBody Map<String, Object> request, Principal principal) {
        long userId = (int) request.get("id");
        final User user =
                userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException(userId, User.TYPE_NAME));
        final User follower = userRepository.findByLogin(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        boolean remove = follower.getFollowing().remove(user);
        userRepository.save(follower);
        return ResponseEntity.ok(remove);
    }

    @PostMapping("/points")
    public ResponseEntity<?> updatePoints(@RequestBody Map<String, Integer> request, Principal principal) {
        final User user = userRepository.findByLogin(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException(principal.getName()));
        user.setPoints(request.get("points"));
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    private void fillFollowersAndFollowingResult(List<Map<String, Object>> result, Set<User> users) {
        users.forEach(follower -> {
            try {
                final JSONObject jsonObject = new JsonMaker<User>().convertToJson(follower, ID, LOGIN, AVATAR_URL);
                result.add(jsonObject.toMap());
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    private List<Image> getCollection(User user, boolean withAuction) {
        final List<Image> images = imageRepository.findAllByOwnerAndIsActivated(user, true);
        final List<Auction> allBySellerAndBuyer = auctionRepository.findAllBySellerAndBuyer(user, null);
        final List<Image> imagesInAction = allBySellerAndBuyer.stream().map(auction -> {
            auction.getImage().setPrice(auction.getPrice());
            return auction.getImage();
        }).collect(Collectors.toList());
        if (!withAuction) {
            images.removeIf(imagesInAction::contains);
        } else {
            images.forEach(image -> {
                int index = imagesInAction.indexOf(image);
                if (index > -1) {
                    image.setPrice(imagesInAction.get(index).getPrice());
                }
            });
        }
        images.forEach(image -> {
            final List<Auction> auctions = auctionRepository.findAllByBuyerAndImageOrderByEndDate(user, image);
            if (auctions.isEmpty()) {
                return;
            }
            final Auction auction = auctions.get(auctions.size() - 1);
            image.setPrice(auction.getPrice());
            image.setUploadDate(auction.getEndDate());
        });
        return images;
    }
}
