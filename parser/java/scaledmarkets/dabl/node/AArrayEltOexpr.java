/* This file was generated by SableCC (http://www.sablecc.org/). */

package scaledmarkets.dabl.node;

import scaledmarkets.dabl.analysis.*;

@SuppressWarnings("nls")
public final class AArrayEltOexpr extends POexpr
{
    private POexpr _oexpr_;

    public AArrayEltOexpr()
    {
        // Constructor
    }

    public AArrayEltOexpr(
        @SuppressWarnings("hiding") POexpr _oexpr_)
    {
        // Constructor
        setOexpr(_oexpr_);

    }

    @Override
    public Object clone()
    {
        return new AArrayEltOexpr(
            cloneNode(this._oexpr_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAArrayEltOexpr(this);
    }

    public POexpr getOexpr()
    {
        return this._oexpr_;
    }

    public void setOexpr(POexpr node)
    {
        if(this._oexpr_ != null)
        {
            this._oexpr_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._oexpr_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._oexpr_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._oexpr_ == child)
        {
            this._oexpr_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._oexpr_ == oldChild)
        {
            setOexpr((POexpr) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}