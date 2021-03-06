package net.sf.saxon.value;

import net.sf.saxon.Configuration;
import net.sf.saxon.sort.StringCollator;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.NamePool;
import net.sf.saxon.om.StandardNames;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.AtomicType;
import net.sf.saxon.type.BuiltInAtomicType;
import net.sf.saxon.type.ValidationFailure;

import javax.xml.namespace.QName;


/**
 * A qualified name: this is an abstract superclass for QNameValue and NotationValue, representing the
 * XPath primitive types xs:QName and xs:NOTATION respectively
 */

public abstract class QualifiedNameValue extends AtomicValue {

    protected StructuredQName qName;

    /**
     * Factory method to construct either a QName or a NOTATION value, or a subtype of either of these.
     * Note that it is the caller's responsibility to resolve the QName prefix into a URI
     * @param prefix the prefix part of the value. Use "" or null for the empty prefix.
     * @param uri the namespace URI part of the value. Use "" or null for the non-namespace
     * @param local the local part of the value
     * @param targetType the target type, which must be xs:QName or a subtype of xs:NOTATION or xs:QName
     * @param lexicalForm the original lexical form of the value. This is needed in case there are facets
     * such as pattern that check the lexical form
     * @param config the Saxon configuration
     * @return the converted value
     * @throws XPathException if the value cannot be converted.
     */

    public static AtomicValue makeQName(String prefix, String uri, String local,
                                        AtomicType targetType, CharSequence lexicalForm, Configuration config)
            throws XPathException {

        if (targetType.getFingerprint() == StandardNames.XS_QNAME) {
            return new QNameValue(prefix, uri, local, BuiltInAtomicType.QNAME, null);
        } else {
            QualifiedNameValue qnv;
            if (config.getTypeHierarchy().isSubType(targetType, BuiltInAtomicType.QNAME)) {
                qnv = new QNameValue(prefix, uri, local, targetType, null);
            } else {
                qnv = new NotationValue(prefix, uri, local, (AtomicType)null);
            }
            ValidationFailure vf = targetType.validate(qnv, lexicalForm, config.getNameChecker());
            if (vf != null) {
                throw vf.makeException();
            }
            qnv.setTypeLabel(targetType);
            return qnv;
        }
    }


    /**
     * Get the string value as a String. Returns the QName as a lexical QName, retaining the original
     * prefix if available.
     */

    public final String getPrimitiveStringValue() {
        return qName.getDisplayName();
    }

    /**
     * Convert to a StructuredQName
     * @return the name as a StructuredQName
     */

    public StructuredQName toStructuredQName() {
        return qName;
    }

    /**
     * Get the QName in Clark notation, that is "{uri}local" if in a namespace, or "local" otherwise
     */

    public final String getClarkName() {
        return qName.getClarkName();
    }

    /**
     * Get the local part
     */

    public final String getLocalName() {
        return qName.getLocalName();
    }

    /**
     * Get the namespace part. Returns the empty string for a name in no namespace.
     */

    public final String getNamespaceURI() {
        return qName.getNamespaceURI();
    }

    /**
     * Get the prefix. Returns the empty string if the name is unprefixed.
     */

    public final String getPrefix() {
        return qName.getPrefix();
    }

    /**
     * Allocate a nameCode for this QName in the NamePool
     * @param pool the NamePool to be used
     * @return the allocated nameCode
     */

    public int allocateNameCode(NamePool pool) {
        return pool.allocate(getPrefix(), getNamespaceURI(), getLocalName());
    }


    /**
     * Get an object value that implements the XPath equality and ordering comparison semantics for this value.
     * If the ordered parameter is set to true, the result will be a Comparable and will support a compareTo()
     * method with the semantics of the XPath lt/gt operator, provided that the other operand is also obtained
     * using the getXPathComparable() method. In all cases the result will support equals() and hashCode() methods
     * that support the semantics of the XPath eq operator, again provided that the other operand is also obtained
     * using the getXPathComparable() method. A context argument is supplied for use in cases where the comparison
     * semantics are context-sensitive, for example where they depend on the implicit timezone or the default
     * collation.
     *
     * @param ordered true if an ordered comparison is required. In this case the result is null if the
     *                type is unordered; in other cases the returned value will be a Comparable.
     * @param collator
     * @param context the XPath dynamic evaluation context, used in cases where the comparison is context
     *                sensitive @return an Object whose equals() and hashCode() methods implement the XPath comparison semantics
     *         with respect to this atomic value. If ordered is specified, the result will either be null if
     *         no ordering is defined, or will be a Comparable
     */

    public Object getXPathComparable(boolean ordered, StringCollator collator, XPathContext context) {
        return (ordered ? null : this);
    }

    public int hashCode() {
        return qName.hashCode();
    }

    public boolean isIdentical(Value v) {
        return super.isIdentical(v) && qName.getPrefix().equals(((QualifiedNameValue)v).getPrefix());
    }

    /**
     * The toString() method returns the name in the form QName("uri", "local")
     * @return the name in in the form QName("uri", "local")
     */

    public String toString() {
        return "QName(\"" + getNamespaceURI() + "\", \"" + getLocalName() + "\")";
    }

    /**
     * Construct a javax.xml.namespace.QName from this QualifiedNameValue
     */

    public QName toJaxpQName() {
        return qName.toJaxpQName();
        // Note JDK 1.5 dependency
    }


}

//
// The contents of this file are subject to the Mozilla Public License Version 1.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the
// License at http://www.mozilla.org/MPL/
//
// Software distributed under the License is distributed on an "AS IS" basis,
// WITHOUT WARRANTY OF ANY KIND, either express or implied.
// See the License for the specific language governing rights and limitations under the License.
//
// The Original Code is: all this file.
//
// The Initial Developer of the Original Code is Michael H. Kay
//
// Portions created by (your name) are Copyright (C) (your legal entity). All Rights Reserved.
//
// Contributor(s): none.
//

