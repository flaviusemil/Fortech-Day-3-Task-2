package ro.fortech.condurache.flavius;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import java.security.Principal;

import static org.springframework.hateoas.core.DummyInvocationUtils.methodOn;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * Created by internship on 05.07.2017.
 * Bookmark Resources
 */
public class BookmarkResource extends ResourceSupport {

        private final Bookmark bookmark;

        public BookmarkResource(Bookmark bookmark, Principal principal) {
            String username = bookmark.getAccount().getUsername();
            this.bookmark = bookmark;
            this.add(new Link(bookmark.getUri(), "bookmark-uri"));
            this.add(linkTo(BookmarkRestController.class, username).withRel("bookmarks"));
            this.add(linkTo(methodOn(BookmarkRestController.class, username)
                    .readBookmark(principal, bookmark.getId())).withSelfRel());
        }

        public Bookmark getBookmark() {
            return bookmark;
        }
}
