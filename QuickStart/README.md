# Quick Start

The best way to get up and running is to,

* Download and install the DABL processor.
* Create a trivial DABL file.
* Build your trivial DABL file.

## Download and Install DABL

1. Download the the latest dabl.jar file from
[here](https://github.com/ScaledMarkets/dabl/releases).

2. Place the downloaded dabl.jar file somewhere - this is the executable.
You must have Java 1.8 or later to use it. I will assume that you have
placed it at `/MyTools`.

## Create a Trivial DABL File

1. Create a project directory somewhere, and go to that directory from a
command line. (I will assume a Unix/Linux command line - you can translate it
to the appropriate Windows commands if you are using Windows.)

2. Define a simple DABL file, as follows. I will assume that it is called
"sample.dabl".

```
namespace simple
  task Compile  // define a task
    bash """
      echo "#include <stdio>\nvoid main() { println("Hello world"); }" > a.c
      cc a.c
      ./a.out
    """
    report "Done"
    
```

## Build Your DABL File

1. Start docker.

2. Run DABL against your DABL file:
```
java -jar /MyTools/dabl.jar sample.dabl
```

DABL should report success.
