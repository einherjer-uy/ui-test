package org.einherjer.twitter.tickets.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Projects"/*, uniqueConstraints = @UniqueConstraint(columnNames = "prefix")*/)
//table name is plural to avoid restricted keywords in some databases like "user" and "comment"
public class Project extends AbstractEntity {

    @Column(name = "prefix", unique = true, nullable = false)
    private String prefix;

    private String name;

}
