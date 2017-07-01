# Quick Start

The best way to get up and running is to,

* Download and install the DABL processor.
* Create a trivial DABL file.
* Run DABL against your trivial DABL file.

## Download and Install DABL

1. Download the the latest dabl.jar file from
[here](https://github.com/ScaledMarkets/dabl/releases).

2. Place the downloaded dabl.jar file somewhere - this is the executable.
You must have Java 1.8 or later to use it. I will assume that you have
placed it at `/MyTools`.

3. (Optional) Create an alias to point to the DABL command:
```
alias dabl='java -jar /MyTools/dabl.jar'
```

## Create a Trivial DABL File

1. Create a project directory somewhere, and go to that directory from a
command line. (I will assume a Unix/Linux command line - you can translate it
to the appropriate Windows commands if you are using Windows.)

2. Define a simple DABL file, as follows. I will assume that it is called
"simple.dabl".

```
namespace simple
  task Compile  // define a task
    when true
    bash """
      echo "#include <stdio.h>" > a.c
      echo "void main() { println("Hello world"); }" >> a.c
      cc a.c
      ./a.out
    """
    report "Done"
    
```

## Build Your DABL File

1. Start docker. (See [here](https://www.docker.com/community-edition) to get Docker.)

2. Run DABL against your DABL file:
```
dabl simple.dabl
```

DABL should report success, and you should see "Hello world" in the output, followed
by "Done".
