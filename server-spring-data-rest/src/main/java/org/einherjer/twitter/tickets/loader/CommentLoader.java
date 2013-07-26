package org.einherjer.twitter.tickets.loader;

import lombok.extern.slf4j.Slf4j;

import org.einherjer.twitter.tickets.model.Comment;
import org.einherjer.twitter.tickets.repository.CommentRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CommentLoader implements InitializingBean {

	@Autowired
    private CommentRepository comments;

	@Override
	public void afterPropertiesSet() throws Exception {
        if (comments.count() > 0) {
			return;
		}

        Comment c1 = comments.save(new Comment("Hello World!"));
        log.info("Created Comment " + c1);

	}

}
