import org.kie.jenkins.jobdsl.templates.KogitoJobTemplate
import org.kie.jenkins.jobdsl.KogitoConstants
import org.kie.jenkins.jobdsl.Utils

folder(KogitoConstants.KOGITO_DSL_PULLREQUEST_FOLDER)

Map defaultJobParams = [
    job: [
        name: 'optaplanner',
        folder: KogitoConstants.KOGITO_DSL_PULLREQUEST_FOLDER
    ],
    git: [
        author: "${GIT_AUTHOR_NAME}",
        branch: "${GIT_BRANCH}",
        repository: 'optaplanner',
        credentials: "${GIT_AUTHOR_CREDENTIALS_ID}",
        token_credentials: "${GIT_AUTHOR_TOKEN_CREDENTIALS_ID}"
    ]
]

// Optaplanner
def optaplannerCheckParams = Utils.deepCopyObject(defaultJobParams)
optaplannerCheckParams.job.name = 'optaplanner'
optaplannerCheckParams.git.repository = 'optaplanner'
optaplannerCheckParams.pr = [ blackListTargetBranches: ['7.x'] ]
KogitoJobTemplate.createPRJob(this, optaplannerCheckParams)

// OptaWeb Employee rostering
def optawebEmployeeRosteringCheckParams = Utils.deepCopyObject(defaultJobParams)
optawebEmployeeRosteringCheckParams.job.name = 'optaweb-employee-rostering'
optawebEmployeeRosteringCheckParams.git.repository = 'optaweb-employee-rostering'
optawebEmployeeRosteringCheckParams.pr = [ whiteListTargetBranches: ['master'] ]
KogitoJobTemplate.createPRJob(this, optawebEmployeeRosteringCheckParams)

// OptaWeb Vehicle routing
def optawebVehicleRoutingCheckParams = Utils.deepCopyObject(defaultJobParams)
optawebVehicleRoutingCheckParams.job.name = 'optaweb-vehicle-routing'
optawebVehicleRoutingCheckParams.git.repository = 'optaweb-vehicle-routing'
optawebVehicleRoutingCheckParams.pr = [ whiteListTargetBranches: ['master'] ]
KogitoJobTemplate.createPRJob(this, optawebVehicleRoutingCheckParams)
