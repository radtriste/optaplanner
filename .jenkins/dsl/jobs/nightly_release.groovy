import org.kie.jenkins.jobdsl.templates.KogitoJobTemplate
import org.kie.jenkins.jobdsl.KogitoConstants
import org.kie.jenkins.jobdsl.Utils

branchFolder = "${KogitoConstants.KOGITO_DSL_NIGHTLY_RELEASE_FOLDER}/${JOB_BRANCH_FOLDER}"

folder(KogitoConstants.KOGITO_DSL_NIGHTLY_RELEASE_FOLDER)
folder(branchFolder)

defaultJobParams = [
    job: [
        name: 'optaplanner',
        folder: branchFolder
    ],
    git: [
        author: "${GIT_AUTHOR_NAME}",
        branch: "${GIT_BRANCH}",
        repository: 'optaplanner',
        credentials: "${GIT_AUTHOR_CREDENTIALS_ID}",
        token_credentials: "${GIT_AUTHOR_TOKEN_CREDENTIALS_ID}"
    ]
]

def getJobParams(String jobName, String jobDescription, String jenkinsfileName){
    def jobParams = Utils.deepCopyObject(defaultJobParams)
    jobParams.job.name=jobName
    jobParams.job.description=jobDescription
    jobParams.jenkinsfile=".jenkins/${jenkinsfileName}"
    return jobParams
}

// Deploy
KogitoJobTemplate.createPipelineJob(this, getJobParams('optaplanner-deploy', 'Optaplanner Deploy', 'Jenkinsfile.deploy')).with {
    parameters {
        stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')

        booleanParam('SKIP_TESTS', false, 'Skip tests')

        // Release information
        booleanParam('RELEASE', false, 'Is this build for a release?')
        stringParam('PROJECT_VERSION', '', 'Optional if not RELEASE. If RELEASE, cannot be empty.')
        stringParam('KOGITO_VERSION', '', 'Optional if not RELEASE. If RELEASE, cannot be empty.')
    }

    environmentVariables {
        env('JENKINS_EMAIL_CREDS_ID', "${JENKINS_EMAIL_CREDS_ID}")
        
        env('GIT_BRANCH_NAME', "${GIT_BRANCH}")
        env('GIT_AUTHOR', "${GIT_AUTHOR_NAME}")
        env('AUTHOR_CREDS_ID', "${GIT_AUTHOR_CREDENTIALS_ID}")
        env('GITHUB_TOKEN_CREDS_ID', "${GIT_AUTHOR_TOKEN_CREDENTIALS_ID}")
        env('GIT_AUTHOR_BOT', "${GIT_BOT_AUTHOR_NAME}")
        env('BOT_CREDENTIALS_ID', "${GIT_BOT_AUTHOR_CREDENTIALS_ID}")
        
        env('NEXUS_RELEASE_URL', "${MAVEN_NEXUS_RELEASE_URL}")
        env('NEXUS_RELEASE_REPOSITORY_ID', "${MAVEN_NEXUS_RELEASE_REPOSITORY}")
        env('NEXUS_STAGING_PROFILE_ID', "${MAVEN_NEXUS_STAGING_PROFILE_ID}")
        env('NEXUS_BUILD_PROMOTION_PROFILE_ID', "${MAVEN_NEXUS_BUILD_PROMOTION_PROFILE_ID}")

        env('MAVEN_SETTINGS_CONFIG_FILE_ID', "${MAVEN_SETTINGS_FILE_ID}")
        env('MAVEN_DEPLOY_REPOSITORY', "${MAVEN_ARTIFACTS_REPOSITORY}")
        env('MAVEN_DEPENDENCIES_REPOSITORY', "${MAVEN_ARTIFACTS_REPOSITORY}")
    }
}

// Promote pipeline
KogitoJobTemplate.createPipelineJob(this, getJobParams('optaplanner-promote', 'Optaplanner Promote', 'Jenkinsfile.promote')).with {
    parameters {
        stringParam('DISPLAY_NAME', '', 'Setup a specific build display name')
        
        // Deploy job url to retrieve deployment.properties
        stringParam('DEPLOY_BUILD_URL', '', 'URL to jenkins deploy build to retrieve the `deployment.properties` file. If base parameters are defined, they will override the `deployment.properties` information')
        
        // Release information which can override `deployment.properties`
        booleanParam('RELEASE', false, 'Override `deployment.properties`. Is this build for a release?')

        stringParam('PROJECT_VERSION', '', 'Override `deployment.properties`. Optional if not RELEASE. If RELEASE, cannot be empty.')
        stringParam('KOGITO_VERSION', '', 'Optional if not RELEASE. If RELEASE, cannot be empty.')

        stringParam('GIT_TAG', '', 'Git tag to set, if different from PROJECT_VERSION')
    }

    environmentVariables {
        env('JENKINS_EMAIL_CREDS_ID', "${JENKINS_EMAIL_CREDS_ID}")
        
        env('GIT_BRANCH_NAME', "${GIT_BRANCH}")
        env('GIT_AUTHOR', "${GIT_AUTHOR_NAME}")
        env('AUTHOR_CREDS_ID', "${GIT_AUTHOR_CREDENTIALS_ID}")
        env('GITHUB_TOKEN_CREDS_ID', "${GIT_AUTHOR_TOKEN_CREDENTIALS_ID}")
        env('GIT_AUTHOR_BOT', "${GIT_BOT_AUTHOR_NAME}")
        env('BOT_CREDENTIALS_ID', "${GIT_BOT_AUTHOR_CREDENTIALS_ID}")

        env('MAVEN_SETTINGS_CONFIG_FILE_ID', "${MAVEN_SETTINGS_FILE_ID}")
        env('MAVEN_DEPENDENCIES_REPOSITORY', "${MAVEN_ARTIFACTS_REPOSITORY}")
        env('MAVEN_DEPLOY_REPOSITORY', "${MAVEN_ARTIFACTS_REPOSITORY}")
    }
}