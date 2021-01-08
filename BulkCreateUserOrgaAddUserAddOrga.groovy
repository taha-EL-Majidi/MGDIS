import com.atlassian.servicedesk.api.organization.OrganizationService
import com.onresolve.scriptrunner.runner.customisers.PluginModule
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.bc.user.UserService

@PluginModule OrganizationService organizationService

// récupérer l'utilisateur connecté qui lance le script (doit être admin)
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

//créer le builder qui permet de créer des organisations
def createBuilder = organizationService.newCreateBuilder()
//créer le builder qui permet d'ajouter des users dans une organisation
def addUsersToOrgaBuilder = organizationService.newUsersOrganizationUpdateParametersBuilder()
//créer le builder qui permet d'ajouter des organisations à des projets
def addOrganizationBuilder = organizationService.newOrganizationServiceDeskUpdateParametersBuilder()


//récupérer le fichier qui contient la liste des organisations et le parcourir
new File("/opt/atlassian/jira/temp/organisationsUsers.csv").withReader('UTF-8') { reader ->
    def line
    def index = 0
    while ((line = reader.readLine()) != null) {
        if (index >0){
            def cols = line.split(";")
            //récupérer les variables necessaire
            def displayName = cols[0]
            def emailAdress = cols[1]
            def organisation = cols[2]

            createBuilder.name(organisation)
            //Lancer le build pour créer l'organisation qui retourne l'objet organisation dans la varibale "orga"
            def orga = organizationService.createOrganization(currentUser, createBuilder.build())

            //Pour ajouter une organisation à un projet il faut fournir l'organisation créée et l'id du projet cible
            addOrganizationBuilder.organization(orga)
            addOrganizationBuilder.serviceDeskId(1) //Id du portail MGDIS Support = 1
            organizationService.addOrganizationToServiceDesk(currentUser, addOrganizationBuilder.build())

            //créer le client
            ApplicationUser newUser = createUserFt(currentUser,displayName,emailAdress)

            //ajouter l'utilisateur à l'organisation
            Set<ApplicationUser> users = new HashSet<ApplicationUser>();
            users.add(newUser)
            addUsersToOrgaBuilder.users(users)
            addUsersToOrgaBuilder.organization(orga)
            organizationService.addUsersToOrganization(currentUser, addUsersToOrgaBuilder.build())
        }
        index++
    }
}

//la fonction de création d'utilisateur
ApplicationUser createUserFt(ApplicationUser currentUser, String displayName, String emailAdress)
{
    //récupérer le service de gestion des utilisateurs
    def userService = ComponentAccessor.getComponent(UserService)

    //définir un mot de passe vide
    def password = ""

    //requet de création de l'utilisateur
    def newCreateRequest = UserService.CreateUserRequest.withUserDetails(currentUser,emailAdress,password,emailAdress,displayName)
            .sendNotification(true)

    //création du check et vérification du resultat de la requête
    def createValidationResult = userService.validateCreateUser(newCreateRequest)
    assert createValidationResult.isValid() : createValidationResult.errorCollection

    //Création de l'utilisateur et retour du resultat de la requête
    userService.createUser(createValidationResult)

    //Retourner l'utilisateur créé
    return ComponentAccessor.getUserUtil().getUserByName(emailAdress)
}
