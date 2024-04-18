def call(String status, String buildNumber, String buildDate, String failedStage = null, String chatId, String botToken) {
    script {
        if (status == 'SUCCESS') {
            sh """
                sh src/main/scripts/success_notification.sh "${buildNumber}" "${buildDate}" "${chatId}" "${botToken}"
            """
        } else if (status == 'FAILURE') {
            sh """
                sh src/main/scripts/failure_notification.sh "${buildNumber}" "${buildDate}" "${failedStage}" "${chatId}" "${botToken}"
            """
        } else {
            error "Invalid status provided. Supported values: SUCCESS, FAILURE"
        }
    }
}
