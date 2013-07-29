package org.einherjer.twitter.tickets.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Attachment extends AbstractEntity {

    private String fileName;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "bytes", length = 5242880, nullable = false)
    private byte[] bytes;

}
