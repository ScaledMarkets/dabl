# When Clause

## Purpose

A when clause specifies an expression for determining whether a task should be invoked.
The syntax of a when clause is,

<dl>
<dd><code>when</code> <i>logical-expression</i></dd>
</dl>


A <i>logical-expression</i> can consist of any of the following operators:

<dl>
<dd>Highest precedence:
<dl>
<dd>task succeeded</dd>
<dd>task failed</dd>
<dd>artifact newer than artifact</dd>
<dd>artifact older than artifact</dd>
</dl>
</dd>
<dd>Next highest precedence:
<dl>
<ddLnot logical-expression</dd>
</dl>
</dd>
<dd>Next highest precedence:
<dl>
<dd>logical-expression and logical-expression</dd>
<dd>logical-expression or logical-expression</dd>
</dl>
</dd>
</dl>
