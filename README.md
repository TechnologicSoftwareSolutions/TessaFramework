# Tessa Framework
The Tessa Framework is a small, *highly opinionated* set of annotations and utility code designed to simplify the development of RESTful Webservices and Event-based applications. 

The framework strives to present a set of core functionality with opinionated defaults to reduce, and in some cases eliminte, the boilerplate heavy burdens of configuration, transaction management, exception handling, security, and validations.

#### Our Goals for the Framework, in Order of Importance, are:
* Opinionated: Our intent is to provide a simple default way to rapidly achieve common functionality.
* Small: We strive to minimize the amount of code necessary to enable our core functionality.
* Non-Restrictive: If an edge case prevents following the opinionated approach, we won't stand in your way.
* Non-Dependent: As much as we can, we avoid forcing consumers to depend on or inherit from the framework.
* Verbose: We aim to simplify the tasks of monitoring, supporting, and debugging applications via automated logging.

#### The Primary Functionalities of the Framework:
* Simplified transaction management automatically handling:
  * Extracting contextual information from request headers and/or classes
  * Mapping error details to generate and return corresponding REST error responses.
  * Logging of request, response, contextual, error, timing, and event data.
* Integrated Identity and Authorization management including:
  * JWT-based security featuring issuance of public request tokens and private/internal authorization tokens.
  * Options for rotating secrets for use in decoding JWT tokens.
  * Annotations and Utility classes to simplify checking user has roles and/or events.
* Environment based property files with defaults for simple configuration management.
* Simple (one-line) integration with other services developed on the Tessa Framework.
* Highly verbose, automated logging of transaction/event details.
* Utility classes for common functionality such as validations

## Installation and Using the Framework
Detailed refrence documentation can be found in the wiki.

To quickly import the framework into your project, add the following dependency into your pom:

```xml
<dependency>
	<groupId>org.tessatech.tessa.framework</groupId>
	<artifactId>tessa-framework</artifactId>
	<version>1.0-SNAPSHOT</version>
</dependency>
```


## Building from Source
The latest version can be obtained via building the source by executing a maven build on the projects root directory though the command:

```
$ mvn clean install
```

## Support and Reporting Issues
If you are having issues, please check out our documentation on the wiki. Otherwise, we are always happy to help out. Please report any issues or feature requests using GitHub's integrated tracking system.

## License
The Tessa Framework is Open Source software released under the MIT license.
