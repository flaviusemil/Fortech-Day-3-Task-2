package ro.fortech.condurache.flavius;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by internship on 05.07.2017.
 */

@Entity
public class Account {

    @OneToMany(mappedBy = "account")
    private Set<Bookmark> bookmarks = new HashSet<>();

    @Id
    @GeneratedValue
    private Long id;

    // <editor-fold desc="Getters">

    public Set<Bookmark> getBookmarks() {
        return bookmarks;
    }

    public Long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    // </editor-fold>

    @JsonIgnore
    public String password;
    public String username;

    // <editor-fold desc="Constructors">
    public Account(String name, String password) {
        this.username = name;
        this.password = password;
    }

    protected Account() {

    }

    // </editor-fold>
}
