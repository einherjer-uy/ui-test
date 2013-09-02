package org.einherjer.twitter.tickets.model;

import java.math.BigDecimal;

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
import com.fasterxml.jackson.annotation.JsonProperty;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name="Attachments") //table name is plural to avoid restricted keywords in some databases like "user" and "comment"
public class Attachment extends AbstractEntity {

    public static final String UNIT = "Kb";
    public static final BigDecimal SIZE_LIMIT = new BigDecimal(20971520);

    @Column(nullable = false)
    private String fileName;

    @JsonIgnore
    @Column(nullable = false)
    private BigDecimal sizeBytes;

    @Column(nullable = false)
    private String fileType;

    @JsonIgnore
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "bytes", length = 20971520, nullable = false)
    private byte[] bytes;

    @JsonIgnore(false) //override the getter just to include the id in the json
    @Override
    public Long getId() {
        return super.getId();
    }

    @JsonProperty
    public String getFileSize() {
        return sizeBytes.divideToIntegralValue(new BigDecimal(1024)).toString() + " " + UNIT;
    }

    private void setFileSize(String fileSize) {
        //do nothing, workaround for jackson complaning about the missing setter to use on deserialization
    }
}
