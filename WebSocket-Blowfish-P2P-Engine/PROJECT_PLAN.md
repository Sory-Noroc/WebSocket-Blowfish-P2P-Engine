# Plan de Implementare: Sistem P2P cu Criptare Blowfish

Acest document conține pașii detaliați pentru realizarea sistemului de comunicare securizat.

## 1. Infrastructură Web & UI (Ktor + HTML/JS)
- [x] **Configurare Servire Fișiere Statice:** Setarea Ktor pentru a servi HTML/JS din folderul `resources`.
- [x] **Layout Principal:** Structură cu 3 coloane (Parteneri, Chat, Securitate).
- [x] **Integrare Frontend-Backend:** Conectarea paginii web la serverul Ktor prin WebSockets.
- [x] **Sistem de Notificări:** Log-uri în coloana de Securitate pentru status conexiune.

## 2. Descoperire și Conectivitate (Network)
- [x] **Trigger Scanare:** Buton în UI care apelează `NetworkUtils.discoverPeers`.
- [x] **Afișare Dinamică:** Popularea listei de IP-uri găsite în interfață.
- [x] **Initiere Conexiune:** Posibilitatea de a da click pe un IP pentru a pregăti canalul de comunicare.

## 3. Securitate și Criptare (Blowfish & DH)
- [x] **Handshake Diffie-Hellman:** 
    - [x] Generare pereche chei (Public/Private) la pornirea aplicației.
    - [x] Schimb de chei publice prin WebSocket automat la conectare.
    - [x] Calcularea Shared Secret-ului în ambele părți.
- [x] **Integrare Blowfish:**
    - [x] Inițializare `BlowfishEngine` folosind Shared Secret-ul obținut.
    - [x] Logica de Criptare/Decriptare pentru mesaje text.
    - [x] Vizualizare: Afișare în UI a statusului criptării.

## 4. Mesagerie Text (Real-time Chat)
- [x] **Trimitere Mesaj:** Input text + Criptare Blowfish + Trimitere prin WebSocket (via P2PMessage JSON).
- [x] **Recepție Mesaj:** Primire date criptate + Decriptare + Afișare în chat.
- [x] **Istoric Sesiune:** Mesajele rămân în UI pe durata sesiunii.

## 5. Transfer de Fișiere (Chuncking & Encryption)
- [x] **Componentă UI Upload:** Input de tip file ascuns, activat de butonul agrafă.
- [x] **Logica de Segmentare (Chuncking):** Spargerea fișierului în bucăți de 16KB.
- [x] **Trimitere Segmente:** Transmiterea chunk-urilor prin WebSocket folosind mesaje de tip `FileChunk`.
- [x] **Reconstrucție Fișier:** Primirea segmentelor și asamblarea lor în memorie.
- [x] **Indicator Progres:** Log-uri în UI pentru începutul și sfârșitul transferului.

## 6. Documentație și Validare
- [ ] **Schema Topologiei:** Generarea unei diagrame care să explice fluxul de date.
- [ ] **Verificare Vectori de Test:** Validarea implementării Blowfish contra vectorilor oficiali.
- [x] **Demo Mode:** Posibilitatea de a testa între două IP-uri diferite (rezolvat problema de Firewall).
