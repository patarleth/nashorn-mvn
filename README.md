If you don't feel like replacing your jdk/jvm with 1.8, get vagrant

    http://www.vagrantup.com/

setup a box, I almost always choose ubuntu 13.10 

    http://nrel.github.io/vagrant-boxes/

vagrant up && vagrant ssh, then setup jdk8 and mvn (here's a little jdk8 help)

    sudo add-apt-repository ppa:webupd8team/java
    sudo apt-get update
    sudo apt-get install oracle-java8-installer
    sudo apt-get install oracle-java8-set-default

make a user bin folder to hold your scripts

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

finally test the project with -

    nashorn src/js/test.js
    
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
ECMAScript 5.1
INVOKEDYNAMIC and metaobject protocol (WATCH https://www.youtube.com/watch?v=UIv86fUSORM)

Why is the switch from the rhino inspired jsr-223 implementation in jdk1.6 to 1.8 important? Why not just use rhino?

As if I had to tell you - performace. 

When envoking a method from a java class in a rhino script, rhino uses a single class that acts like a map returning the requested method.  The implication of a single 'get/lookup' method, is a single megamorphic code path which Hotspot CANNOT optimize or inline.

INVOKEDYNAMIC was added in jdk7 and now 8, enabling the removal of all of this 'get/lookup' code from your app in favor of dynamic linking/bootstrapping from a CallSite.  The CallSite is then used by the runtime for reification of instructions and direct access to the function pointer which Hotspot can now use ;)  Watch the video...it almost makes sense.

One of the better quotes from the youtube video linked above - (INVOKEDYNAMIC provides) '...a way for classes to replace JVM linking rules with their own logic for their call sites, It's application-custom linking'

INVOKEDYNAMIC by itself does not provide an api to help build languages, its just a jvm instruction.  INVOKEDYNAMIC enables the creation of a class of apis like the one nashorn uses - Dynalink (https://github.com/szegedi/dynalink)

Before Dynalink and INVOKEDYNAMIC existed, in order to get hotspot to optimize of this type of code required a custom classloader and a bytecode generator like https://github.com/cojen/Cojen,  Good luck avoiding permgen memory leaks.

links!

    https://blogs.oracle.com/nashorn/
    https://blogs.oracle.com/nashorn/entry/welcome_to_the_nashorn_blog
    http://openjdk.java.net/projects/nashorn/
    https://wiki.openjdk.java.net/display/Nashorn/Main
    https://bitbucket.org/adoptopenjdk/jdk8-nashorn
