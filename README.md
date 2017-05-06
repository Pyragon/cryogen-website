# README #

The Cryogen Website was built by Cody Thompson and Clayton Williams using some open-source templates found online.

### Built in Spark ###

Whilst not entirely necessary, this website was built in Spark (Java) for the purpose of learning.
Jade is used to compile and render templates to html

### Template Files ###

A properties template file has been included (props_.json) for reference. Copy this file, rename and remove the underscore and edit as needed.

An empty SQL export has also been included (database_skeletons.sql), you should only need to import this file into your database, and be set.

### TODO ###

*   Staff section can be rewritten down to like 4-5 files.
*   Finish Paypal transaction management

*   Forums

*   Staff page

    -   ~~Click 'view' on overview items needs to be done~~
    -   Way for admins to make announcements for 'overview' page
    -   Possibly limit searching to archived/active (atm searches through all)
    -   Add page support to all tabs
    -   Archiving report should edit 'last action', as well as add a comment, same for appeals
    -   Have log for all
    -   Refresh all pages when a punishment/appeal is accepted. Only refreshes appeal page atm
        -   Add methods into global js file   


*   Other
    -   Way for players to view their own active reports
    -   Proper download page needed
    -   Organize pages more
    -   'hover' to show popup for reason in punishments/appeal offence (reasons could be too long for table)

*   Security
    -   Secure and requests to website with 'secret' from properties file
