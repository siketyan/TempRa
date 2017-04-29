# TempMonitor
Monitors the temperature, the humidity and the pressure in your room on Raspberry Pi.

## Features
- Monitors every 10 minutes and insert into MySQL database.
- Displays a graph and realtime data on the web browser.
- Tweets the data every hour automatically.

## Requirements
- Raspberry Pi (Tested on RPi 2 Model B)
- Operating system on Raspberry Pi what can run Java (Tested on Raspbian)
- Java Runtime Environment (JRE) 1.8+ (Tested on 1.8.0 Update 65)
- BME280 temperature/humidity/pressure module

## Usage
First, connect your BME280 to your Raspberry Pi with I2C.  
Before running, you have to put the config files below into the same directory as the jar file.
- monitor.properties
```properties
SQL_Type=mysql
SQL_Host=[YOUR SQL HOSTNAME HERE]
SQL_Port=[YOUR SQL PORT HERE]
SQL_Database=[YOUR SQL DATABASE NAME HERE]
SQL_User=[YOUR SQL USERNAME HERE]
SQL_Password=[YOUR SQL PASSWORD HERE]
SQL_Timeout=1

Socket_Interval=1000
Socket_Port=8888
HTTP_Port=8080

Format_Record={{STR1}}:{{STR2}}
Format_Hour={{STR1}}, {{STR2}}h
Format_Day={{STR1}}/{{STR2}}
Format_Month={{STR2}}, {{STR1}}
Format_Year={{STR1}}

Twitter_DateFormat=yyyy/M/d H:mm
Twitter_Content=Now: {{DATE}}\n \
                \n \
                Temperature: {{TEMP}} °C\n \
                Humidity: {{HUM}} %\n \
                Pressure: {{PRES}} hPa\n \
                \n \
                \#TempMonitor \#AutoTweet
```
- twitter4j.properties
```properties
oauth.consumerKey=[YOUR TWITTER CONSUMER KEY HERE]
oauth.consumerSecret=[YOUR TWITTER CONSUMER SECRET HERE]
oauth.accessToken=[YOUR TWITTER ACCESS TOKEN HERE]
oauth.accessTokenSecret=[YOUR TWITTER ACCESS TOKEN SECRET HERE]
```
Next, import [setup.sql](https://raw.githubusercontent.com/Siketyan/TempMonitor/master/setup.sql) into you database.  
Finally, type the command to run:
```bash
java -jar TempMonitor-x.x-jar-with-dependencies.jar
```

## Web Console
Type the address of your Raspberry Pi and specificed port number to access the console.  
e.g.)  
  `http://192.168.11.81:8080/` _# By IP address_  
  `http://raspberry:8080/` _# By hostname_

## Open Source Licenses
- [Pi4J](http://www.pi4j.com/)

  > GNU Lesser General Public License v3.0  
  >   
  > Copyright (c) 2012-2017 Pi4J  
  > https://github.com/Pi4J/pi4j/blob/master/LICENSE.txt

- [ControlEverythingCommunity/BME280](https://github.com/ControlEverythingCommunity/BME280)

  > Distributed with a free-will license.  
  > Use it any way you want, profit or free, provided it fits in the licenses of its associated works.

- [cron4j](http://www.sauronsoftware.it/projects/cron4j/)

  > GNU Lesser General Public License v2.1  
  >   
  > Copyright (c) 2007-2012 Sauron Software  
  > http://www.gnu.org/licenses/lgpl-2.1.html

- [Twitter4J](http://twitter4j.org/)

  > Apache License 2.0  
  >   
  > Copyright (c) 2007 Yusuke Yamamoto  
  > https://github.com/yusuke/twitter4j/blob/master/LICENSE.txt

- [Jetty](http://www.eclipse.org/jetty/)

  > Jetty 9 (as well as 7 and 8) is dual licensed under the Apache License 2.0 and Eclipse Public License 1.0.  
  > Jetty is free for commercial use and distribution under the terms of either license, with exceptions listed in the NOTICE file.

- [MySQL Connector/J](https://dev.mysql.com/doc/connector-j/)

  > GNU General Public License Version 2.0
  >   
  > Copyright (c) 1998, 2017, Oracle and/or its affiliates. All rights reserved.  
  > https://downloads.mysql.com/docs/licenses/connector-j-6.0-gpl-en.pdf

- [jQuery](https://jquery.com/)

  > Copyright (c) JS Foundation and other contributors, https://js.foundation/  
  > https://github.com/jquery/jquery/blob/master/LICENSE.txt

- [Bootstrap](http://getbootstrap.com/)

  > The MIT License (MIT)  
  >   
  > Copyright (c) 2011-2016 Twitter, Inc.  
  > https://github.com/twbs/bootstrap/blob/master/LICENSE

- [Chart.js](http://www.chartjs.org/)

  > The MIT License (MIT)  
  >   
  > Copyright (c) 2013-2017 Nick Downie  
  > https://github.com/chartjs/Chart.js/blob/master/LICENSE.md

- [Font Awesome](http://fontawesome.io/)

  - Font
    > SIL Open Font License (OFL)  
    >   
    > Font Awesome by Dave Gandy - http://fontawesome.io  
    > [http://scripts.sil.org/cms/scripts/page.php?item_id=OFL_web](http://scripts.sil.org/cms/scripts/page.php?item_id=OFL_web)

  - Code
    > The MIT License (MIT)
    >   
    > Font Awesome by Dave Gandy - http://fontawesome.io  
    > https://opensource.org/licenses/mit-license.html

- [Lato](http://www.latofonts.com/lato-free-fonts/)

  > SIL Open Font License (OFL)  
  >   
  > Copyright (c) Łukasz Dziedzic  
  > [http://scripts.sil.org/cms/scripts/page.php?item_id=OFL_web](http://scripts.sil.org/cms/scripts/page.php?item_id=OFL_web)
