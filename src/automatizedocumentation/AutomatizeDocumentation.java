package automatizedocumentation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.CommitAction;
import org.gitlab4j.api.models.CommitAction.Action;
import org.gitlab4j.api.models.Issue;
import org.gitlab4j.api.models.Milestone;
import org.gitlab4j.api.models.Note;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.RepositoryFile;

import automatizedocumentation.utils.DateUtils;

/**
 * Automatize documentation for GitLab
 *
 * @author Daniane P. Gomes
 *
 */
public class AutomatizeDocumentation {

	private final String url = "https://gitlab.com/";

	private final String token = "PLACE_YOUR_TOKEN_HERE";

	private final String projectRepository = "danianepg/automatize-documentation";

	private final String changelogFilePath = "README.md";

	private final String branch = "master";

	private final String commitEmail = "danianepg@gmail.com";

	private final String commitName = "Daniane P. Gomes";

	private final GitLabApi gitLabApi;

	public AutomatizeDocumentation() {
		this.gitLabApi = new GitLabApi(this.url, this.token);
	}

	/**
	 * Finds all issues on a project related to the most recent milestone and write
	 * its descriptions and comments on a file on repository and on the project
	 * Wiki.
	 */
	public void generateDocumentation() {

		try {

			final Integer projectId = this.getProject(this.gitLabApi).getId();
			final Milestone milestone = this.getMostRecentMilestone(this.gitLabApi, projectId);
			final Integer mostRecentMilestoneId = milestone.getId();

			final List<Issue> issues = this.gitLabApi.getIssuesApi().getIssues(projectId);
			System.out.println("Number of issues found: " + issues.size());

			final StringBuilder changelogMessages = new StringBuilder();
			final LocalDate from = DateUtils.toLocalDate(milestone.getStartDate());
			final LocalDate to = DateUtils.toLocalDate(milestone.getDueDate());

			final StringBuilder sprintTitle = new StringBuilder();
			sprintTitle.append(milestone.getTitle());
			sprintTitle.append(": from ");
			sprintTitle.append(from.format(DateUtils.getDateFormat()));
			sprintTitle.append(" to ");
			sprintTitle.append(to.format(DateUtils.getDateFormat()));
			changelogMessages.append("## " + sprintTitle.toString() + "\n\n");

			this.getChangelogMessagesFromIssues(projectId, mostRecentMilestoneId, issues, changelogMessages);

			this.createCommit(this.gitLabApi, projectId, changelogMessages);
			this.createWiki(projectId, changelogMessages, sprintTitle.toString());

		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Create the wiki page.
	 *
	 * @param projectId
	 * @param changelogMessages
	 * @param sprintTitle
	 * @throws GitLabApiException
	 */
	private void createWiki(final Integer projectId, final StringBuilder changelogMessages, final String sprintTitle)
			throws GitLabApiException {

		System.out.println("Creating wiki");
		this.gitLabApi.getWikisApi().createPage(projectId, sprintTitle, changelogMessages.toString());
	}

	/**
	 * Get the content of an issue
	 *
	 * @param projectId
	 * @param mostRecentMilestoneId
	 * @param issues
	 * @param changelogMessages
	 * @throws GitLabApiException
	 */
	private void getChangelogMessagesFromIssues(final Integer projectId, final Integer mostRecentMilestoneId,
			final List<Issue> issues, final StringBuilder changelogMessages) throws GitLabApiException {

		issues.stream().forEach(i -> {

			System.out.println("Issue: " + i.getIid());
			if (i.getMilestone() != null && i.getMilestone().getId().equals(mostRecentMilestoneId)) {

				// Get only solution from description
				final String description = i.getDescription();

				changelogMessages.append("Issue " + i.getIid() + ": ");
				changelogMessages.append(description + "\n\n");

				this.printIssuesLabels(i);
				changelogMessages.append(this.printIssuesNotes(projectId, i));
			}
		});

	}

	/**
	 * Print the labels of an issue
	 *
	 * @param issue
	 */
	private void printIssuesLabels(final Issue issue) {
		System.out.println("Issue labels: ");
		issue.getLabels().stream().forEach(System.out::println);
	}

	/**
	 *
	 * @param projectId
	 * @param i
	 */
	private StringBuilder printIssuesNotes(final Integer projectId, final Issue i) {

		final StringBuilder notes = new StringBuilder();

		try {

			final List<Note> notesLst = this.gitLabApi.getNotesApi().getIssueNotes(projectId, i.getIid());
			System.out.println("Number of notes found: " + notesLst.size());

			final boolean isClosed = notesLst.stream().filter(n -> n.getBody().contains("closed")).findAny()
					.isPresent();

			if (isClosed) {
				notesLst.stream().forEach(n -> {
					System.out.println("\t" + n.getBody());
					notes.append("\n* " + n.getBody());
				});
			}

		} catch (final GitLabApiException e) {
			e.printStackTrace();
		}

		return notes;
	}

	/**
	 *
	 * @param gitLabApi
	 * @param projectId
	 * @param changelogMessages
	 * @throws GitLabApiException
	 */
	private void createCommit(final GitLabApi gitLabApi, final Integer projectId, final StringBuilder changelogMessages)
			throws GitLabApiException {

		System.out.println("Creating commit");

		final RepositoryFile file = gitLabApi.getRepositoryFileApi().getFile(projectId, this.changelogFilePath,
				this.branch);
		final StringBuilder content = new StringBuilder(file.getDecodedContentAsString());

		final CommitAction ca = new CommitAction();
		ca.setFilePath(this.changelogFilePath);

		final Action action = Action.UPDATE;
		ca.setAction(action);

		content.append("\n\n");
		content.append(changelogMessages.toString());

		ca.setContent(content.toString());

		final List<CommitAction> commitLst = new ArrayList<>();
		commitLst.add(ca);

		gitLabApi.getCommitsApi().createCommit(projectId, this.branch, "My awesome commit", null, this.commitEmail,
				this.commitName, commitLst);
	}

	/**
	 *
	 * @param gitLabApi
	 * @return
	 * @throws GitLabApiException
	 */
	private Project getProject(final GitLabApi gitLabApi) throws GitLabApiException {
		return gitLabApi.getProjectApi().getProject(this.projectRepository);
	}

	/**
	 * Get the most recent milestone
	 *
	 * @param gitLabApi
	 * @param projectId
	 * @return
	 * @throws GitLabApiException
	 */
	private Milestone getMostRecentMilestone(final GitLabApi gitLabApi, final Integer projectId)
			throws GitLabApiException {
		final List<Milestone> milestones = gitLabApi.getMilestonesApi().getMilestones(projectId);
		System.out.println("Number of milestones found:" + milestones.size());

		return milestones.get(0);
	}

	public static void main(final String[] args) {
		final AutomatizeDocumentation generateChangelog = new AutomatizeDocumentation();
		generateChangelog.generateDocumentation();
	}

}
