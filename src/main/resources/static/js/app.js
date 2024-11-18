let stompClient = null;

window.onload = connect;

function connect() {
    const socket = new SockJS('/chat');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);

        stompClient.subscribe('/topic/public', function (messageOutput) {
            const messageData = JSON.parse(messageOutput.body);
            showMessage(messageData.sender, messageData.message, messageData.sender === "AI" ? "message-received" : "message-sent");
        });
    });
}

function sendMessage() {
    const messageContent = document.getElementById("message").value;
    if (messageContent && stompClient) {
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify({sender: "User", message: messageContent}));
        document.getElementById("message").value = '';
    }
}

function showMessage(sender, message, messageType) {
    let conversation = document.getElementById("conversation");
    if (!conversation) {
        conversation = document.createElement('div');
        conversation.id = "conversation";
        conversation.classList.add("chat-box");
        document.querySelector(".chat-container").appendChild(conversation);
    }

    const messageElement = document.createElement('div');
    messageElement.classList.add("chat-message", messageType);

    const messageContent = document.createElement('div');
    messageContent.classList.add("message-content");
    messageContent.innerText = `${sender}: ${message}`;

    messageElement.appendChild(messageContent);
    conversation.appendChild(messageElement);

    conversation.scrollTop = conversation.scrollHeight;
}