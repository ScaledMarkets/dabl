/* This file was generated by SableCC (http://www.sablecc.org/). */

package scaledmarkets.dabl.node;

import scaledmarkets.dabl.analysis.*;

@SuppressWarnings("nls")
public final class TNamespace extends Token
{
    public TNamespace()
    {
        super.setText("namespace");
    }

    public TNamespace(int line, int pos)
    {
        super.setText("namespace");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TNamespace(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTNamespace(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TNamespace text.");
    }
}