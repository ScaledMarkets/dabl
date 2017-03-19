# When Clause

## Purpose

A when clause specifies an expression for determining whether a task should be invoked.
The syntax of a when clause is,

<dl>
<dd><code>when</code> <i>logical-expression</i></dd>
</dl>


A <i>logical-expression</i> can consist of any of the following operators:

<dl>
<dd><i><b>Highest precedence:</b></i>
<dl>
<dd>task succeeded</dd>
<dd>task failed</dd>
<dd>artifact newer than artifact</dd>
<dd>artifact older than artifact</dd>
</dl>
</dd>
<dd><i><b>Next highest precedence:</i></b>
<dl>
<ddLnot logical-expression</dd>
</dl>
</dd>
<dd><i><b>Next highest precedence:</i></b>
<dl>
<dd>logical-expression and logical-expression</dd>
<dd>logical-expression or logical-expression</dd>
</dl>
</dd>
</dl>
