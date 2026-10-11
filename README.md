\# University Placement Cell Automation Tracker



A high-performance backend automation tool developed in Java and powered by a MySQL relational database. The engine replaces error-prone manual spreadsheet cross-referencing by executing real-time algorithmic eligibility filtering for campus recruitment drives.



\## Tech Stack \& Architecture

\* \*\*Backend Core:\*\* Java (JDK 17/21)

\* \*\*Database Infrastructure:\*\* MySQL Server \& Workbench

\* \*\*Database Driver Connectivity:\*\* JDBC (Java Database Connectivity Library) 9.1.0/8.0

\* \*\*Development Workspace:\*\* IntelliJ IDEA Community Edition



\## System Architecture \& Data Mapping

The ecosystem utilizes a relational schema containing three structural database components:

1\. `Students`: Tracks individual academic evaluation indexes (`cgpa`, `branch`, and `active\_backlogs`).

2\. `Companies`: Enforces specific corporate eligibility constraints (`min\_cgpa` floor and `max\_backlogs` caps).

3\. `Applications`: Maps historical tracking utilizing structured Foreign Keys.



\## Core Features \& Concepts Demonstrated

\* \*\*Algorithmic Shortlisting:\*\* Implements parameterized JDBC queries (`PreparedStatement`) to dynamically evaluate and output real-time student eligibility metrics based on targeted company rules.

\* \*\*SQL Injection Mitigation:\*\* Neutralizes structural database vulnerabilities by passing user inputs strictly as parameters rather than raw strings.

\* \*\*Data Integrity Enforcement:\*\* Implements unique constraints at the database gate to prevent duplicate profile entries and ensure data reliability.

\* \*\*Fault-Tolerant Execution:\*\* Leverages robust Java exception handling combined with `try-with-resources` structures to automatically close resource streams, preventing connection leaks.

\*

