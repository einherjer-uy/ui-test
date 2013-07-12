package org.einherjer.blog.persistence;

import java.security.SecureRandom;

import sun.misc.BASE64Encoder;

public class SessionDAO {

    public SessionDAO() {

    }


    public String findUserNameBySessionId(String sessionId) {
        return null;
    }


    // starts a new session in the sessions table
    public String startSession(String username) {

        // get 32 byte random number. that's a lot of bits.
        SecureRandom generator = new SecureRandom();
        byte randomBytes[] = new byte[32];
        generator.nextBytes(randomBytes);

        BASE64Encoder encoder = new BASE64Encoder();

        String sessionID = encoder.encode(randomBytes);


        return sessionID;
    }

    // ends the session by deleting it from the sesisons table
    public void endSession(String sessionID) {

    }

    // retrieves the session from the sessions table
    public Object getSession(String sessionID) {
        return null;
    }
}
