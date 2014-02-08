rapidbackend
============
<br/>
A social application engine and backend server for mobile/web applications. Written in Java 
<br/>
<br/>
Rapidbackend is a back-end framework for building your own mobile/web applications with social functions.
<br/>
<br/>
How to use:<br/>
0. first install mysql and redis on your machine. <br/>
&nbsp;0.1 apt-get install mysql<br/>
&nbsp;0.2 wget http://download.redis.io/releases/redis-2.8.2.tar.gz<br/>
&nbsp;0.3 tar -xzvf redis-2.8.2.tar.gz<br/>
&nbsp;0.4 cd redis-2.8.2<br/>
&nbsp;0.5 make;sudo make install<br/>
1. Download from git.<br/>
2. unzip rapidbackend.zip<br/>
3. cd into rapidbackend<br/>
4. gradle startInstallServer<br/>
5. open http://localhost:10888 with your browser<br/>
&nbsp;5.1 input your mysql user name and password<br/>
&nbsp;5.2 design your own models in the panel<br/>
&nbsp;5.3 design the data model of the social service you want to create<br/>
&nbsp;5.4 click install and generate models and services<br/>
6. gradle startRapidbackend (your backend server is ready )<br/>
<br/>
Current version is 0.1<br/>

The search function and GEO function will be comitted in next release.
More detailed documents coming soon<br/>