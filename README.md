# Web Automation Framework – Selenium + TestNG

This is a scalable end-to-end web automation framework built using **Java, Selenium WebDriver, and TestNG** for automating the **Advantage Online Shopping** web application.  
The framework follows industry best practices such as **Page Object Model (POM)**, centralized driver management, and detailed failure diagnostics using **Allure Reports**.

---

## 📌 Features

✅ Selenium 4 based UI automation  
✅ Page Object Model (POM) architecture  
✅ TestNG based execution  
✅ Centralized WebDriver management  
✅ Headless Chrome execution (CI-ready)  
✅ Soft Assertions for comprehensive validation  
✅ Allure Reporting with screenshots & page source on failure  
✅ Data-driven utilities (JSON, Faker)  
✅ Retry Analyzer for flaky tests  
✅ Modular, maintainable, and extensible design  

---

## 📂 Project Structure
```
AdvantageOnlineShopping_Web_Automation
├── Resources
│   ├── Drivers
│   │   └── chromedriver
│   ├── TestData
│   │   └── testdata.json
│   └── testng.xml
│
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── Constant
│   │   │   ├── Core
│   │   │   ├── Enum
│   │   │   ├── Pages
│   │   │   └── Util
│   │   └── resources
│   │
│   └── test
│       └── java
│           ├── AccountTests.java
│           ├── CartTests.java
│           ├── CheckoutTests.java
│           ├── LoginTests.java
│           ├── OrderTests.java
│           ├── RegistrationTests.java
│           └── SearchTests.java
│
├── config.properties
├── pom.xml
---
```
## 🧪 Test Coverage

The automation suite covers the following functional areas:

- User Registration validations
- Login (valid & invalid scenarios)
- Product Search
- Cart operations
- Checkout flow
- Account management
- Order verification

---

🚀 How to Run
Clone the Repository
```bash
git clone https://github.com/Niraj98-QA/AdvantageOnlineShopping_Web_Automation.git
```

Run Tests Using Maven
```bash
mvn clean test
```

🧰 Tech Stack
Language: Java 21
UI Automation: Selenium WebDriver 4
Test Framework: TestNG
Build Tool: Maven
Reports: Allure
Design Pattern: Page Object Model (POM)
Browser: Chrome (Headless supported)

🔮 Future Enhancements

CI/CD integration (GitHub Actions / Jenkins)

👤 Author

Niraj Fegade
Automation / SDET Engineer
Java | Selenium | TestNG | API & Web Automation




