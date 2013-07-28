package org.einherjer.twitter.tickets.controller;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.einherjer.twitter.tickets.model.InvalidLoginException;
import org.einherjer.twitter.tickets.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public void doLogin(LoginForm loginForm) throws InvalidLoginException {
        userService.validateLogin(loginForm.getUsername(), loginForm.getPassword());
        //            String sessionID = blogService.startSession(user.getUsername());
        //            if (sessionID == null) {
        //                return "redirect:internal_error";
        //            } else {
        //                // set the cookie for the user's browser
        //                response.addCookie(new Cookie("session", sessionID));
        //                return "redirect:welcome";
        //            }
    }

    @ExceptionHandler(InvalidLoginException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public @ResponseBody ExceptionBody handleInvalidLoginException(InvalidLoginException e) {
        return new ExceptionBody(e.getMessage(), ExceptionUtils.getStackTrace(e));
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
