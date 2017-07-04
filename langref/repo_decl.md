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
<dd>[<code>public</code>] <code>repo</code> <i>name</i> <code>type</code> <i>repo_type</i>
	[<code>scheme</code> <i>tcp_scheme</i>] <code>path</code> <i>repo-path</i>
	[<code>userid</code> <i>userid</i>] [<code>password</code> <i>password</i>]
</dd>
</dl>

where,
<dl>
<dd><i>name</i> is the name by which the repo will be known in this DABL file.</dd>
<dd><i>repo_type</i> is a string that identified a supported type of repository, such 
as <code>"maven"</code> or <code>"git"</code>. The type must be one of those
listed as a dabl.repo.providers.<type> setting.
(See <a href="https://github.com/ScaledMarkets/dabl#using-the-reference-implementation">
<i>Using the Reference Implementation</i>.)</a>
</dd>
<dd><i>tcp_scheme</i> is the TCP <i>scheme</i> by which the repository should be
accessed, such as <code>"https"</code> or <code>"ssh"</code>.</dd>
<dd><i>repo-path</i> is the hostname and resource path, with an optional port,
at which the repo should be accessed: this is a URL minus the <i>scheme</i>://.</dd>
<dd><i>userid</i> The user Id string for authenticating to the repository.</dd>
<dd><i>password</i> The password string for authenticating to the repository.</dd>
</dl>

## Example

```
repo my_maven type "maven"
  path "mymaven.abc.com"
  userid "MavenUserId" password "MavenPassword"
```
