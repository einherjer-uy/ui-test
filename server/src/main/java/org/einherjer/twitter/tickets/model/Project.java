package org.einherjer.twitter.tickets.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Project extends AbstractEntity {

    @Column(unique = true)
    private String prefix;

    private String name;

}
