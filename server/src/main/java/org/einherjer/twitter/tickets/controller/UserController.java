package org.einherjer.twitter.tickets.controller;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.einherjer.twitter.tickets.model.InvalidLoginException;
import org.einherjer.twitter.tickets.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Login
     * 
     * Example request:
     * 
     * POST /login HTTP/1.1
     * Host: localhost:8080
     * Content-Type: application/json
     * {"username":"user@twitter.com", "password":"Admin_123"}
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public @ResponseBody Map<String, Object> login(@RequestBody LoginJson jsonBody) throws InvalidLoginException {
        userService.validateLogin(jsonBody.getUsername(), jsonBody.getPassword());
        //            String sessionID = blogService.startSession(user.getUsername());
        //            if (sessionID == null) {
        //                return "redirect:internal_error";
        //            } else {
        //                // set the cookie for the user's browser
        //                response.addCookie(new Cookie("session", sessionID));
        //                return "redirect:welcome";
        //            }
        return new HashMap<String, Object>() {
            {
                put("message", "ok");
            }
        };
    }

    @ExceptionHandler(InvalidLoginException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public @ResponseBody ExceptionBody handleInvalidLoginException(InvalidLoginException e) {
        return new ExceptionBody(e.getMessage(), ExceptionUtils.getStackTrace(e));
    }
    
    /*
     * Represents the JSON body of the request
     * (could also represent form data (x-www-from-urlencoded)) in a POST /login
     *      (or PUT, but PUT requires HttpPutFormContentFilter, see Spring docs)
     * SSL assumed
     */
    @Getter
    @Setter
    public static class LoginJson {
        private String username;
        private String password;
    }

    //	@RequestMapping(value = "/logout", method = RequestMethod.GET)
    //    public String doLogout(Model model, @CookieValue("session") String sessionID) {
    //		if (sessionID == null) {
    //			// no session to end
    //			return "redirect:login";
    //		} else {
    //			// deletes from session table
    //			userService.endSession(sessionID);
    //			return "redirect:login";
    //		}
    //	}

}
