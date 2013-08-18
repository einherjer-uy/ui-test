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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.einherjer.twitter.tickets.ServiceLocator;
import org.einherjer.twitter.tickets.repository.TicketRepository;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@Entity
@NoArgsConstructor
//table name is plural to avoid restricted keywords in some databases like "user" and "comment"
@Table(name = "Tickets", uniqueConstraints = @UniqueConstraint(columnNames = { "ticket_number", "project_id" }))
@JsonIgnoreProperties(ignoreUnknown = true) //needed so that when BackboneJS sends a json with "number":null (when creating a new ticket, cause it always sends the id field) the parsing of the json doesn't fail because of the absence of a getNumber here
public class Ticket extends AbstractEntity {

    @JsonIgnore //we'll use getNumber as the ticket number (id for the rest api)
    @Column(name = "ticket_number", nullable = false) //just "number" is restricted in some databases
    private Integer number;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private @Getter Project project;

    @Column(nullable = false)
    private @Getter TicketType type;
    
    @Column(nullable = false)
    private @Getter TicketPriority priority;
    
    private @Getter String title;

    @Column(nullable = false)
    private @Getter String description;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(nullable = true)
    private @Getter DateTime due;

    @Column(nullable = false)
    private @Getter TicketStatus status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "assignee_id", nullable = false)
    private @Getter @Setter User assignee;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ticket_id")
    @OrderBy(value = "timestamp DESC")
    private List<LogEntry> log = new ArrayList<LogEntry>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ticket_id")
    private Set<Attachment> attachments = new HashSet<Attachment>();

    public Ticket(Project project, String title, String description, User assignee, TicketType type, TicketPriority priority, DateTime due) {
        this.number = this.generateTicketNumber(project);
        this.project = project;
        this.title = title;
        this.description = description;
        this.status = TicketStatus.NEW;
        this.assignee = assignee;
        this.type = type;
        this.priority = priority;
        this.due = due;
    }

    /**
     * Constructor used by the service when creating a new Ticket. It's the only place where createdBy and createdTimestamp are set
     * @param project
     * @param data
     */
    public Ticket(Project project, Ticket data, User createdBy) {
        this(project, data.title, data.description, data.assignee, data.type, data.priority, data.due);
        this.logCreation(createdBy);
    }

    //we'll use this as the id for the rest api
    @JsonProperty("number")
    public String getTicketId() {
        return this.project.getPrefix() + "-" + this.number;
    }

    /**
     * Only sets the value if the original value is not null cause the data is coming as a JSON and a missing key would be mapped to a null value otherwise 
     */
    public void set(Ticket data) {
        if (data.title != null) this.title = data.title;
        if (data.description != null) this.description = data.description;
        if (data.status != null) this.status = data.status;
        if (data.assignee != null) this.assignee = data.assignee;
        if (data.type != null) this.type = data.type;
        if (data.priority != null) this.priority = data.priority;
        if (data.due != null) this.due = data.due;
    }

    public List<LogEntry> getLog() {
        return Collections.unmodifiableList(log);
    }

    public void addComment(String text, User user) {
        this.log.add(new CommentLogEntry(user, text));
    }

    public void logCreation(User user) {
        this.log.add(new CreationLogEntry(user));
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
        return currMaxNum == null ? 1 : currMaxNum + 1;
    }

    @AllArgsConstructor
    public static enum TicketStatus {
        NEW("New"),
        CANCELLED("Cancelled"),
        APPROVED("Approved");
        private @Getter String description;
    }
    
    @AllArgsConstructor
    public static enum TicketType {
        HARDWARE("Hardware"),
        MERCHANDISING("Merchandising"),
        APPLICATIONS("Applications"),
        CONNECTIVITY("Connectivity"),
        SECURITY("Security");
        private @Getter String description;
    }
    
    @AllArgsConstructor
    public static enum TicketPriority {
        LOW("Low"),
        MEDIUM("Medium"),
        HIGH("High"),
        CRITICAL("Critical");
        private @Getter String description;
    }
}
