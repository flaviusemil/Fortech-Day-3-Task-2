package ro.fortech.condurache.flavius;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.core.DummyInvocationUtils.methodOn;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * Created by internship on 05.07.2017.
 */
public class BookmarkResource extends ResourceSupport {

        private final Bookmark bookmark;

        public BookmarkResource(Bookmark bookmark) {
            String username = bookmark.getAccount().getUsername();
            this.bookmark = bookmark;
            this.add(new Link(bookmark.getUri(), "bookmark-uri"));
            this.add(linkTo(BookmarkRestController.class, username).withRel("bookmarks"));
            this.add(linkTo(methodOn(BookmarkRestController.class, username)
                    .readBookmark(username, bookmark.getId())).withSelfRel());
        }

        public Bookmark getBookmark() {
            return bookmark;
        }
}
