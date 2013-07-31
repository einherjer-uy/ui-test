package org.einherjer.twitter.tickets.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="Attachments") //table name is plural to avoid restricted keywords in some databases like "user" and "comment"
public class Attachment extends AbstractEntity {

    @Column(nullable = false)
    private String fileName;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "bytes", length = 5242880, nullable = false)
    private byte[] bytes;

}
