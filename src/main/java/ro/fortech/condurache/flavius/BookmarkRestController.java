package ro.fortech.condurache.flavius;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.Collection;

/**
 * Created by internship on 05.07.2017.
 */

@RestController
@RequestMapping("/{userId}/bookmarks")
class BookmarkRestController {
    private final BookmarkRepository bookmarkRepository;
    private final AccountRepository accountRepository;

    @Autowired
    BookmarkRestController(BookmarkRepository bookmarkRepository,
                           AccountRepository accountRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.accountRepository = accountRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    Collection<Bookmark> readBookmarks(Principal principal) {
        this.validateUser(principal);
        return this.bookmarkRepository.findByAccountUsername(principal.getName());
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> add(Principal principal, @RequestBody Bookmark input) {
        this.validateUser(principal);

        return this.accountRepository
                .findByUsername(principal.getName())
                .map(account -> {
                    Bookmark bookmark = bookmarkRepository.save(new Bookmark(account, input.uri, input.description));

                    Link forOneBookmark = new BookmarkResource(bookmark).getLink("self");

                    return ResponseEntity.created(URI.create(forOneBookmark.getHref())).build();
                }).orElse(ResponseEntity.noContent().build());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{bookmarkId}")
    Bookmark readBookmark(Principal principal, @PathVariable Long bookmarkId) {
        this.validateUser(principal);
        return this.bookmarkRepository.findOne(bookmarkId);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{bookmarkId}")
    Collection<Bookmark> deleteBookmark(Principal principal, @PathVariable Long bookmarkId) {
        this.validateUser(principal);

        this.bookmarkRepository.delete(bookmarkId);

        return this.readBookmarks(principal);
    }

    private void validateUser(Principal principal) {
        String userId = principal.getName();
        this.accountRepository
                .findByUsername(userId)
                .orElseThrow(
                        () -> new UserNotFoundException(userId));
    }
}
