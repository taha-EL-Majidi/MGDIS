import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.config.PriorityManager


//récupérer issue courante
Issue issue = event.issue
MutableIssue mutableIssue = (MutableIssue) issue;
def issueService = ComponentAccessor.getIssueService()
IssueManager issueManager = ComponentAccessor.getIssueManager()

//récupérer l'administarteur
def adminUserName = "taha.elmajidi@valiantys.com"
ApplicationUser adminUser = ComponentAccessor.userManager.getUser(adminUserName)

//récupérer le champ criticité dont l'ID est 12817
CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()
CustomField criticite = customFieldManager.getCustomFieldObject(12817)

//récupérer les valeurs du champs criticité
def selectedOptions = mutableIssue.getCustomFieldValue(criticite) as Map
def parentOption = selectedOptions.get(null)
def childOption = selectedOptions.get("1")
String parentValue = parentOption.toString()
String childValue = childOption.toString()

//récupérer les priorités
PriorityManager priorityManager = ComponentAccessor.getComponent(PriorityManager)
// L'id de la priorité P1 = 10104
def P1 = priorityManager.getPriority("10104")
// L'id de la priorité P2 = 10105
def P2 = priorityManager.getPriority("10105")
// L'id de la priorité P3 = 10106
def P3 = priorityManager.getPriority("10106")
// L'id de la priorité P4 = 10107
def P4 = priorityManager.getPriority("10107")
// L'id de la priorité P5 = 10108
def P5 = priorityManager.getPriority("10108")
// L'id de la priorité P6 = 10109
def P6 = priorityManager.getPriority("10109")
// L'id de la priorité P7 = 10110
def P7 = priorityManager.getPriority("10110")
// L'id de la priorité P8 = 10111
def P8 = priorityManager.getPriority("10111")

log.error parentValue + " - " + childValue

//Assigner la priorité en focntion de la criticité
if (parentValue == "Bloquant" && childValue == "Immédiat")
{
    mutableIssue.setPriorityId(P1.id)
}
else if (parentValue == "Bloquant" && childValue == "Urgent")
{
    mutableIssue.setPriorityId(P2.id)
}
else if (parentValue == "Bloquant" && childValue == "Elevé")
{
    mutableIssue.setPriorityId(P3.id)
}
else if (parentValue == "Majeur" && childValue == "Urgent")
{
    mutableIssue.setPriorityId(P4.id)
}
else if (parentValue == "Majeur" && childValue == "Elevé")
{
    mutableIssue.setPriorityId(P5.id)
}
else if (parentValue == "Majeur" && childValue == "Normal")
{
    mutableIssue.setPriorityId(P6.id)
}
else if (parentValue == "Mineur" && childValue == "Elevé")
{
    mutableIssue.setPriorityId(P7.id)
}
else if (parentValue == "Mineur" && childValue == "Normal")
{
    mutableIssue.setPriorityId(P8.id)
}





log.error mutableIssue.getPriority().name


//mettre à jour le ticke courant
issueManager.updateIssue(adminUser,mutableIssue, EventDispatchOption.DO_NOT_DISPATCH,false);
