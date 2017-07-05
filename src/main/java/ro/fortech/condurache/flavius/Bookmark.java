package ro.fortech.condurache.flavius;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * Created by internship on 05.07.2017.
 */

@Entity
public class Bookmark {

    @JsonIgnore
    @ManyToOne
    private Account account;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    // <editor-fold desc="Constructors">
    protected Bookmark() {

    }

    public Bookmark(Account account, String uri, String description) {
        this.uri = uri;
        this.description = description;
        this.account = account;
    }
    // </editor-fold>

    public String uri;
    public String description;

    // <editor-fold desc="Getters">
    public Account getAccount() {
        return account;
    }

    public Long getId() {
        return id;
    }

    public String getUri() {
        return uri;
    }

    public String getDescription() {
        return description;
    }
    // </editor-fold>


}
