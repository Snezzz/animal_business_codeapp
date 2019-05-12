"use strict";

const app = angular.module("workModule", ["ui.bootstrap"]);

app.controller("workCtrl", function ($http, $interval) {
    const workApp = this;
    const apiBaseURL = "/api/work/";

    $http.get(apiBaseURL + "me").then((response) => workApp.thisNode = response.data.me);
    $http.get(apiBaseURL + "peers").then((response) => workApp.peers = response.data.peers);

    workApp.updateMessages = () => {
        $http.get(apiBaseURL + "messages").then((response) => {
            // We build an empty dictionary mapping peers to their messages.
            workApp.messages = {};
            workApp.peers.forEach(function (peer) {
                workApp.messages[peer] = [];
            });

            // We fill the dictionary.
            response.data.forEach(function (data) {
                const message = data.state.data;
                let counterparty = message.target;
                let sender = "Me: ";
                if (message.origin != workApp.thisNode) {
                    counterparty = message.origin;
                    sender = "Them: ";
                }

                // We display the messages in reverse chronological order.
                workApp.messages[counterparty].unshift([sender, message.message]);
            });
        });
    };

    workApp.send = (recipient) => {
        const sendMsgEndpoint = apiBaseURL + `message?target=${recipient}&message=${workApp.form.message}`;
        $http.get(sendMsgEndpoint).then(() => {}, () => {});
        // We reset the text input.
        workApp.form.message = "";
    };

    // We poll the server for new messages every 100 milliseconds.
    $interval(workApp.updateMessages, 100);
});