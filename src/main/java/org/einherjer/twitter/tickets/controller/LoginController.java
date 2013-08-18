package org.einherjer.twitter.tickets.controller;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.einherjer.twitter.tickets.model.InvalidLoginException;
import org.einherjer.twitter.tickets.model.User;
import org.einherjer.twitter.tickets.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class LoginController /*implements ResourceProcessor<Resources<Object>>*/{

    //    public static final String LOGIN_REL = "login";

    @Autowired
    private LoginService loginService;

    /*
     * returning String and receiving ModelMap is the same as receiving and returning ModelAndView, and setting ModelAndView.setViewName
     * This method is not used since the login is handled by spring security. It's left only to show how to resolve the view in the case of html resources (see MvcWebApplicationInitializer)
     * The @RequestParam show how to access query string values (in this case ?error or ?logout were going to be used to show variants of the view)
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(@RequestParam(value = "error", required = false) boolean error, @RequestParam(value = "logout", required = false) boolean logout, ModelMap model) {
        return "forward:/content/login.html"; //for static resources no need for ViewResolver, just forward and the web server root servlet will respond
    }

    /**
     * Login
     * 
     * Example request:
     * 
     * POST /api/login HTTP/1.1
     * Host: localhost:8080
     * Content-Type: application/x-www-form-urlencoded
     * username=user%40twitter.com&password=Admin_123
     */
    //Jackson can be used with "full data binding" (e.g. @RequestBody LoginJson), or "simple data binding" (e.g. @ResponseBody Map<String, Object>). 
    //      Both kinds of data binding can be used in either @ResponseBody or @RequestBody.
    //Note also that we can create an object LoginJson but we can also use the same model entity (Comment or any other).
    //      If we use the model entity not every field need to be present (when serializing the null fields will not appear on the JSON,
    //      when deserializing missing fields in the JSON will be left null in the deserialized object)
    //This method is not used since the login is handled by spring security. It's left only to show Jackson full vs simple data binding
    @RequestMapping(value = "/login2", method = RequestMethod.POST)
    public ResponseEntity<String> /*@ResponseBody Map<String, Object>*/login(/*@RequestBody*/LoginJson jsonBody) throws InvalidLoginException {
        loginService.validateLogin(jsonBody.getUsername(), jsonBody.getPassword());
        //            String sessionID = blogService.startSession(user.getUsername());
        //            if (sessionID == null) {
        //                return "redirect:internal_error";
        //            } else {
        //                // set the cookie for the user's browser
        //                response.addCookie(new Cookie("session", sessionID));
        //                return "redirect:welcome";
        //            }

        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
        //        return new HashMap<String, Object>() {
        //            {
        //                put("message", "ok");
        //            }
        //        };
    }

    //This method is not used since the login is handled by spring security. It's left only to show how to translate exceptions to http status error codes, and include an error message in the response 
    @ExceptionHandler(InvalidLoginException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public @ResponseBody ExceptionBody handleInvalidLoginException(InvalidLoginException e) {
        return new ExceptionBody(e.getMessage(), ExceptionUtils.getStackTrace(e));
    }
    
    /*
     * Represents form data (x-www-from-urlencoded)) in a POST /login
     *      (or PUT, but PUT requires HttpPutFormContentFilter, see Spring docs)
     * (could also represent the JSON body of the request, is @RequestBody is used) 
     * SSL assumed
     */
    @Getter
    @Setter
    public static class LoginJson {
        private String username;
        private String password;
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

    @RequestMapping(value = "/loggedUser", method = RequestMethod.GET)
    public @ResponseBody User getLoggedUser() {
        return loginService.getLoggedUser();
    }
        
}
