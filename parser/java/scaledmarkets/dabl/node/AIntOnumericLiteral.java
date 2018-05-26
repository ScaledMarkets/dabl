/* This file was generated by SableCC (http://www.sablecc.org/). */

package scaledmarkets.dabl.node;

import scaledmarkets.dabl.analysis.*;

@SuppressWarnings("nls")
public final class AIntOnumericLiteral extends POnumericLiteral
{
    private POsign _osign_;
    private TWholeNumber _wholeNumber_;

    public AIntOnumericLiteral()
    {
        // Constructor
    }

    public AIntOnumericLiteral(
        @SuppressWarnings("hiding") POsign _osign_,
        @SuppressWarnings("hiding") TWholeNumber _wholeNumber_)
    {
        // Constructor
        setOsign(_osign_);

        setWholeNumber(_wholeNumber_);

    }

    @Override
    public Object clone()
    {
        return new AIntOnumericLiteral(
            cloneNode(this._osign_),
            cloneNode(this._wholeNumber_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAIntOnumericLiteral(this);
    }

    public POsign getOsign()
    {
        return this._osign_;
    }

    public void setOsign(POsign node)
    {
        if(this._osign_ != null)
        {
            this._osign_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._osign_ = node;
    }

    public TWholeNumber getWholeNumber()
    {
        return this._wholeNumber_;
    }

    public void setWholeNumber(TWholeNumber node)
    {
        if(this._wholeNumber_ != null)
        {
            this._wholeNumber_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._wholeNumber_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._osign_)
            + toString(this._wholeNumber_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._osign_ == child)
        {
            this._osign_ = null;
            return;
        }

        if(this._wholeNumber_ == child)
        {
            this._wholeNumber_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._osign_ == oldChild)
        {
            setOsign((POsign) newChild);
            return;
        }

        if(this._wholeNumber_ == oldChild)
        {
            setWholeNumber((TWholeNumber) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}