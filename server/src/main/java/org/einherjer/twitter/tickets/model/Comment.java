package org.einherjer.twitter.tickets.model;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends AbstractEntity {

    private String text;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime timestamp;

    public Comment(String text) {
        this.text = text;
        this.timestamp = new DateTime();
    }

}
