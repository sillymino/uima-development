package org.barcelonamedia.uima.analysis_engine.configurableAnnotator.types;

import java.util.*;

public class Document {
    
    List<Field> fields = new ArrayList<Field>();
    
    /** Constructs a new document with no fields. */
    public Document() {}

    /**
     * <p>Adds a field to a document.  Several fields may be added with
     * the same name.</p>
     */
    public void add(Field field) {
        fields.add(field);        
    }
    
    /** Returns a List of all the fields in a document.
     */
    public final List<Field> getFields() {
      return fields;
    }
    
    /**
     * Returns the string value of the field with the given name if any exist in
     * this document, or null.  If multiple fields exist with this name, this
     * method returns the first value added.
     */
    public String get(String name) {
        for (int i = 0; i < fields.size(); i++) {
            Field field = (Field)fields.get(i);
            if (field.name().equals(name))
              return field.stringValue();
          }
          return null;        
    }
    
    /**
     * Returns a field with the given name if any exist in this document, or
     * null.  If multiple fields exists with this name, this method returns the
     * first value added.
     */
    Field getField(String name) {
        for (int i = 0; i < fields.size(); i++) {
            Field field = (Field)fields.get(i);
            if (field.name().equals(name))
              return field;
          }
          return null;        
    }
    
    private final static Field[] NO_FIELDS = new Field[0];
    
    /**
     * Returns an array of {@link Field}s with the given name.
     * This method returns an empty array when there are no
     * matching fields.  It never returns null.
     *
     * @param name the name of the field
     * @return a <code>Field[]</code> array
     */
    public Field[] getFields(String name) {
        List<Field> result = new ArrayList<Field>();
        for (int i = 0; i < fields.size(); i++) {
          Field field = (Field)fields.get(i);
          if (field.name().equals(name)) {
            result.add(field);
          }
        }

        if (result.size() == 0)
          return NO_FIELDS;

        return (Field[])result.toArray(new Field[result.size()]);
    }
    
    private final static String[] NO_STRINGS = new String[0];

    /**
     * Returns an array of values of the field specified as the method parameter.
     * This method returns an empty array when there are no
     * matching fields.  It never returns null.
     * @param name the name of the field
     * @return a <code>String[]</code> of field values
     */
    public final String[] getValues(String name) {
      List<String> result = new ArrayList<String>();
      for (int i = 0; i < fields.size(); i++) {
        Field field = (Field)fields.get(i);
        if (field.name().equals(name))
          result.add(field.stringValue());
      }
      
      if (result.size() == 0)
        return NO_STRINGS;
      
      return (String[])result.toArray(new String[result.size()]);
    }
    
    /**
     * <p>Removes field with the specified name from the document.
     * If multiple fields exist with this name, this method removes the first field that has been added.
     * If there is no field with the specified name, the document remains unchanged.</p>
     */
    void removeField(String name) {
        Iterator<Field> it = fields.iterator();
        while (it.hasNext()) {
          Field field = it.next();
          if (field.name().equals(name)) {
            it.remove();
            return;
          }
        }        
    }
    
    /**
     * <p>Removes all fields with the given name from the document.
     * If there is no field with the specified name, the document remains unchanged.</p>
    **/
    void removeFields(String name) {
        Iterator<Field> it = fields.iterator();
        while (it.hasNext()) {
          Field field = (Field)it.next();
          if (field.name().equals(name)) {
            it.remove();
          }
        }        
    }
}
