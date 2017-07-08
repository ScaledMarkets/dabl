# Installing Tools

All task containers provide access to a container-specific package manager,
via yum.


## Reusing a Tool Installation

Install the tools.
Create an image of the container.
Specify that image in tasks that require the tools.

namespace mynamespace

repo MyImageRegistry type "docker"
    path "myregistry.abc.com"
    userid "$MyUserId" key file "~/mykey.pem"

task GetTools
    bash """
        yum install ...
    """
    //* Create an image of the task container. *//
    MyImage = snapshot
    post MyImage to MyImageRegistry as "MyProject/MyToolsImage:latest"

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
