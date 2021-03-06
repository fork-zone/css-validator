//
// Author: Yves Lafon <ylafon@w3.org>
//
// (c) COPYRIGHT MIT, ERCIM, Keio, Beihang, 2017.
// Please first read the full copyright statement in file COPYRIGHT.html
package org.w3c.css.properties.css3.counterstyle;

import org.w3c.css.util.ApplContext;
import org.w3c.css.util.InvalidParamException;
import org.w3c.css.values.CssExpression;
import org.w3c.css.values.CssIdent;
import org.w3c.css.values.CssLayerList;
import org.w3c.css.values.CssTypes;
import org.w3c.css.values.CssValue;
import org.w3c.css.values.CssValueList;

import java.util.ArrayList;

import static org.w3c.css.values.CssOperator.COMMA;
import static org.w3c.css.values.CssOperator.SPACE;

/**
 * @spec https://www.w3.org/TR/2015/CR-css-counter-styles-3-20150611/#descdef-counter-style-additive-symbols
 */
public class CssAdditiveSymbols extends org.w3c.css.properties.css.counterstyle.CssAdditiveSymbols {


    /**
     * Create a new CssAdditiveSymbols
     */
    public CssAdditiveSymbols() {
        value = initial;  // this is wrong...
    }

    /**
     * Creates a new CssAdditiveSymbols
     *
     * @param expression The expression for this property
     * @throws org.w3c.css.util.InvalidParamException
     *          Expressions are incorrect
     */
    public CssAdditiveSymbols(ApplContext ac, CssExpression expression, boolean check)
            throws InvalidParamException {
        CssValue val;
        char op = SPACE;
        ArrayList<CssValue> values = new ArrayList<>();
        ArrayList<CssValue> additive;
        boolean gotint, gotsymbol;
        setByUser();


        if (expression.getCount() % 2 == 1) {
            // we need an odd number here
            throw new InvalidParamException("unrecognize", ac);
        }
        while (!expression.end()) {
            additive = new ArrayList<>();
            gotsymbol = false;
            gotint = false;
            for (int i = 0; i < 2; i++) {
                val = expression.getValue();
                op = expression.getOperator();
                switch (val.getType()) {
                    case CssTypes.CSS_URL:
                    case CssTypes.CSS_IMAGE:
                    case CssTypes.CSS_STRING:
                        if (gotsymbol) {
                            throw new InvalidParamException("value",
                                    val.toString(),
                                    getPropertyName(), ac);
                        }
                        additive.add(val);
                        gotsymbol = true;
                        break;
                    case CssTypes.CSS_IDENT:
                        if (CssIdent.isCssWide((CssIdent) val)) {
                            throw new InvalidParamException("value",
                                    val.toString(),
                                    getPropertyName(), ac);
                        }
                        if (gotsymbol) {
                            throw new InvalidParamException("value",
                                    val.toString(),
                                    getPropertyName(), ac);
                        }
                        additive.add(val);
                        gotsymbol = true;
                        break;
                    case CssTypes.CSS_NUMBER:
                        val.getCheckableValue().checkInteger(ac, this);
                        if (gotint) {
                            throw new InvalidParamException("value",
                                    val.toString(),
                                    getPropertyName(), ac);
                        }
                        additive.add(val);
                        gotint = true;
                        break;
                    default:
                        throw new InvalidParamException("value",
                                val.toString(),
                                getPropertyName(), ac);
                }
                if (i == 0 && op != SPACE) {
                    throw new InvalidParamException("operator", op,
                            getPropertyName(), ac);
                }
                expression.next();
            }
            values.add(new CssValueList(additive));
            if (op != COMMA && !expression.end()) {
                throw new InvalidParamException("operator", op,
                        getPropertyName(), ac);
            }

        }
        if (values.isEmpty()) {
            throw new InvalidParamException("unrecognize", ac);
        }
        value = (values.size() == 1) ? values.get(0) : new CssLayerList(values);

    }

    public CssAdditiveSymbols(ApplContext ac, CssExpression expression)
            throws InvalidParamException {
        this(ac, expression, false);
    }

}

