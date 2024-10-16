Documentation for playwright
https://playwright.dev/java/

For compiling use:

    mvn clean install

This command will open your report in a browser. (allure serve must be pre-installed)

    allure serve --host localhost allure-results


command for recording tests:

    mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="codegen https://www.telekom.de"



