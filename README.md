
# Automatize Documentation on GitLab

## Using Java and GitLab4J

![Image for post](https://miro.medium.com/max/7952/0*r-R8KGFsxC4y72Zb)

Photo by  [Annie Spratt](https://unsplash.com/@anniespratt?utm_source=medium&utm_medium=referral)  on  [Unsplash](https://unsplash.com/?utm_source=medium&utm_medium=referral)

[**GitLab**](https://gitlab.com/)  is a well-known platform that allows a team to manage projects, to create documentation, pipelines, repositories and more.

It can be used to manage the project’s Sprints and to replace other tools such as  **Trello** or  **Jira**.

When utilized from “_end-to-end_” during the development cycle, it allows some automatizations, such as to generate documentation or a changelog based on requirements collected and developed during the process.

This article aims to demonstrate how to use the project’s issues or **User Stories** to generate a changelog, a Wiki page or “readme” file that can be delivered to costumers automatically.

## Technical stack

-   Java 11
-   [GitLab4J](https://github.com/gitlab4j)

## GitLab Structure

-   We have a milestone that is “**Sprint 1**” and it has issues associated with it, as you can check  [here](https://gitlab.com/danianepg/automatize-documentation/-/milestones).
-   We have a  [README.md](https://gitlab.com/danianepg/automatize-documentation/-/blob/master/README.md)  file representing a changelog file that will be packed with the application and deployed somewhere, through the pipeline. This file would be mapped to an URL inside the application and it would be available to the costumers through a link.

![Image for post](https://miro.medium.com/max/30/1*PoizmMX92x7nItC8GcFH0Q.png?q=20)

![Image for post](https://miro.medium.com/max/1903/1*PoizmMX92x7nItC8GcFH0Q.png)

Milestone Sprint 1

![Image for post](https://miro.medium.com/max/30/1*Z_eFWGpttnhNFkR20Dz5qg.png?q=20)

![Image for post](https://miro.medium.com/max/1902/1*Z_eFWGpttnhNFkR20Dz5qg.png)

Sprint 1 and its issues

![Image for post](https://miro.medium.com/max/30/1*k4hoBzq6nG4Bz3INqzLagA.png?q=20)

![Image for post](https://miro.medium.com/max/1904/1*k4hoBzq6nG4Bz3INqzLagA.png)

README.md file representing the changelog file visible to the customer

## Setup the integration

On your  **GitLab** profile, go to “_Settings_” and then “_Access Tokens_”. Generate an access token with all the checkboxes checked. Click on “_Create personal access token”_  and copy the value of the field “_Your new personal access token_”.

This token will be used to authenticate on  **GitLab** through a  **Java** application using the library  **GitLab4J**.

![Image for post](https://miro.medium.com/max/30/1*lHCuy9yj9LQYF71Jubfvdg.png?q=20)

![Image for post](https://miro.medium.com/max/1892/1*lHCuy9yj9LQYF71Jubfvdg.png)

Generate an access token on GitLab

![Image for post](https://miro.medium.com/max/30/1*5HPeswj2XAuWtjiiEPcZCQ.png?q=20)

![Image for post](https://miro.medium.com/max/1892/1*5HPeswj2XAuWtjiiEPcZCQ.png)

Generated access token

## Hands-on

Using  **GitLab4J** we will retrieve the latest milestone (it should be the first on the list), read its issues, labels and notes and if they are closed, we will create a new  _commit_ on  **README.md**  file and a Wiki page.

Let’s inspect the [**code on my GitHub**](https://github.com/danianepg/automatize-documentation-gitlab/blob/master/src/automatizedocumentation/AutomatizeDocumentation.java)  project that does that.

-   Method “_generateDocumentation_” reads the milestones and its issues.
-   Method “_getChangelogMessagesFromIssues_” reads the issues and its details.
-   Method “_printIssuesLabels_” just print issues’ labels. But it could be used for some filtering, for example, only issues with label “_public_” should be exported to the documentation.
-   Method “_printIssuesNotes_” retrieves all the comments related to the issue to add them to documentation.
-   Method “_createCommit_” commits to the repository.

## Results

After running the code, a Wiki page is created, as can be observed on the image below.

![Image for post](https://miro.medium.com/max/30/1*jHtI5fJXjLhQGGJnxwFWQw.png?q=20)

![Image for post](https://miro.medium.com/max/1904/1*jHtI5fJXjLhQGGJnxwFWQw.png)

Wiki page with Sprint’s and issues’ details

Also, a new commit was made and the README.md file was updated.

![Image for post](https://miro.medium.com/max/30/1*aG_qBrxn0VUM9wUL8LWYTg.png?q=20)

![Image for post](https://miro.medium.com/max/1903/1*aG_qBrxn0VUM9wUL8LWYTg.png)

Automatized commit

![Image for post](https://miro.medium.com/max/30/1*bey-jGDDGYMWcIIq1Piygg.png?q=20)

![Image for post](https://miro.medium.com/max/1891/1*bey-jGDDGYMWcIIq1Piygg.png)

Commit content

It is possible to read commit messages and add them to the documentation if needed.

## Conclusions

When  **GitLab** is used to control  **Sprints** and project’s requirements, it is possible to retrieve information from it and automatize the documentation only by reading the issues’ information.

If the  **User Stories**  or issues are well organized and written, no extra effort is needed to achieve this goal.

A simple  **Java** code and  **GitLab4J** library can digest information and thus, a project’s documentation can be automated.
