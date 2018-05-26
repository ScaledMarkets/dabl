/* This file was generated by SableCC (http://www.sablecc.org/). */

package scaledmarkets.dabl.node;

import scaledmarkets.dabl.analysis.*;

@SuppressWarnings("nls")
public final class TError extends Token
{
    public TError()
    {
        super.setText("error");
    }

    public TError(int line, int pos)
    {
        super.setText("error");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TError(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTError(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TError text.");
    }
}