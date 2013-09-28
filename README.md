ui-test
=======

requirements
------------
- java7
- maven3
- port 8080

usage
-----
- git clone https://github.com/einherjer-uy/ui-test.git
- Optionally: edit application.properties mail.to property to specify an email where all email notifications will be sent (for testing purposes)
- cd ui-test
- mvn jetty:run
- browse localhost:8080

users
-----
- Requestor: req@twitter.com / Admin_123
- Requestor 2: req2@twitter.com / Admin_123
- Approver: app@twitter.com / Admin_123
- Approver 2: app2@twitter.com / Admin_123
- Executor: exe@twitter.com / Admin_123
- Executor 2: exe2@twitter.com / Admin_123

---

<h3>Bonus:</h3> Import twitter_tickets.json in Postman (Chrome plugin) to peek at how data is looking in the server and stuff you might not be able to see in the UI

---

demo script
-----------
0- explain tech stack (incl. server-side)
1- responsiveness of login screen
2- login as requestor, approver, executor (in a different browser or incognito)
3- dashboard screen (navbar features, responsiveness specially in cards view, list vs cards)
4- differences in dashboard screen between requestor/approver/executor roles (actions and "create new"; requestor sees his own tickets, approver see NEW and APPROVED, executor sees APPROVED)
5- create new ticket (validation of due date and description, attachments)
6- edit ticket (download, delete attachment)
7- email notifications, open ticket by url (e.g. http://localhost:8080/#browse/TT-2)
8- requestor cancels a ticket
9- requestor creates a ticket, approver is notified, new tickets are shown in bold
10- approver changes priority, enters a coment, the requestor is notified (ONLY the creator of the ticket, not every requestor)
11- approver rejects from the edit modal without a comment, gets an error
12- same thing but with a comment, requestor is notfied and ticket is displayed in red (also in card view)
13- approved tickets are shown in green
14- approver approves, executor is notified, can view or mark as done
