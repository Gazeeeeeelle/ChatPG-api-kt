# Concept
Offer a **unified chat experience**, allowing users to chat with friends alongside an integrated AI assistant for real-time support, brainstorming, roleplaying, and more.

# Features & Data Structure


# Code & Performance
When using Postman to generate requests **sequentially**, **VisualVM** was used to analyze the performance of the **RESTful API**. While alternating between the creation of **bearer tokens** and the **fetching** of 20 messages from a chat, the results were:

<p>
  <img width="80%" alt="Non-parallel" src="https://github.com/user-attachments/assets/fcca11cc-fba1-4ab6-ad10-ceef4c62b6ed" />
</p>

The system maintained a **consistent memory footprint** and minimal CPU overhead, showing no signs of memory leakage.

While running **100 virtual users** simultaneously via Postman to request tokens and fetch messages, the results remained just as stable.

<p>
  <img width="80%" alt="PostmanGraphParallel" src="https://github.com/user-attachments/assets/3148a57a-23f4-4c81-ab8d-d2be6acf261f" />
  <img width="80%" alt="VisualVMGraphParallel" src="https://github.com/user-attachments/assets/555de369-3fca-4ec9-8b25-d5cb34424e2b" />
</p>

# Automated Tests
To ensure reliability, the RESTful API features unit and integration tests using **JUnit** and **Mockito**, covering all core business logic and validations.

<p>
  <img width="80%" alt="tests" src="https://github.com/user-attachments/assets/ac9e933c-e6e1-4c05-9cd3-dbd3c1c2da84" />
</p>

# Documentation
**Dokka**, the Kotlin standard, is used for documentation to ensure it is both readable and maintainable. All method documentation follows a strict 4-part pattern:

* **Intent**: What the method does and the rationale for its required parameters.
* **Parameters (@param)**: Detailed explanation of the parameters' purpose (expanded for complex objects) to avoid bloating the initial summary.
* **Returns (@return)**: Specifies the return type and exactly what data is returned.
* **Throws (@throws)**: Lists exceptions the method can throw, covering both misusage and warnings.

# Evolution & Post-Mortem

## Language Choice
When I first **conceptualized** this project, it was to be written entirely in **Java (JDK 17)**. After researching the advantages of **Kotlin**, I spent approximately 16 hours refactoring the entire codebase. This transition allowed for more **concise**, robust, and readable code, leveraging Kotlin's modern syntax to reduce boilerplate and improve maintainability.

<p>
  <img width="80%" alt="doc" src="https://github.com/user-attachments/assets/56eea3c1-b7a7-421e-bf3e-c904b11d2716" />
</p>