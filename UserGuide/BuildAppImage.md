# Building an Application Image

All task containers provide access to docker.

namespace mynamespace

repo MyImageRegistry type "docker"
    path "myregistry.abc.com"
    userid "$MyUserId" key file "~/mykey.pem"

task CreateImage
    inputs ...
    outputs ...
    bash """
        docker build ...  /// the task container's private docker instance is used
    """

