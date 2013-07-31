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
//@Table(name = "Project", uniqueConstraints = @UniqueConstraint(columnNames = { "prefix" }))
public class Project extends AbstractEntity {

    @Column(/*name = "prefix", */unique = true)
    private String prefix;

    private String name;

}
