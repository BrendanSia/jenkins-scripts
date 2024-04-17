#!/bin/bash

build_number=$1
build_date=$2
failed_stage=$3
chat_id=$4
bot_token=$5

alert_message="Build Passed! ✅\n"
alert_message+="\n"
alert_message+="Build no: ${build_number}\n"
alert_message+="Date: ${build_date}\n"

curl -X POST -H "Content-Type: application/json" -d "{\"chat_id\": \"${chat_id}\", \"text\": \"${alert_message}\", \"disable_notification\": false}" "https://api.telegram.org/bot${bot_token}/sendMessage"