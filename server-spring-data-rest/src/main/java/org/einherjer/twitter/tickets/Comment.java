package org.einherjer.twitter.tickets;

import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends AbstractEntity {

    private String text;

}
