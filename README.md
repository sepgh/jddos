# jddos
Experimental Java based dDos tool
---

jDdos is dDos script that targets HTTP based applications. This script is written in Java and it's only experimental so **I do not take responsiblities for any usages**, you might use it at your own risks.

---

## Main features

- HTTP GET and POST (only json) support
- Accepts multiple threads
- Accepts and forces you to use SOCKS proxy
- Can read list of SOCKS proxy from url
- Configurable number of threads running the attack
- Can run infinite attack until told to stop
- Configurable sleep time between request from each service (Default is random)
- Uses **Spring Shell**, therefore its easy to write an attack script and copy it into several machines

---

## RUN

First build the project

    mvn clean package -DskipTests

Run it

    cd target/
    ./jdDos.java

List commands

    help

