<h2> Concept </h2>
Offer an unified chat experience, allowing users to chat with friends alongside an integrated AI assistant for real-time support, brainstorming, roleplaying and whatever you are looking for.

<h2> Features </h2>

<h2> Code & Performance </h2>
<p>
  When using Postman to generate requests sequencially, VisualVM was used to analyse the performance of the <i>RESTful API</i>. While alternating between the creation of <i>bearer tokens</i> and fething of 20 messages from a chat, the results were:
</p>
<img width="80%" alt="Non-parellel" src="https://github.com/user-attachments/assets/fcca11cc-fba1-4ab6-ad10-ceef4c62b6ed" />
<p> 
  We can see it maintained a consistent memory footprint and minimal CPU overhead. It did not present signs of memory leakage, as well. 
</p>
<p/>
<p> 
  Though, while running 100 Virtual Users at a time from Postman, requesting tokens and fetching messages, the results were stable, but clearly increased the CPU overhead.
</p>
<img width="80%" alt="PostmanGraphParallel" src="https://github.com/user-attachments/assets/3148a57a-23f4-4c81-ab8d-d2be6acf261f" />
<img width="80%" alt="VisualVMGraphParallel" src="https://github.com/user-attachments/assets/555de369-3fca-4ec9-8b25-d5cb34424e2b" />

<h2> Automated Tests</h2>
To ensure reliability, the RESTful API features tests using JUnit and Mockito, covering all of the core validations.
<img width="80%" alt="tests" src="https://github.com/user-attachments/assets/ac9e933c-e6e1-4c05-9cd3-dbd3c1c2da84" />

<h2> Documentation </h2>
Dokka is used for documentation, making it both readable and simple to write.
The documentation always follows the pattern:
<p/>
<p>
  -> What this does and why it needs the parameters received.
</p><p>
  -> Parameters: Further attention to parameters purpose (usually longer when more complex) not to bloat the first section
</p><p>
  -> Returns: Mentions the type returned alongside what exactly it returns.
</p><p>
  -> Throws: Exceptions the method can throw both on misusage and/or as warning.
</p>
<p/>


<h2> Evolution & Post-Mortems </h2>
<p>
  When I first idealized the project, it was meant to be entirely written in Java (JDK 17), and for a good time it was, until I considered Kotlin. Searching further, i found out <i> why </i> Kotlin. After around 16 hours of active work, I have rewritten the whole code into this modern, robust and honestly, beautiful language.
</p>
<p>
  Kotlin offers you the possiblity of writting much more consize, clean (and therefore readable), straight forward code. 
</p>

<!--
Documentation:
Dokka is used for documentation, making it both readable and simple to write.
The documentation always follows the pattern:
-> What this does and why it needs the parameters received.
-> Parameters: Further attention to parameters purpose (usually longer when more complex) not to bloat the first section
-> Returns: Mentions the type returned alongside what exactly it returns.
-> Throws: Exceptions the method can throw both on misusage and/or as warning.
The previous Java codebase:
I have choosen Java (JDK 17) when i started this project, and later on, before too many features were added, I decided to switch it to a more modern approuch: Kotlin.
The whole process took around 12 hours of total active work. I do not regret doing that and I am glad I did it.
Null safety, infix functions, 'let', 'run', cleaner lambdas, and reduction of boiler plate code are the reasons that made me enjoy much more the development of this personal project.
Automated tests:
To ensure reliability, the RESTful API features tests using JUnit and Mockito, covering all of the core validations.
Performance analysis:
Under simulated stress, maintained a consistent memory footprint and minimal CPU overhead. Did not present signs of memory leakage.
Front-end's design:
Users can navigate their accessible chats via a sidebar. In the header, there is the authentication portal, serving as a trigger for login/logout and token requests.
-->

<img width="80%" alt="doc" src="https://github.com/user-attachments/assets/56eea3c1-b7a7-421e-bf3e-c904b11d2716" />

