# cexplore
Code for concept explorer. A web application to display concept properties based on dbpedia queries.

About the project:

This project was developed by kunalsen and krishnadhi. The aim of the project is to make use of dbpedia (and other datasets in future)
to understand concepts/terms. It is intended to be a web application and currently runs on apache tomcat. A standalone 
test functionality is also available. 

Some Features:


1. Search for a concept say: Alan Turing

2. The applications fires a series of SPARQL queries to get relevant data from dbpedia.

3. Disambiguation, RDF Classes, Wikipedia topics, foaf links, RDF properties for this concept are displayed on the web app.

4. The user can explore other concepts for which links shown in step 3. Or the user can select a class to which the searched concept belongs to. Then compare various instances of that class.

5. The user can compare two concepts for similar properties. First search for concept1 then click on "save uri". 
Then search for concept2 and then click save uri. Then click the compare button to see how these concepts share same properties.


** The java source code resides under the directory src

** html and js files are in the web-src directory

** Runnable web application is contained in the cexplore.zip file

To run the application on commandline ant 1.6 or above is required

1. Create a database in mysql server and import the file cexplore.sql

2. Update src/jdbc_en_US.properties with the proper db_url, db_user and db_password

3. In the command line navigate to the root directory of this repo

4. Run ant 
    this will build the application

5. Run ant TestConcept
    this will run the test functionality. It will ask for an input. For testing purposes you can put a famous personality name.
    e.g. Alan Turing
    
    
To run the web application on tomcat

1. Extract cexplore folder from cexplore.zip

2. GOTO cexplore/WEB-INF/classes/jdbc_en_US.properties and update with correct settings

3. paste the cexplore folder in tomcat webapps folder

4. restart tomcat

5. open http://[machinename]:[port]/cexplore in the browser
