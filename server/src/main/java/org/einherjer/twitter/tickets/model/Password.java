package org.einherjer.twitter.tickets.model;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.springframework.util.Assert;


@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@ToString
@EqualsAndHashCode
public class Password {

    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%_]).{6,})";

    @Column(name = "password")
    private @Getter String hashedAndSalted;

    public Password(String password) {
        Assert.isTrue(isSecure(password), "Passwords must contain a minimum of 6 characters, one lowercase letter, one uppercase letter, one digit, and one of the symbols @#$%_");
        this.hashedAndSalted = makePasswordHash(password, Integer.toString(new SecureRandom().nextInt()));
    }

    private boolean isSecure(String password) {
        return password == null ? false : password.matches(PASSWORD_PATTERN);
    }

    public void validateLogin(String password) {
        Assert.notNull(password, "Null password");

        String salt = this.hashedAndSalted.split(",")[1];

        if (!this.hashedAndSalted.equals(makePasswordHash(password, salt))) {
            throw new IllegalArgumentException("Submitted password is not a match");
        }
    }

    private String makePasswordHash(String password, String salt) {
        try {
            String saltedAndHashed = password + "," + salt;
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(saltedAndHashed.getBytes());
            Base64 encoder = new Base64();
            byte hashedBytes[] = (new String(digest.digest(), "UTF-8")).getBytes();
            return encoder.encodeToString(hashedBytes) + "," + salt;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 is not available", e);
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 unavailable?  Not a chance", e);
        }
    }

}
