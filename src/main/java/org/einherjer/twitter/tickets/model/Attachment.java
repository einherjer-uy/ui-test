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

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="Attachments") //table name is plural to avoid restricted keywords in some databases like "user" and "comment"
public class Attachment extends AbstractEntity {

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileSize;

    @Column(nullable = false)
    private String fileType;

    @JsonIgnore
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "bytes", length = 5242880, nullable = false)
    private byte[] bytes;

    @JsonIgnore(false) //override the getter just to include the id in the json
    @Override
    public Long getId() {
        return super.getId();
    }
}
