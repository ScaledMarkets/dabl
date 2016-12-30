# Motivation

Existing “build” languages (e.g., make, ant, maven, gradle, Jenkins “pipeline”) leave much to be desired. They tend to be non-composable, weakly typed, and have poor extensibility features. As such, they make build processes brittle and unreliable and also limit reusability.

There is no reason why it needs to be this way: The current state of affairs stems from the “guru” culture of system administration, and the still present 1980s era tradition that build languages are quick-to-modify scripts that are maintained by sysadmin “gurus” who are above accountability for the maintainability of their scripts. Yet, infrastructure code definitely warrants high reliability and maintainability, and increasingly build pipelines are part of infrastructure. Thus, the time for reliable and composable build languages has come.

Treating pipeline definition as a first-class design activity, supported by a true language, is consistent with the DevOps philosophy of treating the build and deployment pipeline as a system to be designed, coded, and maintained.

A better model than current practice is needed, whereby,

* The build language is strongly typed, in order to promote maintainability and reliability.
* The build language uses information hiding, encapsulation, and true modularity in order to promote reuse and extensibility.



[Language Reference](langref/README.md)

See also https://drive.google.com/open?id=1xoyDMebGHedfBUFcsMUkjQJHSwrt3sCFF8CDhrVaTjo
