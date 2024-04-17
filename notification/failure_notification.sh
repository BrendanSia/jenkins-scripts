#!/bin/bash

build_number=$1
build_date=$2
failed_stage=$3

alert_message="Build Failed! ❌\n"
alert_message+="\n"
alert_message+="Build no: ${build_number}\n"
alert_message+="Date: ${build_date}\n"
alert_message+="\n"
alert_message+="Stage Failed: ${failed_stage}\n"

# Quality Gate specific details (optional)
if [[ "${failed_stage}" == "Quality Gate" ]]; then
  username="admin"
  password="Bs2024"
  auth_string=$(echo -n "${username}:${password}" | base64)

  response=$(curl -sSL -u "${auth_string}" 'http://localhost:9000/api/qualitygates/project_status?projectKey=com.demo-project:brendan-sia&branch=main')
  json_data=$(echo "${response}" | jq .)

  coverage=$(echo "${json_data}" | jq '.projectStatus.conditions')

  alert_message+="Details: \n"
  while IFS= read -r condition; do
    metric_key=$(echo "${condition}" | jq '.metricKey')
    actual_value=$(echo "${condition}" | jq '.actualValue')
    error_threshold=$(echo "${condition}" | jq '.errorThreshold')

    if [[ "${metric_key}" == "coverage" ]]; then
      alert_message+="\t- ${metric_key}: ${actual_value}% | min: ${error_threshold}% \n"
    else
      alert_message+="\t- ${metric_key}: ${actual_value}% | max: ${error_threshold}% \n"
    fi
  done <<< "${coverage}"
fi

alert_message+="\nPlease refer to logs sent via email for more information."

curl -X POST -H "Content-Type: application/json" -d "{\"chat_id\": \"-1002005133194\", \"text\": \"${alert_message}\", \"disable_notification\": false}" "https://api.telegram.org/bot7134798871:AAFpwT4nsKTfY19XFR4dapq3XQTpq0lQZFc/sendMessage"

