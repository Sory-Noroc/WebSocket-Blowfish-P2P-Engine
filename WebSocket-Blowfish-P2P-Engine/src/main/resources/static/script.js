
document.addEventListener("DOMContentLoaded", () => {
    updateStatus("Sistem P2P Pornit", "status-online");
    logSecurity("Interfață inițializată. Folosiți Scanare pentru a găsi parteneri.");

    setInterval(pollMessages, 2000);
    setInterval(pollStatus, 3000);
});

async function pollMessages() {
    try {
        const response = await fetch('/messages');
        if (response.ok) {
            const messages = await response.json();
            messages.forEach(msg => {
                if (msg.startsWith("DOWNLOAD_READY:")) {
                    const fileName = msg.split(":")[1];
                    displayFileDownload(fileName);
                } else {
                    displayMessage("Partener", msg);
                }
            });
        }
    } catch (e) { console.error(e); }
}

function displayFileDownload(fileName) {
    const chatBox = document.getElementById("chat-messages");
    const msgDiv = document.createElement("div");

    msgDiv.style.cssText = "background: #1e272e; border-left: 4px solid #00d2d3; padding: 12px; margin: 8px 0; border-radius: 5px; color: white;";

    msgDiv.innerHTML = `
        <div style="margin-bottom: 8px;"><strong>📂 Fișier primit:</strong> ${fileName}</div>
        <a href="/download" target="_blank" 
           style="background: #00d2d3; color: #222; padding: 5px 15px; text-decoration: none; border-radius: 3px; font-weight: bold; font-size: 13px;">
           DESCARCĂ FIȘIER
        </a>
    `;

    chatBox.appendChild(msgDiv);
    chatBox.scrollTop = chatBox.scrollHeight;
    logSecurity(`Fisierul ${fileName} este gata pentru descarcare.`);
}

async function pollStatus() {
    try {
        const response = await fetch('/status');
        if (response.ok) {
            const status = await response.json();
            document.getElementById("my-pub-key").textContent = status.myPublicKey;
            document.getElementById("partner-pub-key").textContent = status.partnerPublicKey;
            
            if (status.connected) {
                updateStatus("Conectat", "status-online");
            } else {
                updateStatus("Așteptare conexiune", "status-offline");
            }

            if (status.partnerPublicKey !== "N/A") {
                document.getElementById("shared-secret").textContent = "Generat (Criptare Activă)";
            }
        }
    } catch (e) {
        console.error("Eroare la polling status:", e);
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

document.getElementById("file-input").onchange = async (event) => {
    const file = event.target.files[0];
    if (!file) return;

    logSecurity(`Se trimite fișierul: ${file.name}...`);
    const formData = new FormData();
    formData.append("file", file);

    try {
        const response = await fetch('/send-file', {
            method: 'POST',
            body: formData
        });

        if (response.ok) {
            logSecurity(`Fișierul ${file.name} a fost trimis.`);
            displayMessage("Eu", `Fișier trimis: ${file.name}`);
        } else {
            logSecurity(`Eroare la trimitere fișier: ${response.statusText}`);
        }
    } catch (e) {
        logSecurity(`Eroare rețea la trimitere fișier: ${e.message}`);
    }
    
    // Reset input
    event.target.value = "";
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