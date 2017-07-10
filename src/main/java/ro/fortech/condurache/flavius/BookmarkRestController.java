package ro.fortech.condurache.flavius;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletRequest;
import java.awt.print.Book;
import java.net.URI;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by internship on 05.07.2017.
 * Bookmark REST Controller
 */

@RestController
@RequestMapping("/bookmarks")
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
    Resources<BookmarkResource> readBookmarks(Principal principal) {
        this.validateUser(principal);

        List<BookmarkResource> bookmarkResourceList = bookmarkRepository
                .findByAccountUsername(principal.getName()).stream()
                .map(bookmark -> new BookmarkResource(bookmark, principal))
                .collect(Collectors.toList());

        return new Resources<>(bookmarkResourceList);
        //return this.bookmarkRepository.findByAccountUsername(principal.getName());
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> add(Principal principal, @RequestBody Bookmark input) {
        this.validateUser(principal);

        return accountRepository
                .findByUsername(principal.getName())
                .map(account -> {
                    Bookmark bookmark = bookmarkRepository.save(new Bookmark(account, input.uri, input.description));

                    Link forOneBookmark = new BookmarkResource(bookmark, principal).getLink(Link.REL_SELF);

                    return ResponseEntity.created(URI.create(forOneBookmark.getHref())).build();
                }).orElse(ResponseEntity.noContent().build());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{bookmarkId}")
    BookmarkResource readBookmark(Principal principal, @PathVariable Long bookmarkId) {
        this.validateUser(principal);
        return new BookmarkResource(this.bookmarkRepository.findOne(bookmarkId), principal);
        //return this.bookmarkRepository.findOne(bookmarkId);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{bookmarkId}")
    Resources<BookmarkResource> deleteBookmark(Principal principal, @PathVariable Long bookmarkId) {
        this.validateUser(principal);

        this.bookmarkRepository.delete(bookmarkId);

        return this.readBookmarks(principal);
        //return new BookmarkResource(this.bookmarkRepository.findOne(bookmarkId), principal);
        //return this.readBookmarks(principal);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{bookmarkId}")
    Bookmark updateBookmark(Principal principal,
                                  @PathVariable Long bookmarkId,
                                  @RequestBody Bookmark request) {
        this.validateUser(principal);
        //System.out.println(this.bookmarkRepository.findOne(bookmarkId).getUri());
        this.bookmarkRepository.findOne(bookmarkId).update(request);

        //System.out.println(this.bookmarkRepository.findOne(bookmarkId).getUri());


        return this.bookmarkRepository.findOne(bookmarkId);
    }

    private void validateUser(Principal principal) {
        String userId = principal.getName();
        this.accountRepository
                .findByUsername(userId)
                .orElseThrow(
                        () -> new UserNotFoundException(userId));
    }
}
