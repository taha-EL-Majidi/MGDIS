import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.customfields.manager.OptionsManager
import com.atlassian.jira.issue.customfields.option.Options
import com.atlassian.jira.issue.customfields.option.Option
import com.atlassian.jira.issue.fields.config.FieldConfig
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.fields.CustomField
import groovyx.net.http.HTTPBuilder


log.error "Début de Traitement"

//REST API call
// Si l'adresse vient à changer il faut la changer ici
def http = new HTTPBuilder('http://intranet-mediation.oceanie.local/api/projet/CM/2021')
def response = http.request(GET, TEXT) {
    // utilisez un admin pour la connexion ou un agent Solution
    requestContentType = JSON
    headers.Accept = 'application/json'
}
log.error "Retour du call API"
def result = new groovy.json.JsonSlurper().parse(response)
def lbtiers = result.lbtiers
def lbprojet = result.lbproj
def idprojet = result.idproj

//récupérer le champ Tier/Projet
CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()
CustomField tierProjet = customFieldManager.getCustomFieldObject(12816)

//récupérer l'option manager
OptionsManager optionsManager = ComponentAccessor.getOptionsManager()
//récupérer la configuration du champ Tier/Projet
FieldConfig fieldConfig = tierProjet.getConfigurationSchemes().listIterator().next().getOneAndOnlyConfig()



//Parcourir tous les tiers du JSON
def indexJSON = 0
while (indexJSON <= lbtiers.size())
{
    //Vérifier l'existence du tier
    def tierOption = ComponentAccessor.optionsManager.getOptions(fieldConfig)?.find {
        it.toString() == lbtiers[indexJSON]
    }
    // Si le tier n'existe pas sur la liste des options du champ
    if (tierOption == null)
    {
        //Créer un tier dans la liste des options et le projet associé
        Option option = optionsManager.createOption(fieldConfig,1,1,lbtiers[indexJSON])
        Option optionChild = optionsManager.createOption(fieldConfig,option.getOptionId(),1,lbprojet[indexJSON])
    }
    // Si le tier existe sur la liste des options du champ
    else
    {
        //Vérifier l'existence du projet associé au tier
        def projetOption = tierOption.getChildOptions()?.find {
            it.toString() == lbprojet[indexJSON]
        }
        //Si le projet n'existe pas la liste des sous-option
        if (projetOption == null)
        {
            //créer le projet associé au tier
            Option optionChild = optionsManager.createOption(fieldConfig,tierOption.getOptionId(),1,lbprojet[indexJSON])
        }
    }
    indexJSON++
}


//parcourir toutes les options pour descativer les tiers qui ne sont plus dans le JSON
Options options = optionsManager.getOptions(fieldConfig)
def optionIndex = 0
while (optionIndex < options.size())
{
    def optionPresente = false
    tierIndex = 0
    while (tierIndex <= lbtiers.size){
        if (options[optionIndex].getValue() == lbtiers[tierIndex])
        {
            optionPresente = true
            tierIndex++
        }
        else
        {
            tierIndex++
        }
    }

    if (optionPresente == false)
    {
        optionsManager.disableOption(options[optionIndex])
    }
    optionIndex++
}
