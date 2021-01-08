import com.atlassian.servicedesk.api.organization.OrganizationService
import com.onresolve.scriptrunner.runner.customisers.PluginModule
import com.atlassian.jira.component.ComponentAccessor


@PluginModule OrganizationService organizationService

// récupérer l'utilisateur connecté qui lance le script (doit être admin)
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

//créer le builder qui permet de créer des organisations
def createBuilder = organizationService.newCreateBuilder()
//créer le builder qui permet des organisations à des projets
def addOrganizationBuilder = organizationService.newOrganizationServiceDeskUpdateParametersBuilder()

//récupérer le fichier qui contient la liste des organisations et le parcourir
new File("/opt/atlassian/jira/temp/JSD_Organisations.csv").withReader('UTF-8') { reader ->
    def line
    def index = 0
    while ((line = reader.readLine()) != null) {
        if (index >0){
            //la variable "line" contient le nom de l'organisation à créer
            createBuilder.name(line)
            //Lancer le build pour créer l'organisation qui retourne l'objet organisation dans la varibale "orga"
            def orga = organizationService.createOrganization(currentUser, createBuilder.build())
            //Pour ajouter une organisation à un projet il faut fournir l'organisation créée et l'id du projet cible
            addOrganizationBuilder.organization(orga)
            addOrganizationBuilder.serviceDeskId(1) //Id du portail MGDIS Support = 1
            organizationService.addOrganizationToServiceDesk(currentUser, addOrganizationBuilder.build())
        }
        index++
    }
}
