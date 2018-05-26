/* This file was generated by SableCC (http://www.sablecc.org/). */

package scaledmarkets.dabl.node;

import scaledmarkets.dabl.analysis.*;

@SuppressWarnings("nls")
public final class TMs extends Token
{
    public TMs()
    {
        super.setText("ms");
    }

    public TMs(int line, int pos)
    {
        super.setText("ms");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TMs(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTMs(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TMs text.");
    }
}