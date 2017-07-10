package ro.fortech.condurache.flavius;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

/**
 * Created by internship on 05.07.2017.
 * Tests for Bookmark Rest Controller
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BookmarksApplication.class)
@WebAppConfiguration
@WithMockUser(username = "bdussault", password = "password")
public class BookmarkRestControllerTest {

    private static final String[] auth = {"Authorization", "Bearer 1242aee8-a4fa-44de-a4a3-857a4542c304"};

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    private Principal principal;

    private String username = "bdussault";
    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    private Account account;
    private List<Bookmark> bookmarkList = new ArrayList<>();

    @Autowired
    private BookmarkRepository bookmarkRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny().orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();

        this.bookmarkRepository.deleteAllInBatch();
        this.accountRepository.deleteAllInBatch();

        this.account = accountRepository.save(new Account(username, "password"));
        this.bookmarkList.add(bookmarkRepository.save(new Bookmark(account, "http://bookmark.com/1/" + username, "A description")));
        this.bookmarkList.add(bookmarkRepository.save(new Bookmark(account, "http://bookmark.com/2/" + username, "A description")));
    }

    @Test
    public void userNotFound() throws Exception {
        mockMvc.perform(post("/george/bookmarks/")
                .principal(testPrincipal())
                .header(auth[0], auth[1])
                .content(this.json(new Bookmark()))
                .contentType(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void readSingleBookmark() throws Exception {
        mockMvc.perform(get("/bookmarks/" + this.bookmarkList.get(0).getId())
                .header(auth[0], auth[1])
                .principal(testPrincipal()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("bookmark.id", is(this.bookmarkList.get(0).getId().intValue())))
                .andExpect(jsonPath("bookmark.uri", is("http://bookmark.com/1/" + username)))
                .andExpect(jsonPath("bookmark.description", is("A description")));
    }

    @Test
    public void readBookmarks() throws Exception {
        mockMvc.perform(get("/bookmarks")
                .header(auth[0], auth[1])
                .principal(testPrincipal()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("content", hasSize(2)))
                .andExpect(jsonPath("content[0].bookmark.id", is(this.bookmarkList.get(0).getId().intValue())))
                .andExpect(jsonPath("content[0].bookmark.uri", is("http://bookmark.com/1/" + username)))
                .andExpect(jsonPath("content[0].bookmark.description", is("A description")))
                .andExpect(jsonPath("content[1].bookmark.id", is(this.bookmarkList.get(1).getId().intValue())))
                .andExpect(jsonPath("content[1].bookmark.uri", is("http://bookmark.com/2/" + username)))
                .andExpect(jsonPath("content[1].bookmark.description", is("A description")));
    }

    @Test
    public void createBookmark() throws Exception {
        String bookmarkJson = json(new Bookmark(
                this.account, "http://spring.io", "a bookmark to the best resource for Spring news and information"));

        this.mockMvc.perform(post("/bookmarks")
                .header(auth[0],auth[1])
                .principal(testPrincipal())
                .contentType(contentType)
                .content(bookmarkJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", is("test")));
    }

    @Test
    public void deleteBookmark() throws Exception {

        Long deletedItemId = this.bookmarkList.get(0).getId();

        this.mockMvc.perform(delete("/bookmarks/" + deletedItemId)
                .header(auth[0], auth[1])
                .principal(testPrincipal())
                .contentType(contentType))
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", hasSize(1)));
    }

    @Test
    public void updateBookmark() throws Exception {

        Bookmark bookmark = new Bookmark(account, "http://orion.com", "My simple website");

        System.out.println("Principal: " + json(bookmark));

        this.mockMvc.perform(put("/bookmarks/" + this.bookmarkList.get(0).getId())
                .principal(testPrincipal())
                .header(auth[0], auth[1])
                .contentType(contentType)
                .content(json(bookmark).getBytes()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("id", is(this.bookmarkList.get(0).getId().intValue())))
        .andExpect(jsonPath("uri", is(bookmark.getUri())))
        .andExpect(jsonPath("description", is(bookmark.getDescription())));
    }

    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    private Principal testPrincipal() {
        if (principal == null)
            principal = new Principal() {
                @Override
                public String getName() {
                    return username;
                }
            };

        return principal;
    }
}
