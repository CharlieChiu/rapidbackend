package com.rapidbackend.socialutil.install.dbinstall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class DOMUtil {

 public static Map<String,String> toMap(NamedNodeMap attrs) {
   return toMapExcept(attrs);
 }

 public static Map<String,String> toMapExcept(NamedNodeMap attrs, String... exclusions) {
   Map<String,String> args = new HashMap<String,String>();
   outer: for (int j=0; j<attrs.getLength(); j++) {
     Node attr = attrs.item(j);
     String attrName = attr.getNodeName();
     for (String ex : exclusions)
       if (ex.equals(attrName)) continue outer;
     String val = attr.getNodeValue();
     args.put(attrName, val);
   }
   return args;
 }

 public static Node getChild(Node node, String name) {
   if (!node.hasChildNodes()) return null;
   NodeList lst = node.getChildNodes();
   if (lst == null) return null;
   for (int i=0; i<lst.getLength(); i++) {
     Node child = lst.item(i);
     if (name.equals(child.getNodeName())) return child;
   }
   return null;
 }
 
 public static Node getChild(Node node, String name,String errMsg) {
     Node result = getChild(node, name);
     if(result == null){
         throw new RuntimeException(errMsg + ": missing mandatory attribute node '" + name + "'");
     }
     return result;
   }
 
 /**
  * return an empty list if no child is found
  * @param node
  * @param name
  * @return
  */
 public static List<Node> getChildren(Node node,String name){
     List<Node> children = new ArrayList<Node>();
     if (!node.hasChildNodes()) return children;
     NodeList lst = node.getChildNodes();
     if(lst == null) return children;
     for (int i=0; i<lst.getLength(); i++) {
         Node child = lst.item(i);
         if (name.equals(child.getNodeName())) children.add(child);
     }
     
     return children;
 }
 /**
  * 
  * @param node
  * @param name
  * @return null if has't find the node
  */
 public static Node getOffspring(Node node, String name){
     NodeWalker walker = new NodeWalker(node);
     Node result = null;
     while(walker.hasNext()){
         Node n = walker.nextNode();
         if(n.getNodeName().equalsIgnoreCase(name)){
             result = n;
             break;
         }
     }
     return result;
 }

 public static String getAttr(NamedNodeMap attrs, String name) {
   return getAttr(attrs,name,null);
 }

 public static String getAttr(Node nd, String name) {
   return getAttr(nd.getAttributes(), name);
 }

 public static String getAttr(NamedNodeMap attrs, String name, String missing_err) {
   Node attr = attrs==null? null : attrs.getNamedItem(name);
   if (attr==null) {
     if (missing_err==null) return null;
     throw new RuntimeException(missing_err + ": missing mandatory attribute '" + name + "'");
   }
   String val = attr.getNodeValue();
   return val;
 }

 public static String getAttr(Node node, String name, String missing_err) {
   return getAttr(node.getAttributes(), name, missing_err);
 }

 


 /**
  * Drop in replacement for Node.getTextContent().
  *
  * <p>
  * This method is provided to support the same functionality as
  * Node.getTextContent() but in a way that is DOM Level 2 compatible.
  * </p>
  *
  * @see <a href="http://www.w3.org/TR/DOM-Level-3-Core/core.html#Node3-textContent">DOM Object Model Core</a>
  */
 public static String getText(Node nd) {

   short type = nd.getNodeType();

   // for most node types, we can defer to the recursive helper method,
   // but when asked for the text of these types, we must return null
   // (Not the empty string)
   switch (type) {

   case Node.DOCUMENT_NODE: /* fall through */
   case Node.DOCUMENT_TYPE_NODE: /* fall through */
   case Node.NOTATION_NODE: /* fall through */
     return null;
   }

   StringBuilder sb = new StringBuilder();
   getText(nd, sb);
   return sb.toString();
 }

 /** @see #getText(Node) */
 private static void getText(Node nd, StringBuilder buf) {

   short type = nd.getNodeType();

   switch (type) {

   case Node.ELEMENT_NODE: /* fall through */
   case Node.ENTITY_NODE: /* fall through */
   case Node.ENTITY_REFERENCE_NODE: /* fall through */
   case Node.DOCUMENT_FRAGMENT_NODE:
     NodeList childs = nd.getChildNodes();
     for (int i = 0; i < childs.getLength(); i++) {
       Node child = childs.item(i);
       short childType = child.getNodeType();
       if (childType != Node.COMMENT_NODE &&
           childType != Node.PROCESSING_INSTRUCTION_NODE) {
         getText(child, buf);
       }
     }
     break;

   case Node.ATTRIBUTE_NODE: /* fall through */
     /* Putting Attribute nodes in this section does not exactly
        match the definition of how textContent should behave
        according to the DOM Level-3 Core documentation - which
        specifies that the Attr's children should have their
        textContent concated (Attr's can have a single child which
        is either Text node or an EntityRefrence).  In practice,
        DOM implementations do not seem to use child nodes of
        Attributes, storing the "text" directly as the nodeValue.
        Fortunately, the DOM Spec indicates that when Attr.nodeValue
        is read, it should return the nodeValue from the child Node,
        so this approach should work both for strict implementations,
        and implementations actually encountered.
     */
   case Node.TEXT_NODE: /* fall through */
   case Node.CDATA_SECTION_NODE: /* fall through */
   case Node.COMMENT_NODE: /* fall through */
   case Node.PROCESSING_INSTRUCTION_NODE: /* fall through */
     buf.append(nd.getNodeValue());
     break;

   case Node.DOCUMENT_NODE: /* fall through */
   case Node.DOCUMENT_TYPE_NODE: /* fall through */
   case Node.NOTATION_NODE: /* fall through */
   default:
     /* :NOOP: */

   }
 }

 /**
  * Replaces ${system.property[:default value]} references in all attributes
  * and text nodes of supplied node.  If the system property is not defined and no
  * default value is provided, a runtime exception is thrown.
  *
  * @param node DOM node to walk for substitutions
  */
 public static void substituteSystemProperties(Node node) {
   substituteProperties(node, null);
 }

 /**
  * Replaces ${property[:default value]} references in all attributes
  * and text nodes of supplied node.  If the property is not defined neither in the
  * given Properties instance nor in System.getProperty and no
  * default value is provided, a runtime exception is thrown.
  *
  * @param node DOM node to walk for substitutions
  * @param properties the Properties instance from which a value can be looked up
  */
 public static void substituteProperties(Node node, Properties properties) {
   // loop through child nodes
   Node child;
   Node next = node.getFirstChild();
   while ((child = next) != null) {

     // set next before we change anything
     next = child.getNextSibling();

     // handle child by node type
     if (child.getNodeType() == Node.TEXT_NODE) {
       child.setNodeValue(substituteProperty(child.getNodeValue(), properties));
     } else if (child.getNodeType() == Node.ELEMENT_NODE) {
       // handle child elements with recursive call
       NamedNodeMap attributes = child.getAttributes();
       for (int i = 0; i < attributes.getLength(); i++) {
         Node attribute = attributes.item(i);
         attribute.setNodeValue(substituteProperty(attribute.getNodeValue(), properties));
       }
       substituteProperties(child, properties);
     }
   }
 }

 /*
  * This method borrowed from Ant's PropertyHelper.replaceProperties:
  *   http://svn.apache.org/repos/asf/ant/core/trunk/src/main/org/apache/tools/ant/PropertyHelper.java
  */
 public static String substituteProperty(String value, Properties coreProperties) {
   if (value == null || value.indexOf('$') == -1) {
     return value;
   }

   List<String> fragments = new ArrayList<String>();
   List<String> propertyRefs = new ArrayList<String>();
   parsePropertyString(value, fragments, propertyRefs);

   StringBuilder sb = new StringBuilder();
   Iterator<String> i = fragments.iterator();
   Iterator<String> j = propertyRefs.iterator();

   while (i.hasNext()) {
     String fragment = i.next();
     if (fragment == null) {
       String propertyName = j.next();
       String defaultValue = null;
       int colon_index = propertyName.indexOf(':');
       if (colon_index > -1) {
         defaultValue = propertyName.substring(colon_index + 1);
         propertyName = propertyName.substring(0,colon_index);
       }
       if (coreProperties != null) {
         fragment = coreProperties.getProperty(propertyName);
       }
       if (fragment == null) {
         fragment = System.getProperty(propertyName, defaultValue);
       }
       if (fragment == null) {
         throw new RuntimeException("No system property or default value specified for " + propertyName + " value:" + value);
       }
     }
     sb.append(fragment);
   }
   return sb.toString();
 }

 /*
  * This method borrowed from Ant's PropertyHelper.parsePropertyStringDefault:
  *   http://svn.apache.org/repos/asf/ant/core/trunk/src/main/org/apache/tools/ant/PropertyHelper.java
  */
 private static void parsePropertyString(String value, List<String> fragments, List<String> propertyRefs) {
     int prev = 0;
     int pos;
     //search for the next instance of $ from the 'prev' position
     while ((pos = value.indexOf("$", prev)) >= 0) {

         //if there was any text before this, add it as a fragment
         //TODO, this check could be modified to go if pos>prev;
         //seems like this current version could stick empty strings
         //into the list
         if (pos > 0) {
             fragments.add(value.substring(prev, pos));
         }
         //if we are at the end of the string, we tack on a $
         //then move past it
         if (pos == (value.length() - 1)) {
             fragments.add("$");
             prev = pos + 1;
         } else if (value.charAt(pos + 1) != '{') {
             //peek ahead to see if the next char is a property or not
             //not a property: insert the char as a literal
             /*
             fragments.addElement(value.substring(pos + 1, pos + 2));
             prev = pos + 2;
             */
             if (value.charAt(pos + 1) == '$') {
                 //backwards compatibility two $ map to one mode
                 fragments.add("$");
                 prev = pos + 2;
             } else {
                 //new behaviour: $X maps to $X for all values of X!='$'
                 fragments.add(value.substring(pos, pos + 2));
                 prev = pos + 2;
             }

         } else {
             //property found, extract its name or bail on a typo
             int endName = value.indexOf('}', pos);
             if (endName < 0) {
               throw new RuntimeException("Syntax error in property: " + value);
             }
             String propertyName = value.substring(pos + 2, endName);
             fragments.add(null);
             propertyRefs.add(propertyName);
             prev = endName + 1;
         }
     }
     //no more $ signs found
     //if there is any tail to the string, append it
     if (prev < value.length()) {
         fragments.add(value.substring(prev));
     }
 }

}
