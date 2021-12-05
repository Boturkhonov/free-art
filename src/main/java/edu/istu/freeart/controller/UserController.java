package edu.istu.freeart.controller;

import edu.istu.freeart.entity.Auction;
import edu.istu.freeart.entity.User;
import edu.istu.freeart.repo.AuctionRepository;
import edu.istu.freeart.repo.ImageRepository;
import edu.istu.freeart.repo.UserRepository;
import edu.istu.freeart.util.JsonMaker;
import org.hibernate.ObjectNotFoundException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
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

    public UserController(UserRepository userRepository,
            AuctionRepository auctionRepository,
            ImageRepository imageRepository) {
        this.userRepository = userRepository;
        this.auctionRepository = auctionRepository;
        this.imageRepository = imageRepository;
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
        int collectionCount = imageRepository.countAllByCreatorAndIsActivated(user, true)
                - auctionRepository.countAllBySellerAndBuyer(user, null);
        jsonObject.put("collectionCount", collectionCount);
        jsonObject.put("worksCount", imageRepository.findAllByCreator(user).size());
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
        List<Auction> auctions = auctionRepository.findAllBySeller(optional.get());
        auctions = auctions.stream()
                .filter(auction -> auction.getBuyer() == null)
                .sorted(Comparator.comparing(Auction::getEndDate))
                .collect(Collectors.toList());
        auctions.forEach(auction -> {
            auction.setComments(null);
            auction.setBids(null);
        });
        return ResponseEntity.status(HttpStatus.OK).body(auctions);
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
}
