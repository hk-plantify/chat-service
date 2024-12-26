let socket = null;

window.onload = connect;

function connect() {
    const socket = new WebSocket("/ws/chat");

    socket.onopen = function () {
        console.log("Connected to WebSocket server");
    };

    socket.onmessage = function (event) {
        const messageData = JSON.parse(event.data);
        showMessage(messageData.sender, messageData.message, messageData.sender === "AI" ? "message-received" : "message-sent");
    };

    socket.onclose = function () {
        console.log("WebSocket connection closed");
        setTimeout(connect, 3000);
    };

    socket.onerror = function (error) {
        console.error("WebSocket error:", error);
    };
}

function sendMessage() {
    const messageContent = document.getElementById("message").value;
    if (messageContent) {
        if (socket && socket.readyState === WebSocket.OPEN) {
            showMessage("User", messageContent, "message-sent");
            const message = {
                sender: "User",
                message: messageContent
            };
            socket.send(JSON.stringify(message));
            document.getElementById("message").value = '';
        } else {
            console.error("WebSocket is not connected");
            alert("WebSocket 연결이 끊어졌습니다. 다시 시도하세요.");
        }
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