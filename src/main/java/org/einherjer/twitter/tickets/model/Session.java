package org.einherjer.twitter.tickets.model;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@EqualsAndHashCode(of = { "id" })
@Table(name = "Sessions")
public class Session {
    //this approach of handing attachments for tickets that havent' been created has a few issues
    //it is stored by session so if the user opens 2 tabs in a browser the session is the same and the files will be assigned to the first tab to save
    //this issue could be improved if when the first attachment is uploaded a token is generated and returned to the client so the client can identify
    //the batch of files corresponding to the ticket
    //also there should be some garbage collection mechanism, either asynchronous or tied to the timeout of the session

    @Id
    private @Getter String id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "session_id")
    private Set<Attachment> attachments = new HashSet<Attachment>();

    public Session(String id) {
        this.id = id;
    }
    public Set<Attachment> getAttachments() {
        return Collections.unmodifiableSet(attachments);
    }

    public Attachment addAttachment(String fileName, BigDecimal fileSize, String fileType, byte[] bytes) {
        Attachment attachment = new Attachment(fileName, fileSize, fileType, bytes);
        validateNewAttachmentSize(attachment);
        this.attachments.add(attachment);
        return attachment;
    }

    private void validateNewAttachmentSize(Attachment attachment) {
        BigDecimal total = BigDecimal.ZERO;
        for (Attachment a : this.attachments) {
            total = total.add(a.getSizeBytes());
        }
        if (total.add(attachment.getSizeBytes()).compareTo(Attachment.SIZE_LIMIT) > 0) {
            throw new IllegalArgumentException("Total attachment size per ticket exeeded (20mb)");
        }
    }

    public void removeAttachment(Long attachmentId) {
        this.attachments.remove(this.findAttachmentById(attachmentId));
    }

    public void cleanAttachments() {
        this.attachments.clear();
    }

    public Attachment findAttachmentById(Long attachmentId) {
        for (Attachment a : this.attachments) {
            if (a.getId().equals(attachmentId)) {
                return a;
            }
        }
        return null;
    }

}
