const socket = new WebSocket(`ws://${window.location.host}/ws`);

socket.onopen = () => {
    console.log("Conectat la serverul Ktor");
    updateStatus("Online", "status-online");
    logSecurity("Conexiune WebSocket stabilită.");
};

socket.onmessage = (event) => {
    const msg = event.data;
    console.log("Mesaj primit:", msg);
    // Aici vom trata mesajele primite de la backend
    displayMessage("Server", msg);
};

socket.onclose = () => {
    updateStatus("Deconectat", "status-offline");
};

function updateStatus(text, className) {
    const statusEl = document.getElementById("connection-status");
    statusEl.textContent = text;
    statusEl.className = className;
}

function displayMessage(sender, text) {
    const chatBox = document.getElementById("chat-messages");
    const msgDiv = document.createElement("div");
    msgDiv.innerHTML = `<strong>${sender}:</strong> ${text}`;
    chatBox.appendChild(msgDiv);
    chatBox.scrollTop = chatBox.scrollHeight;
}

function logSecurity(message) {
    const logContainer = document.getElementById("log-container");
    const entry = document.createElement("div");
    entry.textContent = `[${new Date().toLocaleTimeString()}] ${message}`;
    logContainer.appendChild(entry);
    logContainer.scrollTop = logContainer.scrollHeight;
}

document.getElementById("send-btn").onclick = () => {
    const input = document.getElementById("message-input");
    if (input.value) {
        socket.send(input.value);
        displayMessage("Eu", input.value);
        input.value = "";
    }
};

document.getElementById("scan-btn").onclick = () => {
    const subnet = document.getElementById("subnet-input").value;
    logSecurity(`Scanare pornită pentru ${subnet}...`);
    // Vom trimite o comandă de scanare către backend prin WS
    socket.send(JSON.stringify({ type: "SCAN", subnet: subnet }));
};