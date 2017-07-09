# Installing Tools

All task containers provide access to a container-specific package manager,
via the Alpine Linux package manager,
[apk](https://wiki.alpinelinux.org/wiki/Alpine_Linux_package_management).

## Reusing a Tool Installation

Install the tools.
Create an image of the container.
Specify that image in tasks that require the tools.

namespace mynamespace

repo MyImageRegistry type "docker"
    path "myregistry.abc.com"
    userid "$MyUserId" key file "~/mykey.pem"

//* needs to be open to access the package repo, and to push the image to the repo. *//
open task GetTools
    inputs package ...
    outputs MyImage to "MyProject" in MyImageRegistry
    bash """
        apk update
        apk add ...
    """
    //* Create an image of the task container. *//
    MyImage = snapshot as "MyToolsImage:latest"
    post MyImage

task compileit
    use GetTools.MyImage
    ...

task compileit2
    use "MyProject/MyToolsImage:latest" from MyImageRegistry
    ...

task compileit3
    use "MyProject/MyToolsImage:latest" from MyImageRegistry
        /// Must use this form if the image is 
    ...

=====

# Installing Tools

## Reusing a Tool Installation

Install the tools.
Create an image of the container.
Specify that image in tasks that require the tools.

namespace mynamespace

repo MyImageRegistry type "docker"
    path "myregistry.abc.com"
    userid "$MyUserId" key file "~/mykey.pem"

//* needs to be open to access the package repo, and to push the image to the repo. *//
open task GetTools
    inputs package ...
    outputs MyImage to "MyProject" in MyImageRegistry
    install ...
    //* Create an image of the task container. *//
    MyImage = snapshot as "MyToolsImage:latest"
    post MyImage

task compileit
    use GetTools.MyImage
    ...

task compileit2
    use "MyProject/MyToolsImage:latest" from MyImageRegistry
    ...

task compileit3
    use "MyProject/MyToolsImage:latest" from MyImageRegistry
        /// Must use this form if the image is 
    ...
