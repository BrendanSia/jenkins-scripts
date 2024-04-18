import groovy.json.JsonSlurper

def alert(String chatId, String botToken, int buildNumber, String username, String password, String failedStage) {
    def alertMessage = "𝘽𝙪𝙞𝙡𝙙 𝙁𝙖𝙞𝙡𝙚𝙙! ❌\n\nBuild no: ${buildNumber}\nDate: ${new Date().format('yyyy-MM-dd HH:mm:ss')}\n\nStage Failed: ${failedStage}\n\n"

    if (failedStage == "Quality Gate") {
        def authString = "${username}:${password}".bytes.encodeBase64().toString()

        def response = httpRequest(
            url: 'http://localhost:9000/api/qualitygates/project_status?projectKey=com.demo-project:brendan-sia&branch=main',
            method: 'GET',
            customHeaders: [[name: 'Authorization', value: "Basic ${authString}"]]
        )
        def jsonSlurper = new JsonSlurper()
        def jsonData = jsonSlurper.parseText(response.content)

        def coverage = jsonData.projectStatus.conditions
        alertMessage += "Details: \n"
        coverage.each { condition ->
            if (condition.metricKey == "coverage") {
                alertMessage += "\t- ${condition.metricKey}: ${condition.actualValue}% | min: ${condition.errorThreshold}% \n"
            } else {
                alertMessage += "\t- ${condition.metricKey}: ${condition.actualValue}% | max: ${condition.errorThreshold}% \n"
            }
        }
    }

    alertMessage += "\n𝙋𝙡𝙚𝙖𝙨𝙚 𝙧𝙚𝙛𝙚𝙧 𝙩𝙤 𝙡𝙤𝙜𝙨 𝙖𝙩𝙩𝙖𝙘𝙝𝙚𝙙 𝙞𝙣 𝙚𝙢𝙖𝙞𝙡 𝙛𝙤𝙧 𝙢𝙤𝙧𝙚 𝙞𝙣𝙛𝙤𝙧𝙢𝙖𝙩𝙞𝙤𝙣."

    sh "curl -X POST -H \"Content-Type: application/json\" -d '{\"chat_id\": \"${chatId}\", \"text\": \"${alertMessage}\", \"disable_notification\": false}' \"https://api.telegram.org/bot${botToken}/sendMessage\""
}