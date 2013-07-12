package org.einherjer.blog.persistence;

import java.util.List;

public class BlogPostDAO {

    public BlogPostDAO() {

    }

    // Return a single post corresponding to a permalink
    public Object findByPermalink(String permalink) {
        return null;
    }

    // Return a list of posts in descending order. Limit determines
    // how many posts are returned.
    public List<Object> findByDateDescending(int limit) {
        return null;
    }

    public String addPost(String title, String body, List tags, String username) {
        String permalink = title.replaceAll("\\s", "_"); // whitespace becomes _
        permalink = permalink.replaceAll("\\W", ""); // get rid of non alphanumeric
        permalink = permalink.toLowerCase();

        

        return permalink;
    }

    // Append a comment to a blog post
    public void addPostComment(final String name, final String email, final String body,
                               final String permalink) {

        ;
    }

}
