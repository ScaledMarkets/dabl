# Repo Declaration

## Purpose

A repo declaration identifies an external repository, and gives it a name that
can be referenced within the DABL file. A repository is an Internet location
(IP address or domain name) and an account on that server, if any.

The repository declaration specifies how to
connect to the repository, including specifying a protocol (e.g., HTTPS or SSH)
and credentials (if any). Normally, credentials
would be specified in a DABL file using environment variables, so that they are
not hard-coded into the file.

## Syntax

A repo declaration has the form,

<dl>
<dd><code>repo</code> <i>name</i> <code>type</code> <i>repo_type</i>
	[<code>scheme</code> <i>tcp_scheme</i>] <code>path</code> <i>repo-path</i>
	[<code>userid</code> <i>userid</i>] [<code>password</code> <i>password</i>]
</dd>
</dl>
