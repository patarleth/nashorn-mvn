get vagrant
 http://www.vagrantup.com/

get/setup a box
  http://nrel.github.io/vagrant-boxes/


vagrant up
vagrant ssh

setup jdk8 and mvn (here's a little jdk8 help)
  sudo add-apt-repository ppa:webupd8team/java
  sudo apt-get update
  sudo apt-get install oracle-java8-installer
  sudo apt-get install oracle-java8-set-default

add a ~/bin folder to your path, edit ~/.profile, make sure 

# set PATH so it includes user's private bin if it exists
if [ -d "$HOME/bin" ] ; then
    PATH="$HOME/bin:$PATH"
fi

reload the .profile if changed
source ~/.profile

mvngencp
  generates a project classpath using mvn dependency:build-classpath

mvnsavecp
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

nashorn
  if pom.xml exists
    cp = mvnsavecp
    jjs -classpath "$cp"
  fi

Nashorn features
ECMAScript 5.1
INVOKEDYNAMIC and a metaobject protocol MUST WATCH https://www.youtube.com/watch?v=UIv86fUSORM

Why is this change important? Why not just use rhino?
Performace. When envoking a method in a java class in rhino, a single class is used which for all intensive purposes looks and acts like a map to return the requested method.  The implication is that every call in rhino is now routed through this single 'get/lookup' method.  This creates a single megamorphic code path which Hotspot CANNOT optimize or inline.

When INVOKEDYNAMIC was added in jdk7 and now 8, it enabled the removal of all of this 'get/lookup' code from your app in favor of dynamic linking/bootstrapping from a CallSite.  The CallSite is then used by the runtime for reification of instructions and direct access to the function pointer which Hotspot can now use ;)  Watch the video...it almost makes sense.

One of the better quotes from the youtube video linked above - '...a way for classes to replace JVM linking rules with their own logic for their call sites, It's application-custom linking'

INVOKEDYNAMIC by itself does not provide an api that helps you build languages, its just a jvm instruction.  INVOKEDYNAMIC enables the creation of theses types of apis like the one nashorn uses -
Dynalink (https://github.com/szegedi/dynalink) 
Before Dynalink and INVOKEDYNAMIC to get hotspot optimization of this type of code would require a custom classloader and a bytecode generator like https://github.com/cojen/Cojen,  Good luck avoiding permgen memory leaks.

https://blogs.oracle.com/nashorn/
https://blogs.oracle.com/nashorn/entry/welcome_to_the_nashorn_blog
http://openjdk.java.net/projects/nashorn/
https://wiki.openjdk.java.net/display/Nashorn/Main