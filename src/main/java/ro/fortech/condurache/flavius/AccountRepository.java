package ro.fortech.condurache.flavius;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by internship on 05.07.2017.
 * Account Repository
 */

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);
}
