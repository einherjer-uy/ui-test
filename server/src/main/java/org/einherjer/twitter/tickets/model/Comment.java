package org.einherjer.twitter.tickets.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "Comments") //table name is plural to avoid restricted keywords in some databases like "user" and "comment"
public class Comment extends AbstractEntity implements Comparable<Comment> {

    private String text;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(nullable = false)
    private DateTime timestamp;

    public Comment(String text) {
        this.text = text;
        this.timestamp = new DateTime();
    }

    @Override
    public int compareTo(Comment o) {
        return this.timestamp.compareTo(o.getTimestamp());
    }

}
