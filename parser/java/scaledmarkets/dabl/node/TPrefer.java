/* This file was generated by SableCC (http://www.sablecc.org/). */

package scaledmarkets.dabl.node;

import scaledmarkets.dabl.analysis.*;

@SuppressWarnings("nls")
public final class TPrefer extends Token
{
    public TPrefer()
    {
        super.setText("prefer");
    }

    public TPrefer(int line, int pos)
    {
        super.setText("prefer");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TPrefer(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTPrefer(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TPrefer text.");
    }
}