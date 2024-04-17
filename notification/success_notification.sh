#!/bin/bash

build_number=$1
build_date=$2
failed_stage=$3

alert_message="Build Passed! ✅\n"
alert_message+="\n"
alert_message+="Build no: ${build_number}\n"
alert_message+="Date: ${build_date}\n"

curl -X POST -H "Content-Type: application/json" -d "{\"chat_id\": \"-1002005133194\", \"text\": \"${alert_message}\", \"disable_notification\": false}" "https://api.telegram.org/bot7134798871:AAFpwT4nsKTfY19XFR4dapq3XQTpq0lQZFc/sendMessage"