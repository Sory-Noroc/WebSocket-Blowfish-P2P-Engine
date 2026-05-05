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
        displayMessage("Server JSON", JSON.stringify(data));
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
    if (!chatBox) return;
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
    if (!ips || ips.length === 0) {
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
}

function selectPartner(ip) {
    document.getElementById("target-ip").textContent = ` -> ${ip}`;
    logSecurity(`Partener selectat: ${ip}. Se inițiază handshake...`);
}

document.getElementById("send-btn").onclick = () => {
    const input = document.getElementById("message-input");
    if (input && input.value) {
        socket.send(input.value);
        displayMessage("Eu", input.value);
        input.value = "";
    }
};

document.getElementById("scan-btn").onclick = async () => {
    const subnetInput = document.getElementById("subnet-input");
    const subnet = subnetInput ? subnetInput.value : "192.168.0.0/24";
    logSecurity(`Scanare pornită pentru ${subnet}...`);

    const list = document.getElementById("partners-list");
    list.innerHTML = "Se scanează...";

    try {
        const response = await fetch(`/ips?subnet=${encodeURIComponent(subnet)}`);
        if (!response.ok) throw new Error(`Server error: ${response.status}`);
        
        const ips = await response.json();
        updatePartnersList(ips);
        logSecurity(`Scanare finalizată. S-au găsit ${ips.length} parteneri.`);
    } catch (error) {
        console.error("Eroare la scanare:", error);
        logSecurity(`Eroare la scanare: ${error.message}`);
        list.innerHTML = '<li class="empty">Eroare la scanare</li>';
    }
};