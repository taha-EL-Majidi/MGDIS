import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.config.PriorityManager
import com.atlassian.jira.event.type.EventDispatchOption

//récupérer l'issue manager
IssueManager issueManager = ComponentAccessor.getIssueManager()

//récupérer l'utilisateur courant
ApplicationUser currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

//récupérer le champ Urgence dont l'ID est 12818
CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()
CustomField urgence = customFieldManager.getCustomFieldObject(12818)
//récupérer les options de la liste
def cfConfig = urgence.getRelevantConfig(issue)
def urgentOption = ComponentAccessor.optionsManager.getOptions(cfConfig)?.find {
    it.toString() == 'Urgent'
}
def eleveOption = ComponentAccessor.optionsManager.getOptions(cfConfig)?.find {
    it.toString() == 'Elevé'
}
def normalOption = ComponentAccessor.optionsManager.getOptions(cfConfig)?.find {
    it.toString() == 'Normal'
}
//récupérer la valeur du champ
def urgenceOption = issue.getCustomFieldValue(urgence)
String urgenceValue = urgenceOption.toString()

//récupérer les priorités
PriorityManager priorityManager = ComponentAccessor.getComponent(PriorityManager)
// L'id de la priorité P2 = 10105
def P2 = priorityManager.getPriority("10105")
// L'id de la priorité P3 = 10106
def P3 = priorityManager.getPriority("10106")
// L'id de la priorité P4 = 10107
def P4 = priorityManager.getPriority("10107")

if (urgenceValue == "Urgent")
{
    issue.setPriorityId(P3.id)
    issue.setCustomFieldValue(urgence,eleveOption)
}
else if (urgenceValue == "Elevé")
{
    issue.setPriorityId(P4.id)
    issue.setCustomFieldValue(urgence,normalOption)
}
//mettre à jour l'issue
issueManager.updateIssue(currentUser,issue, EventDispatchOption.ISSUE_UPDATED,false);
