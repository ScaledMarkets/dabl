# When Clause

## Purpose

A when clause specifies an expression for determining whether a <i>task</i> should be invoked.
The syntax of a when clause is,

<dl>
<dd><code>when</code> <i><i>logical-expression</i></i></dd>
</dl>


A <i><i>logical-expression</i></i> can consist of any of the following operators:

<dl>
<dd><i><b>Highest precedence:</b></i>
<dl>
<dd><i>task</i> succeeded</dd>
<dd><i>task</i> failed</dd>
<dd><i>artifact</i> newer than <i>artifact</i></dd>
<dd><i>artifact</i> older than <i>artifact</i></dd>
</dl>
</dd>
<dd><i><b>Next highest precedence:</i></b>
<dl>
<ddLnot <i>logical-expression</i></dd>
</dl>
</dd>
<dd><i><b>Next highest precedence:</i></b>
<dl>
<dd><i>logical-expression</i> and <i>logical-expression</i></dd>
<dd><i>logical-expression</i> or <i>logical-expression</i></dd>
</dl>
</dd>
</dl>
