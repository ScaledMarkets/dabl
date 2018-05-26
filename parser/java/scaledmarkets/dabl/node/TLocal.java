/* This file was generated by SableCC (http://www.sablecc.org/). */

package scaledmarkets.dabl.node;

import scaledmarkets.dabl.analysis.*;

@SuppressWarnings("nls")
public final class TLocal extends Token
{
    public TLocal()
    {
        super.setText("local");
    }

    public TLocal(int line, int pos)
    {
        super.setText("local");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TLocal(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTLocal(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TLocal text.");
    }
}