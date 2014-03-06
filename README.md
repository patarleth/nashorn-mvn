nashorn-mvn
============

First off, mad, mad props to Attila Szegedi <https://github.com/szegedi> author of [dynalink](https://github.com/szegedi/dynalink) <http://szegedi.github.io/dynalink/> used by nashorn providing a "high-level linking and metaobject protocol library".

prereqs - maven, jdk8, on ubuntu or osx.  

If you're on windows you will need to run ubuntu in a virtual machine.

<https://jdk8.java.net/download.html>

if replacing your system's jdk/jvm with 1.8 isn't your thing, try vagrant!


vagrant-up
============

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

reload the .profile if you had to add ~/bin to your path

    source ~/.profile

copy the source code from this project's src/bash folder into ~/bin/

    chmod +x ./src/bash/*
    cp ./src/bash/* ~/bin/.


What is this thing called nashorn
===========

Nashorn features

* ECMAScript 5.1
* Based on INVOKEDYNAMIC and a metaobject protocol provided by Dynalink (WATCH https://www.youtube.com/watch?v=UIv86fUSORM)

Ok, ok, ok.........SOOOOOOOO.....what the hell?

JDK8 rewrote the Rhino based javascript [jsr-223](https://www.jcp.org/en/jsr/detail?id=223) ScriptEngine with nashorn. Why not continue improving Rhino (<https://developer.mozilla.org/en-US/docs/Rhino>)?

Short answer, hotspot optimizations and overall performace. Long answer -

TO envoke a method in a java class from a rhino script, rhino uses a single class that acts like a map or dispatcher that returns the requested method.  The implication of a single 'get/lookup' method is the creation of a megamorphic code path which Hotspot CANNOT optimize or inline. I love that word - megamorphic! thanks Szegedi.

The jvm instruction INVOKEDYNAMIC was added in jdk7 enabling the removal of this kind of 'get/lookup' code in dynamic jvm languages, in favor of linking/bootstrapping from a CallSite.  A CallSite is then used by the runtime for reification of instructions and direct access to the function pointer. Viola! Hotspot can now optimize your script ;)  Watch the videos...it almost makes sense.

One of the better quotes from the youtube video linked above - (INVOKEDYNAMIC provides) '...a way for classes to replace JVM linking rules with their own logic for their call sites, It's application-custom linking'

Keep in mind that INVOKEDYNAMIC isn't magic. By itself the jvm instruction does not provide an api to help build languages. Afterall it's just an instruction.  However, INVOKEDYNAMIC does enable the creation of metaobject protocol apis like Dynalink (https://github.com/szegedi/dynalink) used by nashorn.

Before Dynalink, let alone INVOKEDYNAMIC existed, to get hotspot to optimize code assembled at runtime, required custom classloaders and bytecode generators like <https://github.com/cojen/Cojen>. Good luck avoiding permgen memory leaks with this strategy, it's God's work my friend.

links!

* <https://blogs.oracle.com/nashorn/>
* <https://blogs.oracle.com/nashorn/entry/welcome_to_the_nashorn_blog>
* <http://openjdk.java.net/projects/nashorn/>
* <https://wiki.openjdk.java.net/display/Nashorn/Main>
* <https://bitbucket.org/adoptopenjdk/jdk8-nashorn>


Maven
===========

If I have to tell you what maven is go away...no, no go here and read all about it <http://maven.apache.org/>

Exec'ing a java main based on pom file is easy. I've included a couple sample java classes that show two ways to do this.  The first is ExecJsScriptEngine which executes a java main in a pom file  like so -

<pre>
      &lt;plugin&gt;
        &lt;groupId&gt;org.codehaus.mojo&lt;/groupId&gt;
        &lt;artifactId&gt;exec-maven-plugin&lt;/artifactId&gt;
        &lt;version&gt;1.2.1&lt;/version&gt;
        &lt;executions&gt;
          &lt;execution&gt;
            &lt;id&gt;demoJsFileAtInitPhase&lt;/id&gt;
            &lt;phase&gt;initialize&lt;/phase&gt;
            &lt;configuration&gt;
              &lt;mainClass&gt;ExecJsScriptEngine&lt;/mainClass&gt;
              &lt;arguments&gt;
                &lt;argument&gt;
                  ./src/build/initialize.js
                &lt;/argument&gt;
              &lt;/arguments&gt;
            &lt;/configuration&gt;
            &lt;goals&gt;
              &lt;goal&gt;java&lt;/goal&gt;
            &lt;/goals&gt;
          &lt;/execution&gt;
        &lt;/executions&gt;
      &lt;/plugin&gt;
</pre>

This strategty is definately a bit clumbsy, but it works. If you would like to parameterize the script or tighten up how you interact with the code, checkout the guide on plugins here:

* <https://maven.apache.org/guides/plugin/guide-java-plugin-development.html>

If you do decide to convert the main into a Mojo plugin, I've included a very simple plugin JsScriptEngineMojo.java that gives you a head start. Configured like so -

<pre>
      &lt;plugin&gt;
        &lt;groupId&gt;org.arleth.nashorn&lt;/groupId&gt;
        &lt;artifactId&gt;nashorn-maven&lt;/artifactId&gt;
        &lt;version&gt;1.0.0-SNAPSHOT&lt;/version&gt;
        &lt;executions&gt;
          &lt;execution&gt;
            &lt;id&gt;printTestWorld&lt;/id&gt;
            &lt;phase&gt;test&lt;/phase&gt;
            &lt;configuration&gt;
              &lt;scripttext&gt;java.lang.System.out.println('\n\n\nTest World\n\n\n');&lt;/scripttext&gt;
            &lt;/configuration&gt;
            &lt;goals&gt;
              &lt;goal&gt;jsmojo&lt;/goal&gt;
            &lt;/goals&gt;
          &lt;/execution&gt;
          &lt;execution&gt;
            &lt;id&gt;printTestWorldFromFile&lt;/id&gt;
            &lt;phase&gt;test&lt;/phase&gt;
            &lt;configuration&gt;
              &lt;scriptfile&gt;./src/js/testworld.js&lt;/scriptfile&gt;
            &lt;/configuration&gt;
            &lt;goals&gt;
              &lt;goal&gt;jsmojo&lt;/goal&gt;
            &lt;/goals&gt;
          &lt;/execution&gt;
          &lt;execution&gt;
            &lt;id&gt;printTestWorld2&lt;/id&gt;
            &lt;phase&gt;test&lt;/phase&gt;
            &lt;configuration&gt;
              &lt;scripttext&gt;java.lang.System.out.println('\n\n\nTest World from script again\n\n\n');&lt;/scripttext&gt;
            &lt;/configuration&gt;
            &lt;goals&gt;
              &lt;goal&gt;jsmojo&lt;/goal&gt;
            &lt;/goals&gt;
          &lt;/execution&gt;
        &lt;/executions&gt;
      &lt;/plugin&gt;

</pre>

This runs three seperate scripts, all printing to the console.

JS + Maven from the command line
===========

Quickly exec'ing a script from the command line based on the dependency tree and target/classes folder of a maven project is a tiny bit more difficult due to the startup costs of maven.

The obvious, and least satisfying, solution is to use exec:java 

mvn exec:java -Dexec.mainClass="jdk.nashorn.tools.Shell" Dexec.args="src/js/test.js"

This can take what seems like forever as it runs maven to generate the classpath, my solution is 

    src/bash/nashorn.

Run like so

* nashorn [src/js/test.js](https://github.com/patarleth/nashorn-mvn/blob/master/src/js/test.js)

[src/js/test.js](https://github.com/patarleth/nashorn-mvn/blob/master/src/js/test.js) does three things -

the include test.js script does this

1. prints the content of the .js file
2. creates an org.json.JSONObject, adds properties, then prints it using java.lang.System.out.println
3. creates a nashorn json object, uses JSON.stringify to format it, printed iwth java.lang.System.out.println
    
Here some pseudo code for the src/bash/ shell scripts -

src/bash/mvngencp

    generates a project classpath based on mvn dependency:build-classpath

src/bash/mvnsavecp

    calls mvngencp and saves to a temp folder, returns the cp. Here's the nitty gritty -

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

    runs scripts based on mvnsavecp like so -

    if pom.xml exists
      cp = mvnsavecp
      jjs -classpath "$cp"
    fi

enjoy your js + pom hacking