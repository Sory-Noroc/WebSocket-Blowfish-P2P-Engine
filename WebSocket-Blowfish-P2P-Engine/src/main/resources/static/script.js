const socket = new WebSocket(`ws://${window.location.host}/ws`);

socket.onopen = () => {
    console.log("Conectat la serverul Ktor");
    updateStatus("Online", "status-online");
    logSecurity("Conexiune WebSocket stabilită.");
};

socket.onmessage = (event) => {
    try {
        const data = JSON.parse(event.data);
        console.log("Mesaj JSON primit:", data);

        const type = data["$type"] || data["type"];

        if (type && type.includes("DiscoveryResponse")) {
            updatePartnersList(data.ips);
        } else if (data.ips) { 
            updatePartnersList(data.ips);
        } else {
            displayMessage("Server JSON", JSON.stringify(data));
        }
    } catch (e) {
        console.log("Mesaj text primit:", event.data);
        displayMessage("Server", event.data);
    }
};

socket.onclose = () => {
    updateStatus("Deconectat", "status-offline");
    logSecurity("Conexiune WebSocket închisă.");
};

function updateStatus(text, className) {
    const statusEl = document.getElementById("connection-status");
    if (statusEl) {
        statusEl.textContent = text;
        statusEl.className = className;
    }
}

function displayMessage(sender, text) {
    const chatBox = document.getElementById("chat-messages");
    const msgDiv = document.createElement("div");
    msgDiv.style.marginBottom = "10px";
    msgDiv.innerHTML = `<strong>${sender}:</strong> ${text}`;
    chatBox.appendChild(msgDiv);
    chatBox.scrollTop = chatBox.scrollHeight;
}

function logSecurity(message) {
    const logContainer = document.getElementById("log-container");
    if (logContainer) {
        const entry = document.createElement("div");
        entry.style.borderBottom = "1px solid #444";
        entry.style.padding = "2px 0";
        entry.textContent = `[${new Date().toLocaleTimeString()}] ${message}`;
        logContainer.appendChild(entry);
        logContainer.scrollTop = logContainer.scrollHeight;
    }
}

function updatePartnersList(ips) {
    const list = document.getElementById("partners-list");
    if (!list) return;
    
    list.innerHTML = "";
    if (ips.length === 0) {
        list.innerHTML = '<li class="empty">Niciun partener găsit</li>';
        return;
    }

    ips.forEach(ip => {
        const li = document.createElement("li");
        li.textContent = ip;
        li.style.cursor = "pointer";
        li.onclick = () => selectPartner(ip);
        list.appendChild(li);
    });
    logSecurity(`Scanare finalizată. S-au găsit ${ips.length} parteneri.`);
}

function selectPartner(ip) {
    document.getElementById("target-ip").textContent = ` -> ${ip}`;
    logSecurity(`Partener selectat: ${ip}. Se inițiază handshake...`);
    // Placeholder pentru viitor: socket.send(JSON.stringify({type: "CONNECT", target: ip}));
}

document.getElementById("send-btn").onclick = () => {
    const input = document.getElementById("message-input");
    if (input && input.value) {
        socket.send(input.value);
        displayMessage("Eu", input.value);
        input.value = "";
    }
};

document.getElementById("scan-btn").onclick = () => {
    const subnetInput = document.getElementById("subnet-input");
    const subnet = subnetInput ? subnetInput.value : "192.168.1.0/24";
    logSecurity(`Scanare pornită pentru ${subnet}...`);
    
    const request = {
        "$type": "com.example.blowfish.connection.P2PMessage.DiscoveryRequest",
        "subnet": subnet
    };
    socket.send(JSON.stringify(request));
};