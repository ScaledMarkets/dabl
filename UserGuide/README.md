# User Guide Table Of Contents

[The Simplest Case: Print Hello Word](SimplestCase.md)
—So that you know the basic structure of a DABL specification.

[Compiling a Source File](Compiling.md)
So that you know how to make a task do things.

[Pulling From a Source Repository](PullingFromSourceRepo.md)
So that your tasks can obtain source artifacts.

[Pushing To an Artifact Repository](PushingToArtifactRepo.md)
So that you can persist the artifacts that your tasks build.

[Defining a File Set](DefiningFileSet.md)
So that you can define a collection of files by name, and then refer to them
by name instead of listing the collection each time.

[Installing Tools](InstallingTools.md)
So that you can add software development and build tools to the DABL task
environment.

[Configuring the Task Container](ConfiguringContainer.md)
To attach file volumes, to add memory, to configuring networking, and set
capabilities.

[Building an Application Image](BuildAppImage.md)
So that your DABL namespace can build applications that are packaged as
a container image.

[Calling a Function](CallingFunction.md)
So that you can extend DABL, without having to call out to a shell, in a
reasonably safe and language supported manner.

[Importing Another Namespace](Importing.md)
So that your namespace can use things that are defined in other namespaces,
in a safe, maintainable, language supported manner.

[Defining a Reusable Task Template](TaskTemplate.md)
So that other namespaces can instantiate those tasks, in a safe, maintainable,
language supported manner.

[Creating a Custom Task Base Image](CustomBaseImage.md)
To do things that DABL does not permit.
