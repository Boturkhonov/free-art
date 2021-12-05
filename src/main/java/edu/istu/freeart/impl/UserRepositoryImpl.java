package edu.istu.freeart.impl;

import edu.istu.freeart.entity.User;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Component
public class UserRepositoryImpl {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unused")
    public Long getCollectionCost(User user) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT SUM(a.price) FROM Auction a INNER JOIN Image i " +
                "ON a.buyer = i.owner AND i.owner.id = :id AND i.id = a.image.id", Long.class);
        query.setParameter("id", user.getId());
        Long result = query.getSingleResult();
        return result == null ? 0 : result;
    }

}
