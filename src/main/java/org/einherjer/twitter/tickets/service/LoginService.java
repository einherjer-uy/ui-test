package org.einherjer.twitter.tickets.service;

import org.einherjer.twitter.tickets.model.EmailAddress;
import org.einherjer.twitter.tickets.model.InvalidLoginException;
import org.einherjer.twitter.tickets.model.User;
import org.einherjer.twitter.tickets.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    //    @Transactional(readOnly = true)
    public User validateLogin(String username, String password) throws InvalidLoginException {
        User user = userRepository.findByUsername(new EmailAddress(username));
        if (user == null) {
            throw new InvalidLoginException("User not in database");
        }
        user.getPassword().validateLogin(password);
        return user;
    }

    //	public String findUserNameBySessionId(String sessionId) {
    //		Session session = sessionRepository.findOne(sessionId);
    //		return session != null ? session.getUsername() : null;
    //	}
    //	
    //	public String startSession(String username) {
    //		// get 32 byte random number. that's a lot of bits.
    //		SecureRandom generator = new SecureRandom();
    //		byte randomBytes[] = new byte[32];
    //		generator.nextBytes(randomBytes);
    //		BASE64Encoder encoder = new BASE64Encoder();
    //		String sessionID = encoder.encode(randomBytes);
    //		Session session = new Session();
    //		session.setUsername(username);
    //		session.setId(sessionID);
    //		sessionRepository.save(session);
    //		return sessionID;
    //	}
    //
    //	public void endSession(String sessionID) {
    //		sessionRepository.delete(sessionID);
    //	}

}
