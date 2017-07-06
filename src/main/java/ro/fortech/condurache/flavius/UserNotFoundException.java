package ro.fortech.condurache.flavius;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by internship on 05.07.2017.
 * Custom Exception: User Not Found - 404
 */

class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String userId) {
        super("could not find user '" + userId + "'.");
    }
}