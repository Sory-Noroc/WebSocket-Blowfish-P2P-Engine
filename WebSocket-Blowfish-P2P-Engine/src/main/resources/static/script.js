
document.addEventListener("DOMContentLoaded", () => {
    updateStatus("Sistem P2P Pornit", "status-online");
    logSecurity("Interfață inițializată. Folosiți Scanare pentru a găsi parteneri.");

    setInterval(pollMessages, 2000);
});

async function pollMessages() {
    try {
        const response = await fetch('/messages');
        if (response.ok) {
            const messages = await response.json();
            messages.forEach(msg => {
                displayMessage("Partener", msg);
                logSecurity("Mesaj nou primit de la partener.");
            });
        }
    } catch (e) {
        console.error("Eroare la polling mesaje:", e);
    }
}

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

async function selectPartner(ip) {
    document.getElementById("target-ip").textContent = ` -> ${ip}`;
    logSecurity(`Inițiere conexiune P2P către: ${ip}...`);
    
    try {
        const response = await fetch('/connect', {
            method: 'POST',
            body: ip
        });
        if (response.ok) {
            logSecurity(`Cerere de conectare trimisă către ${ip}.`);
        } else {
            logSecurity(`Eroare la conectare: ${response.statusText}`);
        }
    } catch (e) {
        logSecurity(`Eroare rețea la conectare: ${e.message}`);
    }
}

document.getElementById("send-btn").onclick = async () => {
    const input = document.getElementById("message-input");
    if (input && input.value) {
        const text = input.value;
        try {
            const response = await fetch('/send', {
                method: 'POST',
                body: text
            });
            if (response.ok) {
                displayMessage("Eu", text);
                input.value = "";
            } else {
                logSecurity("Eroare: Nu există o conexiune activă.");
            }
        } catch (e) {
            logSecurity(`Eroare la trimitere: ${e.message}`);
        }
    }
};

document.getElementById("scan-btn").onclick = async () => {
    const subnetInput = document.getElementById("subnet-input");
    const subnet = subnetInput ? subnetInput.value : "192.168.0.0/24";
    logSecurity(`Scanare rețea pornită pentru ${subnet}...`);

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