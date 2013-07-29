package org.einherjer.twitter.tickets.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.NoArgsConstructor;

import org.einherjer.twitter.tickets.ServiceLocator;
import org.einherjer.twitter.tickets.repository.TicketRepository;

@Entity
@NoArgsConstructor
@Table(name = "Ticket", uniqueConstraints = @UniqueConstraint(columnNames = { "number", "project_Id" }))
public class Ticket extends AbstractEntity {

    @Column(name = "mumber")
    private @Getter Integer number;
    
    @ManyToOne
    @JoinColumn(name = "project_Id")
    private @Getter Project project;

    private @Getter String title;

    private @Getter String description;

    private @Getter Status status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ticket_id")
    @OrderBy(value = "timestamp DESC")
    private List<Comment> comments = new ArrayList<Comment>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ticket_id")
    private Set<Attachment> attachments = new HashSet<Attachment>();

    public Ticket(Project project, String title, String description) {
        this.number = this.generateTicketNumber(project);
        this.project = project;
        this.title = title;
        this.description = description;
        this.status = Status.OPEN;
    }

    public List<Comment> getComments() {
        return Collections.unmodifiableList(comments);
    }

    public void addComment(String text) {
        this.comments.add(new Comment(text));
    }

    public Set<Attachment> getAttachments() {
        return Collections.unmodifiableSet(attachments);
    }

    public void addAttachment(String filename, byte[] bytes) {
        this.attachments.add(new Attachment(filename, bytes));
    }

    public void removeAttachment(Long attachmentId) {
        this.attachments.remove(this.findAttachmentById(attachmentId));
    }

    public Attachment findAttachmentById(Long attachmentId) {
        for (Attachment a : this.attachments) {
            if (a.getId().equals(attachmentId)) {
                return a;
            }
        }
        return null;
    }

    //TODO: concurrency issue here!!!
    private Integer generateTicketNumber(Project project) {
        TicketRepository repository = ServiceLocator.getInstance().getTicketRepository();
        Integer currMaxNum = repository.getMaxTicketNumberByProject(project);
        return currMaxNum == null ? 1 : currMaxNum++;
    }

    public static enum Status {
        OPEN,
        IN_PROGRESS,
        DONE;
    }
    
}
