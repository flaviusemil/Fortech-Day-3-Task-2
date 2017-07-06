package ro.fortech.condurache.flavius;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

/**
 * Created by internship on 05.07.2017.
 * Bookmark Repository
 */

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Collection<Bookmark> findByAccountUsername(String username);
}
