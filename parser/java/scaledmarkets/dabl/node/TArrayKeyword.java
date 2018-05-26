/* This file was generated by SableCC (http://www.sablecc.org/). */

package scaledmarkets.dabl.node;

import scaledmarkets.dabl.analysis.*;

@SuppressWarnings("nls")
public final class TArrayKeyword extends Token
{
    public TArrayKeyword()
    {
        super.setText("array");
    }

    public TArrayKeyword(int line, int pos)
    {
        super.setText("array");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TArrayKeyword(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTArrayKeyword(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TArrayKeyword text.");
    }
}