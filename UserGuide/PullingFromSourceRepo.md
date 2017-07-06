# Pulling From a Source Repository


```
namespace UserGuide.PullFromSource

  task CreateSourceFile
    when true
    outputs SourceOutput include "a.c"
    bash """
      echo "#include <stdio.h>" > a.c
      echo "void main() { println("Hello world"); }" >> a.c
      
  task Compile  // define a task
    when true
    inputs CreateSourceFile.SourceOutput
    bash """
      cc a.c
      ./a.out
    """
    report "Done"
    
```


## Using Maven
