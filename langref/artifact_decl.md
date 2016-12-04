# Artifact Declaration

## Purpose

An artifact declaration defines a versioned artifact that is produced by the DABL
file. An artifact declaration can specify compatibility with versions of other
artifacts, and can assert which versions it has actually been tested with.
Such assertions are not verified: they are design-time assertions. However, a
tool could attempt to verify the assertions.

## Syntax

`artifact` *Id* : *expression*.*expression* *artifact-statements*

The Id is the name that will be assigned to the artifact. The two expressions
are the major and minor version numbers, respectively. The artifact-statements
consist of zero or more `assume` statements and `tested with` statements.

An `assume` statement declares that the artifact that is created by the DABL file
is expected to be compatible with the specified versions of another artifact.
This is a design-time assertion, and there of course can be no absolute proof of
compatibility.

A `tested with` statement declares that the artifact has actually been tested
for compatibility with the specified versions of another artifact. This is also
a design-time assertion, although tools might attempt to verify it.

An assume statement has the form,

`assume compatible with` *artifact-Id* *version-spec*

where *version-spec* is a period-separated sequence of version ranges. For example,

```
assume compatible with ABC:3.3-3.4
```

says that the artifact is compatible with major version number 3.3 through 3.4
of artifact `ABC`. One can also use a wildcard, such as,

```
assume compatible with ABC:3.*
```

The `tested with` statement has the form,

`tested with` *artifact-Id* *version-spec*
