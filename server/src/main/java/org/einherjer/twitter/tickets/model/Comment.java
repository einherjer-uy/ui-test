package org.einherjer.twitter.tickets.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends AbstractEntity {

    private String text;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

    public Comment(String text) {
        this.text = text;
        this.timestamp = new Date();
    }

}
