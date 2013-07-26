package org.einherjer.twitter.tickets.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.springframework.util.Assert;


@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@ToString
@EqualsAndHashCode
public class EmailAddress {

    private static final String EMAIL_PATTERN = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$";

    @Column(name = "email")
    private @Getter String emailAddress;

    public EmailAddress(String emailAddress) {
        Assert.isTrue(isValid(emailAddress), "Invalid email address!");
        Assert.isTrue(emailAddress.endsWith("twitter.com"), "Not a twitter address!");
        this.emailAddress = emailAddress;
    }

    private boolean isValid(String emailAddress) {
        return emailAddress == null ? false : emailAddress.toUpperCase().matches(EMAIL_PATTERN);
    }
}
