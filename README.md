# 📱 VulnForum – Podatna aplikacja mobilna (Android + Flask)

**VulnForum** to celowo podatna aplikacja mobilna typu forum, służąca do nauki testowania bezpieczeństwa aplikacji Android.  
Projekt składa się z aplikacji mobilnej napisanej w **Jetpack Compose** oraz backendu we **Flask (Python)**.

Użytkownicy mogą przeglądać i publikować artykuły, komentować je oraz wysyłać sobie nawzajem wiadomości.

---

## 🔐 Funkcje aplikacji / Podatności

Projekt zawiera wiele typowych błędów bezpieczeństwa aplikacji mobilnych, m.in.:

- 📤 Eksportowane aktywności  
- 💉 SQL Injection  
- 🔥 XSS  
- 🔓 Brak autoryzacji lub nieprawidłowa walidacja tokenów  
- 🧊 Nieprawidłowe szyfrowanie i przechowywanie danych  
- 🔗 Deeplinki z możliwością nadużycia  
- 📡 Broadcast receivers podatne na przejęcie  
- 🐛 Nadmiarowe informacje w logach i komunikatach błędów  
- 🌐 Brak ograniczeń CORS / nieprawidłowa konfiguracja metod HTTP

---

## ▶️ Uruchomienie

### Backend (Flask)

```bash  
cd backend  
./start.sh
```
Skrypt ten zainstaluje zależności i uruchomi lokalny serwer Flask API pod adresem:  
**http://127.0.0.1:5000**

---

### 📱 Frontend (APK)

- Zbuduj aplikację w Android Studio **lub** użyj gotowego pliku `.apk`.
- Zainstaluj aplikację na emulatorze lub fizycznym urządzeniu:

```bash
adb install VulnForum.apk
```

### 🎯 Wyzwania

- 🔓 Uzyskaj dostęp do płatnego artykułu  
- ✉️ Wyślij wiadomość w imieniu innego użytkownika  
- 🛡️ Uzyskaj uprawnienia administratora  
- 💰 Zwiększ saldo portfela do `100 000`
- 🕵️‍♂️ Odczytaj tokeny innych użytkowników


<p align="center">
  <img src="assets/vulnforum.png" alt="Zrzut ekranu" width="400"/>
</p>
