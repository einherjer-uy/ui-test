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
