package org.einherjer.twitter.tickets.controller;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.einherjer.twitter.tickets.model.InvalidLoginException;
import org.einherjer.twitter.tickets.service.LoginService;
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
public class LoginController /*implements ResourceProcessor<Resources<Object>>*/{

    public static final String LOGIN_REL = "login";

    @Autowired
    private LoginService loginService;

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
        loginService.validateLogin(jsonBody.getUsername(), jsonBody.getPassword());
        //            String sessionID = blogService.startSession(user.getUsername());
        //            if (sessionID == null) {
        //                return "redirect:internal_error";
        //            } else {
        //                // set the cookie for the user's browser
        //                response.addCookie(new Cookie("session", sessionID));
        //                return "redirect:welcome";
        //            }
        return okMessage();
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

    private HashMap<String, Object> okMessage() {
        return new HashMap<String, Object>() {
            {
                put("message", "ok");
            }
        };
    }

    //    /**
    //     * Exposes the {@link LoginController} to the root resource
    //     * 
    //     * @see org.springframework.hateoas.ResourceProcessor#process(org.springframework.hateoas.ResourceSupport)
    //     */
    //    @Override
    //    public Resources<Object> process(Resources<Object> resource) {
    //        resource.add(linkTo(LoginController.class).slash("login").withRel(LOGIN_REL));
    //        return resource;
    //    }

}
