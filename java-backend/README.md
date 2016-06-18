Before running the backend, you need to set two environment variables.

If you running the application from the command line, use system comman to set the variables, example in Linux/Mac:

```shell
export JDBC_DATABASE_URL="jdbc:mysql://localhost/cobone-reviews?user=root&password="
export CAPTCHA_SECRET="captcha secrect from recaptcah website"
````

If you running in Eclipse or other IDE, you need to set the variable in the IDE, for example in Eclipse:

````
Run -> Run Configurations -> Select your run configuration -> Go to Environment tab and set the two variables
````
