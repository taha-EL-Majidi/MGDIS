import com.atlassian.servicedesk.api.organization.OrganizationService
import com.onresolve.scriptrunner.runner.customisers.PluginModule
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser



@PluginModule OrganizationService organizationService



def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()



def createBuilder = organizationService.newCreateBuilder()
def addUsersToOrgaBuilder = organizationService.newUsersOrganizationUpdateParametersBuilder()
def addOrganizationBuilder = organizationService.newOrganizationServiceDeskUpdateParametersBuilder()



// Create Organization
createBuilder.name("MGDIS")
def orga = organizationService.createOrganization(currentUser, createBuilder.build())



// Add user to Organization
Set<ApplicationUser> users = new HashSet<ApplicationUser>();
users.add(currentUser)
addUsersToOrgaBuilder.users(users)
addUsersToOrgaBuilder.organization(orga)
organizationService.addUsersToOrganization(currentUser, addUsersToOrgaBuilder.build())



// Add Organization to Project
addOrganizationBuilder.organization(orga)
addOrganizationBuilder.serviceDeskId(11)
organizationService.addOrganizationToServiceDesk(currentUser, addOrganizationBuilder.build())