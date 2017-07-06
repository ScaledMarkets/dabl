# Compiling a Source File

## Compiling a C File

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


Using Maven is explained in [Pulling From a Source Repository](PullingFromSourceRepo.md).


## Compiling a Go File

