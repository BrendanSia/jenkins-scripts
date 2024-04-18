def alert(){
    def build_number = args[0]
    def build_date = args[1]
    def failed_stage = args[2]
    def chat_id = args[3]
    def bot_token = args[4]

    def alertMessage = "𝘽𝙪𝙞𝙡𝙙 𝙁𝙖𝙞𝙡𝙚𝙙! ❌\n\nBuild no: ${buildNumber}\nDate: ${buildDate.format('yyyy-MM-dd HH:mm:ss')}\n\nStage Failed: ${failedStage}\n\n"

    // Quality Gate specific details (optional)
    if (failed_stage == "Quality Gate") {
        def username = "admin"
        def password = "Bs2024"
        def authString = Base64.encoder.encodeToString("${username}:${password}".bytes).trim()

        def response = new URL("http://localhost:9000/api/qualitygates/project_status?projectKey=com.demo-project:brendan-sia&branch=main").text
        def jsonData = new JsonSlurper().parseText(response)

        def coverage = jsonData.projectStatus.conditions

        alert_message += "Details: \n"
        coverage.each { condition ->
            def metricKey = condition.metricKey
            def actualValue = condition.actualValue
            def errorThreshold = condition.errorThreshold

            if (metricKey == "coverage") {
            alert_message += "\t- ${metricKey}: ${actualValue}% | min: ${errorThreshold}% \n"
            } else {
            alert_message += "\t- ${metricKey}: ${actualValue}% | max: ${errorThreshold}% \n"
            }
        }
    }

    alert_message += "\nPlease refer to logs sent via email for more information."

    def url = "https://api.telegram.org/bot${bot_token}/sendMessage"
    def data = [
        chat_id: chat_id,
        text: alert_message,
        disable_notification: false
    ]

    def json = JsonOutput.toJson(data)

    def conn = new URL(url).openConnection()
    conn.setRequestProperty("Content-Type", "application/json")
    conn.doOutput = true
    conn.outputStream.write(json.bytes)

    conn.responseCode == 200 ? println("Message sent successfully!") : println("Failed to send message!")
}