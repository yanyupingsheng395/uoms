/**
 * Autogenerated by Thrift Compiler (0.11.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.linksteady.operate.thrift;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.11.0)", date = "2019-12-10")
public class RetentionData implements org.apache.thrift.TBase<RetentionData, RetentionData._Fields>, java.io.Serializable, Cloneable, Comparable<RetentionData> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("RetentionData");

  private static final org.apache.thrift.protocol.TField RETENTION_FIT_FIELD_DESC = new org.apache.thrift.protocol.TField("retentionFit", org.apache.thrift.protocol.TType.LIST, (short)1);
  private static final org.apache.thrift.protocol.TField RETENTION_CHANGE_FIT_FIELD_DESC = new org.apache.thrift.protocol.TField("retentionChangeFit", org.apache.thrift.protocol.TType.LIST, (short)2);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new RetentionDataStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new RetentionDataTupleSchemeFactory();

  public java.util.List<Double> retentionFit; // required
  public java.util.List<Double> retentionChangeFit; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    RETENTION_FIT((short)1, "retentionFit"),
    RETENTION_CHANGE_FIT((short)2, "retentionChangeFit");

    private static final java.util.Map<String, _Fields> byName = new java.util.HashMap<String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // RETENTION_FIT
          return RETENTION_FIT;
        case 2: // RETENTION_CHANGE_FIT
          return RETENTION_CHANGE_FIT;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.RETENTION_FIT, new org.apache.thrift.meta_data.FieldMetaData("retentionFit", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE))));
    tmpMap.put(_Fields.RETENTION_CHANGE_FIT, new org.apache.thrift.meta_data.FieldMetaData("retentionChangeFit", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.DOUBLE))));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(RetentionData.class, metaDataMap);
  }

  public RetentionData() {
  }

  public RetentionData(
    java.util.List<Double> retentionFit,
    java.util.List<Double> retentionChangeFit)
  {
    this();
    this.retentionFit = retentionFit;
    this.retentionChangeFit = retentionChangeFit;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public RetentionData(RetentionData other) {
    if (other.isSetRetentionFit()) {
      java.util.List<Double> __this__retentionFit = new java.util.ArrayList<Double>(other.retentionFit);
      this.retentionFit = __this__retentionFit;
    }
    if (other.isSetRetentionChangeFit()) {
      java.util.List<Double> __this__retentionChangeFit = new java.util.ArrayList<Double>(other.retentionChangeFit);
      this.retentionChangeFit = __this__retentionChangeFit;
    }
  }

  public RetentionData deepCopy() {
    return new RetentionData(this);
  }

  @Override
  public void clear() {
    this.retentionFit = null;
    this.retentionChangeFit = null;
  }

  public int getRetentionFitSize() {
    return (this.retentionFit == null) ? 0 : this.retentionFit.size();
  }

  public java.util.Iterator<Double> getRetentionFitIterator() {
    return (this.retentionFit == null) ? null : this.retentionFit.iterator();
  }

  public void addToRetentionFit(double elem) {
    if (this.retentionFit == null) {
      this.retentionFit = new java.util.ArrayList<Double>();
    }
    this.retentionFit.add(elem);
  }

  public java.util.List<Double> getRetentionFit() {
    return this.retentionFit;
  }

  public RetentionData setRetentionFit(java.util.List<Double> retentionFit) {
    this.retentionFit = retentionFit;
    return this;
  }

  public void unsetRetentionFit() {
    this.retentionFit = null;
  }

  /** Returns true if field retentionFit is set (has been assigned a value) and false otherwise */
  public boolean isSetRetentionFit() {
    return this.retentionFit != null;
  }

  public void setRetentionFitIsSet(boolean value) {
    if (!value) {
      this.retentionFit = null;
    }
  }

  public int getRetentionChangeFitSize() {
    return (this.retentionChangeFit == null) ? 0 : this.retentionChangeFit.size();
  }

  public java.util.Iterator<Double> getRetentionChangeFitIterator() {
    return (this.retentionChangeFit == null) ? null : this.retentionChangeFit.iterator();
  }

  public void addToRetentionChangeFit(double elem) {
    if (this.retentionChangeFit == null) {
      this.retentionChangeFit = new java.util.ArrayList<Double>();
    }
    this.retentionChangeFit.add(elem);
  }

  public java.util.List<Double> getRetentionChangeFit() {
    return this.retentionChangeFit;
  }

  public RetentionData setRetentionChangeFit(java.util.List<Double> retentionChangeFit) {
    this.retentionChangeFit = retentionChangeFit;
    return this;
  }

  public void unsetRetentionChangeFit() {
    this.retentionChangeFit = null;
  }

  /** Returns true if field retentionChangeFit is set (has been assigned a value) and false otherwise */
  public boolean isSetRetentionChangeFit() {
    return this.retentionChangeFit != null;
  }

  public void setRetentionChangeFitIsSet(boolean value) {
    if (!value) {
      this.retentionChangeFit = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case RETENTION_FIT:
      if (value == null) {
        unsetRetentionFit();
      } else {
        setRetentionFit((java.util.List<Double>)value);
      }
      break;

    case RETENTION_CHANGE_FIT:
      if (value == null) {
        unsetRetentionChangeFit();
      } else {
        setRetentionChangeFit((java.util.List<Double>)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case RETENTION_FIT:
      return getRetentionFit();

    case RETENTION_CHANGE_FIT:
      return getRetentionChangeFit();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case RETENTION_FIT:
      return isSetRetentionFit();
    case RETENTION_CHANGE_FIT:
      return isSetRetentionChangeFit();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof RetentionData)
      return this.equals((RetentionData)that);
    return false;
  }

  public boolean equals(RetentionData that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_retentionFit = true && this.isSetRetentionFit();
    boolean that_present_retentionFit = true && that.isSetRetentionFit();
    if (this_present_retentionFit || that_present_retentionFit) {
      if (!(this_present_retentionFit && that_present_retentionFit))
        return false;
      if (!this.retentionFit.equals(that.retentionFit))
        return false;
    }

    boolean this_present_retentionChangeFit = true && this.isSetRetentionChangeFit();
    boolean that_present_retentionChangeFit = true && that.isSetRetentionChangeFit();
    if (this_present_retentionChangeFit || that_present_retentionChangeFit) {
      if (!(this_present_retentionChangeFit && that_present_retentionChangeFit))
        return false;
      if (!this.retentionChangeFit.equals(that.retentionChangeFit))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetRetentionFit()) ? 131071 : 524287);
    if (isSetRetentionFit())
      hashCode = hashCode * 8191 + retentionFit.hashCode();

    hashCode = hashCode * 8191 + ((isSetRetentionChangeFit()) ? 131071 : 524287);
    if (isSetRetentionChangeFit())
      hashCode = hashCode * 8191 + retentionChangeFit.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(RetentionData other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetRetentionFit()).compareTo(other.isSetRetentionFit());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRetentionFit()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.retentionFit, other.retentionFit);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetRetentionChangeFit()).compareTo(other.isSetRetentionChangeFit());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRetentionChangeFit()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.retentionChangeFit, other.retentionChangeFit);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("RetentionData(");
    boolean first = true;

    sb.append("retentionFit:");
    if (this.retentionFit == null) {
      sb.append("null");
    } else {
      sb.append(this.retentionFit);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("retentionChangeFit:");
    if (this.retentionChangeFit == null) {
      sb.append("null");
    } else {
      sb.append(this.retentionChangeFit);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class RetentionDataStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public RetentionDataStandardScheme getScheme() {
      return new RetentionDataStandardScheme();
    }
  }

  private static class RetentionDataStandardScheme extends org.apache.thrift.scheme.StandardScheme<RetentionData> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, RetentionData struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // RETENTION_FIT
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list0 = iprot.readListBegin();
                struct.retentionFit = new java.util.ArrayList<Double>(_list0.size);
                double _elem1;
                for (int _i2 = 0; _i2 < _list0.size; ++_i2)
                {
                  _elem1 = iprot.readDouble();
                  struct.retentionFit.add(_elem1);
                }
                iprot.readListEnd();
              }
              struct.setRetentionFitIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // RETENTION_CHANGE_FIT
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list3 = iprot.readListBegin();
                struct.retentionChangeFit = new java.util.ArrayList<Double>(_list3.size);
                double _elem4;
                for (int _i5 = 0; _i5 < _list3.size; ++_i5)
                {
                  _elem4 = iprot.readDouble();
                  struct.retentionChangeFit.add(_elem4);
                }
                iprot.readListEnd();
              }
              struct.setRetentionChangeFitIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, RetentionData struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.retentionFit != null) {
        oprot.writeFieldBegin(RETENTION_FIT_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.DOUBLE, struct.retentionFit.size()));
          for (double _iter6 : struct.retentionFit)
          {
            oprot.writeDouble(_iter6);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      if (struct.retentionChangeFit != null) {
        oprot.writeFieldBegin(RETENTION_CHANGE_FIT_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.DOUBLE, struct.retentionChangeFit.size()));
          for (double _iter7 : struct.retentionChangeFit)
          {
            oprot.writeDouble(_iter7);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class RetentionDataTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public RetentionDataTupleScheme getScheme() {
      return new RetentionDataTupleScheme();
    }
  }

  private static class RetentionDataTupleScheme extends org.apache.thrift.scheme.TupleScheme<RetentionData> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, RetentionData struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetRetentionFit()) {
        optionals.set(0);
      }
      if (struct.isSetRetentionChangeFit()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetRetentionFit()) {
        {
          oprot.writeI32(struct.retentionFit.size());
          for (double _iter8 : struct.retentionFit)
          {
            oprot.writeDouble(_iter8);
          }
        }
      }
      if (struct.isSetRetentionChangeFit()) {
        {
          oprot.writeI32(struct.retentionChangeFit.size());
          for (double _iter9 : struct.retentionChangeFit)
          {
            oprot.writeDouble(_iter9);
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, RetentionData struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        {
          org.apache.thrift.protocol.TList _list10 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.DOUBLE, iprot.readI32());
          struct.retentionFit = new java.util.ArrayList<Double>(_list10.size);
          double _elem11;
          for (int _i12 = 0; _i12 < _list10.size; ++_i12)
          {
            _elem11 = iprot.readDouble();
            struct.retentionFit.add(_elem11);
          }
        }
        struct.setRetentionFitIsSet(true);
      }
      if (incoming.get(1)) {
        {
          org.apache.thrift.protocol.TList _list13 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.DOUBLE, iprot.readI32());
          struct.retentionChangeFit = new java.util.ArrayList<Double>(_list13.size);
          double _elem14;
          for (int _i15 = 0; _i15 < _list13.size; ++_i15)
          {
            _elem14 = iprot.readDouble();
            struct.retentionChangeFit.add(_elem14);
          }
        }
        struct.setRetentionChangeFitIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

