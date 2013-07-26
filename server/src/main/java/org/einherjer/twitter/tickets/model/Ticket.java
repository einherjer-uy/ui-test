package org.einherjer.twitter.tickets.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import lombok.Getter;
import lombok.NoArgsConstructor;

import org.einherjer.twitter.tickets.ServiceLocator;
import org.einherjer.twitter.tickets.repository.TicketRepository;

@Entity
@NoArgsConstructor
public class Ticket extends AbstractEntity {

    private @Getter Integer number;
    
    @ManyToOne
    private @Getter Project project;

    private @Getter String title;

    private @Getter String description;

    private @Getter Status status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ticket_id")
    @OrderBy(value = "timestamp DESC")
    private List<Comment> comments = new ArrayList<Comment>();

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
