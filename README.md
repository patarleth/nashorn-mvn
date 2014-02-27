nashorn-mvn
============

prereqs - maven, jdk8, on ubuntu or osx.  

If you're on windows you will need to run ubuntu in a virtual machine.

<https://jdk8.java.net/download.html>

if replacing your system jdk/jvm with 1.8 isn't your thing, try vagrant!

<http://www.vagrantup.com/>

I've included a sample Vagrant file and provisioning script that downloads an ubuntu 13.10 vm from <http://cloud-images.ubuntu.com> and installs maven, jdk1.8, builds this project and runs src/js/test.js

If you prefer to manually setup your ubuntu vm/box feel free to hack the provision_nashorn_vagrant.sh script and make it work. I've included the basics here if don't like figuring out shell scripts. Oh, and osx fans - you're on your own.

first(!) install maven if it is not installed, 

    if [ -z $(which mvn) ]; then sudo apt-get install maven; fi

get and install jdk8

    sudo add-apt-repository ppa:webupd8team/java
    sudo apt-get update
    sudo apt-get install oracle-java8-installer
    sudo apt-get install oracle-java8-set-default

add a user bin folder to hold your scripts

    mkdir ~/bin

and add the folder to your path by editing your ~/.profile like so - 

    if [ -d "$HOME/bin" ] ; then
        PATH="$HOME/bin:$PATH"
    fi

reload the .profile if changed

    source ~/.profile

copy the source code from this project into ~/bin/

    chmod +x ./src/bash/*
    cp ./src/bash/* ~/bin/.


Now that you've either got vagrant up/vagrant ssh working or set your computer up, it's time to test the project -

* nashorn [src/js/test.js](https://github.com/patarleth/nashorn-mvn/blob/master/src/js/test.js)

[src/js/test.js](https://github.com/patarleth/nashorn-mvn/blob/master/src/js/test.js) does three things -

My test.js script

1. prints the content of the .js file
2. creates an org.json.JSONObject, adds properties, then prints it using java.lang.System.out.println
3. creates a nashorn json object, uses JSON.stringify to format it, printed iwth java.lang.System.out.println
    
Here's what the files do -

src/bash/mvngencp

    generates a project classpath using mvn dependency:build-classpath

src/bash/mvnsavecp

    if pom.xml exists
      if ~/.tmp/escaped_foldername.cp.txt exists
        if timestamp of pom.xml match the .cp.txt file 
          use ~/.tmp/escaped_foldername.cp.txt
        else
          call mvngencp save to ~/.tmp/escaped_foldername.cp.txt
        fi
      else
        call mvngencp save to ~/.tmp/escaped_foldername.cp.txt
      fi
      return cp
    else
      do nothing
    fi      

src/bash/nashorn

    if pom.xml exists
      cp = mvnsavecp
      jjs -classpath "$cp"
    fi

Nashorn features

* ECMAScript 5.1
* INVOKEDYNAMIC and metaobject protocol (WATCH https://www.youtube.com/watch?v=UIv86fUSORM)

Ok, ok, ok.........SOOOOOOOO.....

Why the switch from the rhino inspired jsr-223 implementation shipped in jdk1.6 to nashorn in 1.8? Why not just use good old rhino (<https://developer.mozilla.org/en-US/docs/Rhino>)?

As if I had to tell you - performace. 

TO envoke a method from a java class in a rhino script, rhino uses a single class acting like a map that returns the requested method.  The implication of a single 'get/lookup' method is the creation of a megamorphic code path which Hotspot CANNOT optimize or inline. I love that word - megamorphic!

The jvm instruction INVOKEDYNAMIC was added in jdk7 enabling the removal of this kind of 'get/lookup' code in dynamic jvm languages, in favor of linking/bootstrapping from a CallSite.  The CallSite can then be used by the runtime for reification of instructions and direct access to the function pointer. Viola! Hotspot can now optimize your script ;)  Watch the video...it almost makes sense.

One of the better quotes from the youtube video linked above - (INVOKEDYNAMIC provides) '...a way for classes to replace JVM linking rules with their own logic for their call sites, It's application-custom linking'

Keep in mind that INVOKEDYNAMIC isn't magic. By itself the jvm instruction does not provide an api to help build languages. Afterall it's just an instruction.  However, INVOKEDYNAMIC does enable the creation of a class of apis like Dynalink (https://github.com/szegedi/dynalink) that is used by nashorn.

Before Dynalink and INVOKEDYNAMIC, to get hotspot to optimize code assembled at runtime, required a custom classloader and a bytecode generator like <https://github.com/cojen/Cojen>. Good luck avoiding permgen memory leaks, it's God's work my friend.

links!

* <https://blogs.oracle.com/nashorn/>
* <https://blogs.oracle.com/nashorn/entry/welcome_to_the_nashorn_blog>
* <http://openjdk.java.net/projects/nashorn/>
* <https://wiki.openjdk.java.net/display/Nashorn/Main>
* <https://bitbucket.org/adoptopenjdk/jdk8-nashorn>
