# Plan de Implementare: Sistem P2P cu Criptare Blowfish

Acest document conține pașii detaliați pentru realizarea sistemului de comunicare securizat.

## 1. Infrastructură Web & UI (Ktor + HTML/JS)
- [x] **Configurare Servire Fișiere Statice:** Setarea Ktor pentru a servi HTML/JS din folderul `resources`.
- [x] **Layout Principal:** Structură cu 3 coloane (Parteneri, Chat, Securitate).
- [x] **Integrare Frontend-Backend:** Conectarea paginii web la serverul Ktor prin WebSockets.
- [ ] **Sistem de Notificări:** Toast-uri/Alerte pentru status conexiune (Handshake, File Received).

## 2. Descoperire și Conectivitate (Network)
- [ ] **Trigger Scanare:** Buton în UI care apelează `NetworkUtils.discoverPeers`.
- [ ] **Afișare Dinamică:** Popularea listei de IP-uri găsite în interfață.
- [ ] **Inițiere Conexiune:** Posibilitatea de a da click pe un IP pentru a deschide un canal de comunicare.

## 3. Securitate și Criptare (Blowfish & DH)
- [ ] **Handshake Diffie-Hellman:** 
    - [ ] Generare pereche chei (Public/Private) la pornirea aplicației.
    - [ ] Schimb de chei publice prin WebSocket automat la conectare.
    - [ ] Calcularea Shared Secret-ului în ambele părți.
- [ ] **Integrare Blowfish:**
    - [ ] Inițializare `BlowfishEngine` folosind Shared Secret-ul obținut.
    - [ ] Logica de Criptare/Decriptare pentru mesaje text (String -> Hex/Base64).
    - [ ] Vizualizare: Afișare în UI a textului criptat înainte de trimitere.

## 4. Mesagerie Text (Real-time Chat)
- [ ] **Trimitere Mesaj:** Input text + Criptare Blowfish + Trimitere prin WebSocket.
- [ ] **Recepție Mesaj:** Primire date criptate + Decriptare + Afișare în chat.
- [ ] **Istoric Sesiune:** Salvarea mesajelor în memorie pentru durata sesiunii.

## 5. Transfer de Fișiere (Chuncking & Encryption)
- [ ] **Componentă UI Upload:** Input de tip file cu drag-and-drop.
- [ ] **Logica de Segmentare (Chuncking):** Spargerea fișierului în bucăți de dimensiune fixă (ex: 16KB).
- [ ] **Criptare pe segmente:** Criptarea fiecărui chunk folosind Blowfish.
- [ ] **Reconstrucție Fișier:** Primirea segmentelor, decriptarea lor și asamblarea într-un Blob pentru descărcare.
- [ ] **Indicator Progres:** Update în timp real al procentului de transfer în UI.

## 6. Documentație și Validare
- [ ] **Schema Topologiei:** Generarea unei diagrame care să explice fluxul de date.
- [ ] **Verificare Vectori de Test:** Validarea implementării Blowfish contra vectorilor oficiali.
- [ ] **Demo Mode:** Script/Buton pentru a simula un schimb de mesaje între 2 instanțe locale.
