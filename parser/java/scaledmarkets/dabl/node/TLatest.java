/* This file was generated by SableCC (http://www.sablecc.org/). */

package scaledmarkets.dabl.node;

import scaledmarkets.dabl.analysis.*;

@SuppressWarnings("nls")
public final class TLatest extends Token
{
    public TLatest()
    {
        super.setText("latest");
    }

    public TLatest(int line, int pos)
    {
        super.setText("latest");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TLatest(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTLatest(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TLatest text.");
    }
}